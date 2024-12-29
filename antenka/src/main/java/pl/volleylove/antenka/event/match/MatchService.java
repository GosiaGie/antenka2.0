package pl.volleylove.antenka.event.match;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import pl.volleylove.antenka.entity.*;
import pl.volleylove.antenka.enums.*;
import pl.volleylove.antenka.event.PlayerWanted;
import pl.volleylove.antenka.event.match.add.AddMatchRequest;
import pl.volleylove.antenka.event.match.add.AddMatchResponse;
import pl.volleylove.antenka.event.match.find.FindMatchRequest;
import pl.volleylove.antenka.event.match.find.FindMatchResponse;
import pl.volleylove.antenka.event.match.signup.SignUpForMatchRequest;
import pl.volleylove.antenka.event.match.signup.SignUpForMatchResponse;
import pl.volleylove.antenka.map.LocationService;
import pl.volleylove.antenka.playerprofile.PlayerProfileService;
import pl.volleylove.antenka.repository.MatchRepository;
import pl.volleylove.antenka.security.NotAuthenticatedException;
import pl.volleylove.antenka.user.UserService;
import pl.volleylove.antenka.user.auth.AuthService;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

//class for business logic
@Service
//main service class for Match
public class MatchService {

    private final MatchRepository matchRepository;
    private final AuthService authService;
    private final UserService userService;
    private final PlayerProfileService playerProfileService;
    private final LocationService locationService;
    private final SlotService slotService;

    @Autowired
    public MatchService(MatchRepository matchRepository, AuthService authService, UserService userService, PlayerProfileService playerProfileService, LocationService locationService, SlotService slotService) {
        this.matchRepository = matchRepository;
        this.authService = authService;
        this.userService = userService;
        this.playerProfileService = playerProfileService;
        this.locationService = locationService;
        this.slotService = slotService;
    }

    @Transactional
    public AddMatchResponse add(AddMatchRequest request, Errors errors) throws IOException, InterruptedException {

        if (errors.hasErrors()) {
            List<String> errList = new LinkedList<>();

            errors.getAllErrors().forEach(err -> errList.add(err.getDefaultMessage()));

            return AddMatchResponse.builder()
                    .addMatchInfo(errList)
                    .build();
        }

        //1. getting authenticated userID
        Long authenticatedUserID = authService.getAuthenticatedUserID();
        //2. after getting userID, we find user and set her/him as organizer
        User userOrganizer;
        try {
            userOrganizer = userService.findByID(authenticatedUserID).orElseThrow();
        } catch (NoSuchElementException e) {
            return AddMatchResponse.builder()
                    .addMatchInfo(List.of("User not found"))
                    .build();
        }

        //3. finding geo data and address type
        Address address = locationService.setLocationInAddress(request.getAddress());
        address.setAddressType(AddressType.EVENT);

        Match match = Match.builder()
                .organizer(userOrganizer)
                .address(address)
                .name(request.getName())
                .dateTime(request.getDateTime())
                .price(request.getPrice())
                .playersNum(request.getPlayersNum())
                .freeSlots(request.getPlayersNum()) //because at the start freeSlots = playersNum
                .isActive(true)
                .isSigningUp(true)
                .build();

        Match savedMatch = matchRepository.save(match);

        // 4. after match got ID in DB, we can save slots for players - now they can have matchID
        savedMatch.setSlots(slotService.saveSlotsInMatch(savedMatch.getEventID(), request.getPlayers()));

        return AddMatchResponse.builder()
                .addMatchInfo(List.of("OK"))
                .match(savedMatch)
                .build();

    }

    public FindMatchResponse findMatch(FindMatchRequest request) throws NotAuthenticatedException {

        PlayerProfile playerProfile;

        try {
            playerProfile = playerProfileService.getPlayerProfileOfAuthenticatedUser().orElseThrow();
        } catch (NoSuchElementException e) {
            return FindMatchResponse
                    .builder()
                    .findMatchInfo(FindMatchInfo.COMPLETE_PLAYER_PROFILE)
                    .build();
        }

        Set<Match> matches;

        if (playerProfile.isActiveBenefit()) {
            matches = matchRepository.findByPlayerWantedBenefit(
                    playerProfile.getGender(),
                    playerProfile.getLevel(),
                    playerProfile.getAge(),
                    playerProfile.getPositions(),
                    request.getMaxPrice());
        } else {
            matches = matchRepository.findByPlayerWantedNoBenefit(
                    playerProfile.getGender(),
                    playerProfile.getLevel(),
                    playerProfile.getAge(),
                    playerProfile.getPositions(),
                    request.getMaxPrice());
        }

        return FindMatchResponse.builder()
                .findMatchInfo(FindMatchInfo.OK)
                .matches(matches)
                .build();
    }

