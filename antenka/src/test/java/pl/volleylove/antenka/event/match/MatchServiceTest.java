package pl.volleylove.antenka.event.match;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import pl.volleylove.antenka.entity.*;
import pl.volleylove.antenka.enums.FindMatchInfo;
import pl.volleylove.antenka.enums.Gender;
import pl.volleylove.antenka.enums.Level;
import pl.volleylove.antenka.enums.Position;
import pl.volleylove.antenka.event.AgeRange;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static pl.volleylove.antenka.enums.Gender.FEMALE;
import static pl.volleylove.antenka.enums.Level.ADVANCED;
import static pl.volleylove.antenka.enums.Level.BEGINNER;
import static pl.volleylove.antenka.enums.Position.LIBERO;
import static pl.volleylove.antenka.enums.SignUpInfo.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private static MatchRepository matchRepository;
    @Mock
    private static AuthService authService;
    @Mock
    private static UserService userService;
    @Mock
    private static PlayerProfileService playerProfileService;
    @Mock
    private static LocationService locationService;
    @Mock
    private static SlotService slotService;
    @Mock
    private static Errors errors;
    @InjectMocks
    private static MatchService matchService;

    private static final String PRICE = "10";
    private static final String ERROR_MSG_INCORRECT_ADDRESS = "Incorrect address";
    private static final String ERROR_MSG_USER_NOT_FOUND = "User not found";
    private static final String MATCH_NAME = "Super match";
    private static final String OK_MSG = "OK";
    private static final Long EVENT_ID = 1L;
    private static final int SLOT_ORDER_NUM_1 = 1;
    private static final int SLOT_ORDER_NUM_2 = 2;
    private static final int INCORRECT_SLOT_ORDER_NUM = 123456789;
    private static final int AGE = 18;
    private static final LocalDateTime DATE_TIME = LocalDateTime.now().plusDays(5);
    private static final PlayerProfile PLAYER_PROFILE_BEGINNER = PlayerProfile.builder()
            .playerProfileID(1L)
            .level(BEGINNER)
            .gender(FEMALE)
            .age(AGE)
            .positions(Set.of(LIBERO))
            .build();

    @Test
    void addTestAddMatchRequestHasErrors() throws IOException, InterruptedException {

        when(errors.hasErrors()).thenReturn(true);
        when(errors.getAllErrors()).thenReturn(List.of(new ObjectError("name: " + ERROR_MSG_INCORRECT_ADDRESS,
                ERROR_MSG_INCORRECT_ADDRESS)));

        AddMatchResponse expectedResponse = AddMatchResponse
                .builder()
                .addMatchInfo(List.of(ERROR_MSG_INCORRECT_ADDRESS))
                .build();

        AddMatchResponse actualResponse = matchService.add(AddMatchRequest.builder().build(), errors);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void addTestUserIsNotLoggedIn() throws IOException, InterruptedException {

        when(userService.findByID(any(long.class))).thenReturn(Optional.empty());

        AddMatchResponse expectedResponse = AddMatchResponse
                .builder()
                .addMatchInfo(List.of(ERROR_MSG_USER_NOT_FOUND))
                .build();

        assertEquals(expectedResponse, matchService.add(AddMatchRequest.builder().build(), errors));
    }

    @Test
    void addTestSuccess() throws IOException, InterruptedException {

        Match matchToAdd = Match.builder().name(MATCH_NAME).build();

        when(userService.findByID(any(long.class))).thenReturn(Optional.of(new User()));
        when(locationService.setLocationInAddress(any(Address.class))).thenReturn(new Address());
        when(matchRepository.save(any(Match.class))).thenReturn(matchToAdd);

        AddMatchResponse expectedResponse = AddMatchResponse
                .builder()
                .match(matchToAdd)
                .addMatchInfo(List.of(OK_MSG))
                .build();

        AddMatchResponse actualResponse = matchService.add(AddMatchRequest.builder().address(new Address()).build(), errors);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findTestNoPlayerProfile() throws NotAuthenticatedException {

        when(playerProfileService.getPlayerProfileOfAuthenticatedUser()).thenReturn(Optional.empty());

        FindMatchResponse expectedResponse = FindMatchResponse
                .builder()
                .findMatchInfo(FindMatchInfo.COMPLETE_PLAYER_PROFILE)
                .build();

        FindMatchResponse actualResponse = matchService.findMatch(new FindMatchRequest(PRICE));

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findTestPlayerWithBenefit() throws NotAuthenticatedException {

        when(playerProfileService.getPlayerProfileOfAuthenticatedUser())
                .thenReturn(Optional.of(PlayerProfile.builder()
                                .gender(FEMALE)
                                .age(25)
                                .level(BEGINNER)
                                .positions(Set.of(Position.SETTER))
                        .activeBenefit(true)
                        .build()));

        when(matchRepository.findByPlayerWantedBenefit(any(Gender.class), any(Level.class), anyInt(), anySet(), any(BigDecimal.class))).thenReturn(new HashSet<>());

        FindMatchResponse expectedResponse = FindMatchResponse.builder()
                .matches(new HashSet<>())
                .findMatchInfo(FindMatchInfo.OK)
                .build();

        FindMatchResponse actualResponse = matchService.findMatch(new FindMatchRequest(PRICE));

        assertEquals(expectedResponse, actualResponse);

        verify(matchRepository, times(1)).findByPlayerWantedBenefit(any(Gender.class), any(Level.class), anyInt(), anySet(), any(BigDecimal.class));
        verify(matchRepository, times(0)).findByPlayerWantedNoBenefit(any(Gender.class), any(Level.class), anyInt(), anySet(), any(BigDecimal.class));
    }

    @Test
    void findTestPlayerWithoutBenefit() throws NotAuthenticatedException {

        when(playerProfileService.getPlayerProfileOfAuthenticatedUser())
                .thenReturn(Optional.of(PlayerProfile.builder()
                        .gender(FEMALE)
                        .age(25)
                        .level(BEGINNER)
                        .positions(Set.of(Position.SETTER))
                        .activeBenefit(false)
                        .build()));

        when(matchRepository.findByPlayerWantedNoBenefit(any(Gender.class), any(Level.class), anyInt(), anySet(), any(BigDecimal.class))).thenReturn(new HashSet<>());

        FindMatchResponse expectedResponse = FindMatchResponse.builder()
                .matches(new HashSet<>())
                .findMatchInfo(FindMatchInfo.OK)
                .build();

        FindMatchResponse actualResponse = matchService.findMatch(new FindMatchRequest(PRICE));

        assertEquals(expectedResponse, actualResponse);

        verify(matchRepository, times(0)).findByPlayerWantedBenefit(any(Gender.class), any(Level.class), anyInt(), anySet(), any(BigDecimal.class));
        verify(matchRepository, times(1)).findByPlayerWantedNoBenefit(any(Gender.class), any(Level.class), anyInt(), anySet(), any(BigDecimal.class));
    }

    @Test
    void signUpForMatchTestUserNotLoggedIn() {

        when(userService.findByID(any(long.class))).thenThrow(NoSuchElementException.class);

        SignUpForMatchResponse expectedResponse = SignUpForMatchResponse.builder()
                .info(YOU_ARE_NOT_LOGGED_IN)
                .build();

        SignUpForMatchResponse response = matchService.signUpForMatch(new SignUpForMatchRequest(EVENT_ID, SLOT_ORDER_NUM_1));

        assertEquals(expectedResponse, response);

        verify(userService, times(1)).findByID(any(long.class));
        verify(playerProfileService, times(0)).getPlayerProfile(any(User.class));
        verify(matchRepository, times(0)).findById(any(long.class));
        verify(matchRepository, times(0)).save(any(Match.class));

    }

    @Test
    void signUpForMatchTestUserWithoutPlayerProfile() {

        when(userService.findByID(any(long.class))).thenReturn(Optional.of(new User()));
        when(playerProfileService.getPlayerProfile(any(User.class))).thenThrow(NoSuchElementException.class);

        SignUpForMatchResponse expectedResponse = SignUpForMatchResponse.builder()
                .info(COMPLETE_PLAYER_OR_TEAM_PROFILE)
                .build();

        SignUpForMatchResponse response = matchService.signUpForMatch(new SignUpForMatchRequest(EVENT_ID, SLOT_ORDER_NUM_1));

        assertEquals(expectedResponse, response);

        verify(userService, times(1)).findByID(any(long.class));
        verify(playerProfileService, times(1)).getPlayerProfile(any(User.class));
        verify(matchRepository, times(0)).findById(any(long.class));
        verify(matchRepository, times(0)).save(any(Match.class));
    }

    @Test
    void signUpForMatchTestIncorrectMatchId() {

        when(userService.findByID(any(long.class))).thenReturn(Optional.of(new User()));
        when(playerProfileService.getPlayerProfile(any(User.class))).thenReturn(Optional.of(new PlayerProfile()));

        SignUpForMatchResponse expectedResponse = SignUpForMatchResponse.builder()
                .info(INCORRECT_EVENT_ID)
                .build();

        SignUpForMatchResponse response = matchService.signUpForMatch(new SignUpForMatchRequest(EVENT_ID, SLOT_ORDER_NUM_1));

        assertEquals(expectedResponse, response);

        verify(userService, times(1)).findByID(any(long.class));
        verify(playerProfileService, times(1)).getPlayerProfile(any(User.class));
        verify(matchRepository, times(1)).findById(any(long.class));
        verify(matchRepository, times(0)).save(any(Match.class));
    }

    @Test
    void signUpForMatchTestIncorrectSlotOrderNum() {

        when(userService.findByID(any(long.class))).thenReturn(Optional.of(new User()));
        when(playerProfileService.getPlayerProfile(any(User.class))).thenReturn(Optional.of(new PlayerProfile()));

        when(matchRepository.findById(any(long.class))).thenReturn(
                Optional.of(Match.builder()
                        .eventID(EVENT_ID)
                        .slots(Set.of(Slot.builder()
                                .orderNum(SLOT_ORDER_NUM_1)
                                .build()))
                        .build()));


        SignUpForMatchResponse expectedResponse = SignUpForMatchResponse.builder()
                .info(INCORRECT_SLOT_NUM)
                .build();

        SignUpForMatchResponse response = matchService.signUpForMatch(new SignUpForMatchRequest(EVENT_ID, INCORRECT_SLOT_ORDER_NUM));

        assertEquals(expectedResponse, response);


        verify(userService, times(1)).findByID(any(long.class));
        verify(playerProfileService, times(1)).getPlayerProfile(any(User.class));
        verify(matchRepository, times(1)).findById(any(long.class));
        verify(matchRepository, times(0)).save(any(Match.class));
    }

    @Test
    void signUpForMatchTestMatchIsNotActive() {

        when(userService.findByID(any(long.class))).thenReturn(Optional.of(new User()));
        when(playerProfileService.getPlayerProfile(any(User.class))).thenReturn(Optional.of(new PlayerProfile()));

        when(matchRepository.findById(any(long.class))).thenReturn(
                Optional.of(Match.builder()
                        .eventID(EVENT_ID)
                        .isActive(false)
                        .slots(Set.of(Slot.builder()
                                .orderNum(SLOT_ORDER_NUM_1)
                                .build()))
                        .build()));


        SignUpForMatchResponse expectedResponse = SignUpForMatchResponse.builder()
                .info(EVENT_IS_CLOSED)
                .build();

        SignUpForMatchResponse response = matchService.signUpForMatch(new SignUpForMatchRequest(EVENT_ID, SLOT_ORDER_NUM_1));

        assertEquals(expectedResponse, response);

        verify(userService, times(1)).findByID(any(long.class));
        verify(playerProfileService, times(1)).getPlayerProfile(any(User.class));
        verify(matchRepository, times(1)).findById(any(long.class));
        verify(matchRepository, times(0)).save(any(Match.class));
    }

    @Test
    void signUpForMatchTestSigningUpIsFalseNoFreeSlots() {

        when(userService.findByID(any(long.class))).thenReturn(Optional.of(new User()));
        when(playerProfileService.getPlayerProfile(any(User.class))).thenReturn(Optional.of(new PlayerProfile()));

        when(matchRepository.findById(any(long.class))).thenReturn(
                Optional.of(Match.builder()
                        .eventID(EVENT_ID)
                        .dateTime(DATE_TIME)
                        .isActive(true) //Match is active, but all its slots have PlayerApplied
                        .slots(Set.of(Slot.builder()
                                .orderNum(SLOT_ORDER_NUM_1)
                                .playerApplied(new PlayerProfile()) //Match has 1 Slot and this Slot has Player, who applied on this Slot - no empty slots
                                .build()))
                        .build()));


        SignUpForMatchResponse expectedResponse = SignUpForMatchResponse.builder()
                .info(EVENT_HAS_NO_FREE_SLOTS_LEFT)
                .build();

        SignUpForMatchResponse response = matchService.signUpForMatch(new SignUpForMatchRequest(EVENT_ID, SLOT_ORDER_NUM_1));

        assertEquals(expectedResponse, response);

        verify(userService, times(1)).findByID(any(long.class));
        verify(playerProfileService, times(1)).getPlayerProfile(any(User.class));
        verify(matchRepository, times(1)).findById(any(long.class));
        verify(matchRepository, times(0)).save(any(Match.class));
    }

    @Test //Match has empty Slots, but User tries to sign up for slot, which has player already
    void signUpForMatchTestSlotHasPlayer() {

        when(userService.findByID(any(long.class))).thenReturn(Optional.of(new User()));
        when(playerProfileService.getPlayerProfile(any(User.class))).thenReturn(Optional.of(new PlayerProfile()));

        when(matchRepository.findById(any(long.class))).thenAnswer(a -> {

            Slot slotWithPlayer = Slot.builder()
                    .orderNum(SLOT_ORDER_NUM_1)
                    .playerApplied(new PlayerProfile())
                    .build();

            Slot slotWithoutPlayer = Slot.builder()
                    .orderNum(SLOT_ORDER_NUM_2)
                    .playerApplied(null)
                    .build();

            return Optional.of(Match.builder()
                    .eventID(EVENT_ID)
                    .dateTime(DATE_TIME)
                    .isActive(true) //Match is active, but all its slots have PlayerApplied
                    .slots(Set.of(slotWithPlayer, slotWithoutPlayer))
                    .build());
        });


        SignUpForMatchResponse expectedResponse = SignUpForMatchResponse.builder()
                .info(SLOT_HAS_PLAYER_ALREADY)
                .build();

        SignUpForMatchResponse response = matchService.signUpForMatch(new SignUpForMatchRequest(EVENT_ID, SLOT_ORDER_NUM_1));

        assertEquals(expectedResponse, response);

        verify(userService, times(1)).findByID(any(long.class));
        verify(playerProfileService, times(1)).getPlayerProfile(any(User.class));
        verify(matchRepository, times(1)).findById(any(long.class));
        verify(matchRepository, times(0)).save(any(Match.class));
    }

    @Test //Match has empty Slots, but User tries to sign up for slot, which has Player already
    void signUpForMatchTestPlayerIsSignedUpForThisMatch() {

        when(userService.findByID(any(long.class))).thenReturn(Optional.of(new User()));
        when(playerProfileService.getPlayerProfile(any(User.class))).thenReturn(Optional.of(PLAYER_PROFILE_BEGINNER));

        when(matchRepository.findById(any(long.class))).thenAnswer(a -> {

            Slot slotWithPlayer = Slot.builder()
                    .orderNum(SLOT_ORDER_NUM_1)
                    .playerApplied(PLAYER_PROFILE_BEGINNER)
                    .build();

            Slot slotWithoutPlayer = Slot.builder()
                    .orderNum(SLOT_ORDER_NUM_2)
                    .playerApplied(null)
                    .build();

            return Optional.of(Match.builder()
                    .eventID(EVENT_ID)
                    .dateTime(DATE_TIME)
                    .isActive(true) //Match is active, but all its slots have PlayerApplied
                    .slots(Set.of(slotWithPlayer, slotWithoutPlayer))
                    .build());
        });


        SignUpForMatchResponse expectedResponse = SignUpForMatchResponse.builder()
                .info(YOU_ARE_SINGED_UP_FOR_THIS_EVENT)
                .build();

        SignUpForMatchResponse response = matchService.signUpForMatch(new SignUpForMatchRequest(EVENT_ID, SLOT_ORDER_NUM_2));

        assertEquals(expectedResponse, response);

        verify(userService, times(1)).findByID(any(long.class));
        verify(playerProfileService, times(1)).getPlayerProfile(any(User.class));
        verify(matchRepository, times(1)).findById(any(long.class));
        verify(matchRepository, times(0)).save(any(Match.class));
    }

    @Test
    void signUpForMatchTestPlayerDoesNotMeetRequirements() {

        when(userService.findByID(any(long.class))).thenReturn(Optional.of(new User()));
        when(playerProfileService.getPlayerProfile(any(User.class))).thenReturn(Optional.of(PLAYER_PROFILE_BEGINNER));

        when(matchRepository.findById(any(long.class))).thenAnswer(a -> {

            Slot slot = Slot.builder()
                    .playerWanted(PlayerWanted.builder().level(ADVANCED).build())
                    .orderNum(SLOT_ORDER_NUM_1)
                    .build();


            return Optional.of(Match.builder()
                    .eventID(EVENT_ID)
                    .dateTime(DATE_TIME)
                    .isActive(true) //Match is active, but all its slots have PlayerApplied
                    .slots(Set.of(slot))
                    .build());
        });


        SignUpForMatchResponse expectedResponse = SignUpForMatchResponse.builder()
                .info(YOU_DO_NOT_MEET_EVENT_REQ)
                .build();

        SignUpForMatchResponse response = matchService.signUpForMatch(new SignUpForMatchRequest(EVENT_ID, SLOT_ORDER_NUM_1));

        assertEquals(expectedResponse, response);

        verify(userService, times(1)).findByID(any(long.class));
        verify(playerProfileService, times(1)).getPlayerProfile(any(User.class));
        verify(matchRepository, times(1)).findById(any(long.class));
        verify(matchRepository, times(0)).save(any(Match.class));
    }

    @Test //signed up is successful and after that 1 free slot left
    void signUpForMatchTestOkFreeSlotLeft() {

        when(userService.findByID(any(long.class))).thenReturn(Optional.of(new User()));
        when(playerProfileService.getPlayerProfile(any(User.class))).thenReturn(Optional.of(PLAYER_PROFILE_BEGINNER));

        //slot to sign up
        Slot slot1 = Slot.builder()
                .playerWanted(PlayerWanted.builder()
                        .level(BEGINNER)
                        .gender(FEMALE)
                        .position(LIBERO)
                        .ageRange(new AgeRange(AGE, AGE))
                        .build())
                .orderNum(SLOT_ORDER_NUM_1)
                .build();

        //free slot
        Slot slot2 = Slot.builder()
                .playerWanted(PlayerWanted.builder()
                        .level(BEGINNER)
                        .gender(FEMALE)
                        .position(LIBERO)
                        .ageRange(new AgeRange(AGE, AGE))
                        .build())
                .orderNum(SLOT_ORDER_NUM_2)
                .build();

        Match match = Match.builder()
                .eventID(EVENT_ID)
                .dateTime(DATE_TIME)
                .isActive(true)
                .isSigningUp(true)
                .freeSlots(2)
                .slots(Set.of(slot1, slot2))
                .build();

        slot1.setEvent(match);
        slot2.setEvent(match);

        when(matchRepository.findById(any(long.class))).thenAnswer(a -> Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        SignUpForMatchResponse expectedResponse = SignUpForMatchResponse.builder()
                .info(OK)
                .slot(slot1)
                .match(match)
                .build();

        SignUpForMatchResponse response = matchService.signUpForMatch(new SignUpForMatchRequest(EVENT_ID, SLOT_ORDER_NUM_1));

        assertEquals(expectedResponse, response);
        assertEquals(1, response.getMatch().getFreeSlots());
        assertTrue(response.getMatch().isSigningUp());


        verify(userService, times(1)).findByID(any(long.class));
        verify(playerProfileService, times(1)).getPlayerProfile(any(User.class));
        verify(matchRepository, times(1)).findById(any(long.class));
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test //it's last free slot - Match's signingUp attribute should be false
    void signUpForMatchTestOkLastFreeSlot() {

        when(userService.findByID(any(long.class))).thenReturn(Optional.of(new User()));
        when(playerProfileService.getPlayerProfile(any(User.class))).thenReturn(Optional.of(PLAYER_PROFILE_BEGINNER));

        Slot slot = Slot.builder()
                .playerWanted(PlayerWanted.builder()
                        .level(BEGINNER)
                        .gender(FEMALE)
                        .position(LIBERO)
                        .ageRange(new AgeRange(AGE, AGE))
                        .build())
                .orderNum(SLOT_ORDER_NUM_1)
                .build();

        Match match = Match.builder()
                .eventID(EVENT_ID)
                .dateTime(DATE_TIME)
                .isActive(true)
                .isSigningUp(true)
                .freeSlots(1)
                .slots(Set.of(slot))
                .build();

        slot.setEvent(match);

        when(matchRepository.findById(any(long.class))).thenAnswer(a -> Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        SignUpForMatchResponse expectedResponse = SignUpForMatchResponse.builder()
                .info(OK)
                .slot(slot)
                .match(match)
                .build();

        SignUpForMatchResponse response = matchService.signUpForMatch(new SignUpForMatchRequest(EVENT_ID, SLOT_ORDER_NUM_1));

        assertEquals(expectedResponse, response);
        assertEquals(0, response.getMatch().getFreeSlots());
        assertFalse(response.getMatch().isSigningUp());


        verify(userService, times(1)).findByID(any(long.class));
        verify(playerProfileService, times(1)).getPlayerProfile(any(User.class));
        verify(matchRepository, times(1)).findById(any(long.class));
        verify(matchRepository, times(1)).save(any(Match.class));
    }



}
