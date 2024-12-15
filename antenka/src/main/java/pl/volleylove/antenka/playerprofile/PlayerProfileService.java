package pl.volleylove.antenka.playerprofile;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.volleylove.antenka.benefit.BenefitService;
import pl.volleylove.antenka.entity.PlayerProfile;
import pl.volleylove.antenka.entity.User;
import pl.volleylove.antenka.enums.PlayerProfileInfo;
import pl.volleylove.antenka.repository.PlayerProfileRepository;
import pl.volleylove.antenka.security.NotAuthenticatedException;
import pl.volleylove.antenka.user.UserService;
import pl.volleylove.antenka.user.auth.AuthService;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class PlayerProfileService {

    private final PlayerProfileRepository playerProfileRepository;

    private final AuthService authService;

    private final UserService userService;

    private final BenefitService benefitService;


    @Autowired
    public PlayerProfileService(PlayerProfileRepository playerProfileRepository, AuthService authService, UserService userService, BenefitService benefitService) {
        this.playerProfileRepository = playerProfileRepository;
        this.authService = authService;
        this.userService = userService;
        this.benefitService = benefitService;
    }

    @Transactional
    public PlayerProfileResponse addOrUpdateProfile(PlayerProfileRequest request) {

        //getting user's profile
        Optional<User> userResult = userService.findByID(authService.getAuthenticatedUserID());

        if (userResult.isEmpty()) {
            return PlayerProfileResponse.builder().info(PlayerProfileInfo.NOT_AUTHENTICATED).build();
        }

        //getting player's profile - if exists, otherwise creating new player's profile
        Optional<PlayerProfile> playerProfileResult = getPlayerProfile(userResult.get());

        //player can't change her/his profile if is signed up for a match
        if (playerProfileResult.isPresent() && isPlayerSignedUpForActiveEvent(playerProfileResult.get())) {
            return PlayerProfileResponse.builder()
                    .info(PlayerProfileInfo.YOU_ARE_SIGNED_UP_FOR_EVENT)
                    .build();
        }

        //if request has BenefitCardNumber, then check if it's correct
        if (request.getBenefitCardNumber() != null && !benefitService.isCorrect(request.getBenefitCardNumber())) {
            return PlayerProfileResponse.builder()
                    .info(PlayerProfileInfo.INCORRECT_BENEFIT_NUMBER)
                    .build();
        }

        PlayerProfile playerProfile = playerProfileResult.orElseGet(PlayerProfile::new);
        playerProfile.setUser(userResult.get());
        playerProfile.setPositions(request.getPositions());
        playerProfile.setLevel(request.getLevel());
        playerProfile.setGender(request.getGender());
        playerProfile.setBenefitCardNumber(request.getBenefitCardNumber());
        playerProfile.setActiveBenefit(request.getBenefitCardNumber() != null && benefitService.isActive(request.getBenefitCardNumber()));
        playerProfile.setAge((int) ChronoUnit.YEARS.between(userResult.get().getBirthday(), LocalDate.now()));


        return PlayerProfileResponse.builder()
                .info(PlayerProfileInfo.OK)
                .playerProfile(playerProfileRepository.save(playerProfile))
                .build();
    }

    public static int calculateAge(LocalDate birthDate) {

        LocalDate currentDate = LocalDate.now();

        if ((birthDate != null && birthDate.isBefore(currentDate))) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }

    public Optional<PlayerProfile> getPlayerProfile(User user) {

        Optional<PlayerProfile> playerProfile = playerProfileRepository.findByUser(user);

        if (playerProfile.isPresent()) {
            //1. setting user's age - age isn't stored in DB, only birthday
            playerProfile.get().setAge(calculateAge(user.getBirthday()));
            //2. setting user's benefit status, basing on the benefit card number; if is active, then only benefit price will be checked
            playerProfile.get().setActiveBenefit(benefitService.isActive(playerProfile.get().getBenefitCardNumber()));
        }
        return playerProfile;
    }

    public Optional<PlayerProfile> getPlayerProfileOfAuthenticatedUser() throws NotAuthenticatedException {

        //getting user's profile
        Optional<User> userResult = userService.findByID(authService.getAuthenticatedUserID());
        if (userResult.isEmpty()) {
            throw new NotAuthenticatedException("User not logged in");
        }

        Optional<PlayerProfile> playerProfile = playerProfileRepository.findByUser(userResult.get());

        if (playerProfile.isPresent()) {
            //3. setting user's age - age isn't stored in DB, only birthday
            playerProfile.get().setAge(calculateAge(userResult.get().getBirthday()));
            //4. setting user's benefit status, basing on the benefit card number; if is active, then only benefit price will be checked
            playerProfile.get().setActiveBenefit(benefitService.isActive(playerProfile.get().getBenefitCardNumber()));

        }
        return playerProfile;
    }

    public static boolean isPlayerSignedUpForActiveEvent(PlayerProfile profile) {
        return profile.getApps().stream().anyMatch(
                slot -> slot.getEvent().isActive());
    }

}