    @Transactional
    public SignUpForMatchResponse signUpForMatch(SignUpForMatchRequest request) {

        User user;
        Match match;
        Slot slotToSignUp;
        PlayerProfile player;

        try {
            user = userService.findByID(authService.getAuthenticatedUserID()).orElseThrow();
        } catch (NoSuchElementException e) {
            return SignUpForMatchResponse.builder()
                    .info(SignUpInfo.YOU_ARE_NOT_LOGGED_IN)
                    .build();
        }

        //1. checking if the user has PlayerProfile - only users with player profile can apply for a match
        try {
            player = playerProfileService.getPlayerProfile(user).orElseThrow();
        } catch (NoSuchElementException e) {
            return SignUpForMatchResponse.builder()
                    .info(SignUpInfo.COMPLETE_PLAYER_OR_TEAM_PROFILE)
                    .build();
        }

        //2. checking if this Match exists
        try {
            match = matchRepository.findById(request.getEventID()).orElseThrow();
        } catch (NoSuchElementException e) {
            return SignUpForMatchResponse.builder()
                    .info(SignUpInfo.INCORRECT_EVENT_ID)
                    .build();
        }

        //3. checking if slot with this orderNum exists
        try {
            slotToSignUp = match.getSlots().stream().filter(s -> s.getOrderNum() == request.getOrderNum()).findAny().orElseThrow();
        } catch (NoSuchElementException e) {
            return SignUpForMatchResponse.builder()
                    .info(SignUpInfo.INCORRECT_SLOT_NUM)
                    .build();
        }

        //4. checking if the match is active
        // I) organizer didn't close it
        // II) match is not in the past
        if (!match.isActive()) {
            return SignUpForMatchResponse.builder()
                    .info(SignUpInfo.EVENT_IS_CLOSED)
                    .build();
        }

        //5.checking if match has any free slot left
        if (!match.isSigningUp()) {
            return SignUpForMatchResponse.builder()
                    .info(SignUpInfo.EVENT_HAS_NO_FREE_SLOTS_LEFT)
                    .build();
        }

        //6. checking if slot does not have player already
        if (slotToSignUp.getPlayerApplied() != null) {
            return SignUpForMatchResponse.builder()
                    .info(SignUpInfo.SLOT_HAS_PLAYER_ALREADY)
                    .build();
        }

        //7. for every match player can only sign up once
        if (isPlayerSignUpForThisMatch(match, player)) {
            return SignUpForMatchResponse.builder()
                    .info(SignUpInfo.YOU_ARE_SINGED_UP_FOR_THIS_EVENT)
                    .build();
        }

        //7. checking if this user, with this player profile can sign in for this match's slot
        //comparing player's profile with player wanted for this slot
        //*level
        //*gender
        //*age
        //*positions

        if (!isPlayerMeetReq(player, slotToSignUp.getPlayerWanted())) {
            return SignUpForMatchResponse.builder()
                    .info(SignUpInfo.YOU_DO_NOT_MEET_EVENT_REQ)
                    .build();
        }

        //setting player to slot
        slotToSignUp.setPlayerApplied(player);

        // changing the number of free slots -1
        match.setFreeSlots(match.getFreeSlots() - 1);

        //if it's last free slot, then close a match for signing up
        if (match.getFreeSlots() == 0) {
            match.setSigningUpEndReason(SigningUpEndReason.OUT_OF_FREE_SLOTS);
            match.setSigningUp(false);
        }

        return SignUpForMatchResponse.builder()
                .info(SignUpInfo.OK)
                .match(matchRepository.save(match))
                .slot(slotToSignUp)
                .build();
    }

    public boolean isPlayerSignUpForThisMatch(Match match, PlayerProfile playerProfile) {
        return match.getSlots().stream().anyMatch(slot -> playerProfile.equals(slot.getPlayerApplied()));
    }

    public boolean isPlayerMeetReq(PlayerProfile playerProfile, PlayerWanted playerWanted) {

        return playerWanted.getLevel() == playerProfile.getLevel() //level
                && playerWanted.getGender() == playerProfile.getGender() //gender
                && playerProfile.getPositions().contains(playerWanted.getPosition()) //position
                && playerWanted.getAgeRange().getAgeMin() <= playerProfile.getAge()  //age
                && playerWanted.getAgeRange().getAgeMax() >= playerProfile.getAge();

    }

}
