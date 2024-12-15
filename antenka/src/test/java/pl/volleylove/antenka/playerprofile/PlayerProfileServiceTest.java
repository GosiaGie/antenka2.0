package pl.volleylove.antenka.playerprofile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.volleylove.antenka.benefit.BenefitService;
import pl.volleylove.antenka.entity.Match;
import pl.volleylove.antenka.entity.PlayerProfile;
import pl.volleylove.antenka.entity.Slot;
import pl.volleylove.antenka.entity.User;
import pl.volleylove.antenka.enums.Gender;
import pl.volleylove.antenka.enums.Level;
import pl.volleylove.antenka.enums.PlayerProfileInfo;
import pl.volleylove.antenka.enums.Position;
import pl.volleylove.antenka.repository.PlayerProfileRepository;
import pl.volleylove.antenka.security.NotAuthenticatedException;
import pl.volleylove.antenka.user.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static pl.volleylove.antenka.playerprofile.PlayerProfileService.calculateAge;

@ExtendWith(MockitoExtension.class)
class PlayerProfileServiceTest {

    @Mock
    private PlayerProfileRepository playerProfileRepository;
    @Mock
    private UserService userService;
    @Mock
    private BenefitService benefitService;

    @InjectMocks
    private PlayerProfileService playerProfileService;

    ///attributes to tests:
    private static final int ALLOWED_AGE_EXAMPLE = 18;
    private static final String BENEFIT_CARD_NUMBER = "1234567";
    private static final String NEW_BENEFIT_CARD_NUMBER = "7891011";
    private static final Level LEVEL = Level.BEGINNER;

    private User user;

    private PlayerProfileRequest request;

    @BeforeEach
    void init() {

        request = PlayerProfileRequest.builder()
                .benefitCardNumber(NEW_BENEFIT_CARD_NUMBER)
                .positions(Set.of(Position.SETTER))
                .level(LEVEL)
                .gender(Gender.FEMALE).build();
    }

    @Test
    void addOrUpdateProfileTestUpdateProfile() {

        user = User.builder().birthday(LocalDate.now().minusYears(ALLOWED_AGE_EXAMPLE)).build();
        when(userService.findByID(any(long.class))).thenReturn(Optional.of(user));

        when(playerProfileRepository.findByUser(any(User.class))).thenAnswer(a -> Optional.of(PlayerProfile.builder()
                .apps(Set.of())
                .level(LEVEL)
                .benefitCardNumber(BENEFIT_CARD_NUMBER).build()));

        when(benefitService.isCorrect(any(String.class))).thenReturn(true);

        when(playerProfileRepository.save(any(PlayerProfile.class))).thenReturn(
                PlayerProfile.builder()
                        .user(user)
                        .level(LEVEL)
                        .build());


        PlayerProfileResponse response = playerProfileService.addOrUpdateProfile(request);

        assertEquals(PlayerProfileInfo.OK, response.getInfo());
        assertEquals(user.getBirthday(), response.getPlayerProfile().getUser().getBirthday());
        assertEquals(request.getLevel(), response.getPlayerProfile().getLevel());

    }

    @Test
    void addOrUpdateProfileTestAddProfile() {

        user = User.builder().birthday(LocalDate.now().minusYears(ALLOWED_AGE_EXAMPLE)).build();
        when(userService.findByID(any(long.class))).thenReturn(Optional.of(user));

        //profile not found - wasn't created yet
        when(playerProfileRepository.findByUser(any(User.class))).thenAnswer(a -> Optional.empty());

        when(benefitService.isCorrect(any(String.class))).thenReturn(true);

        when(playerProfileRepository.save(any(PlayerProfile.class))).thenReturn(
                PlayerProfile.builder()
                        .user(user)
                        .level(LEVEL)
                        .build());


        PlayerProfileResponse response = playerProfileService.addOrUpdateProfile(request);

        assertEquals(PlayerProfileInfo.OK, response.getInfo());
        assertEquals(user.getBirthday(), response.getPlayerProfile().getUser().getBirthday());
        assertEquals(request.getLevel(), response.getPlayerProfile().getLevel());

    }

    @Test
    void addOrUpdateProfileTestAddProfileWithoutBenefit() {

        user = User.builder().birthday(LocalDate.now().minusYears(ALLOWED_AGE_EXAMPLE)).build();
        when(userService.findByID(any(long.class))).thenReturn(Optional.of(user));

        //profile not found - wasn't created yet
        when(playerProfileRepository.findByUser(any(User.class))).thenAnswer(a -> Optional.empty());

        when(playerProfileRepository.save(any(PlayerProfile.class))).thenReturn(
                PlayerProfile.builder()
                        .user(user)
                        .level(LEVEL)
                        .build());


        //setting request without benefit
        request.setBenefitCardNumber(null);

        PlayerProfileResponse response = playerProfileService.addOrUpdateProfile(request);

        assertEquals(PlayerProfileInfo.OK, response.getInfo());
        assertEquals(user.getBirthday(), response.getPlayerProfile().getUser().getBirthday());
        assertEquals(request.getLevel(), response.getPlayerProfile().getLevel());
    }


    @Test
    void addOrUpdateProfileTestUserNotFound() {

        when(userService.findByID(any(long.class))).thenReturn(Optional.empty());

        //call
        PlayerProfileResponse response = playerProfileService.addOrUpdateProfile(request);

        assertEquals(PlayerProfileInfo.NOT_AUTHENTICATED, response.getInfo());

    }

    @Test
    void addOrUpdateProfileTestPlayerSignedUpForEvent() {

        user = User.builder().birthday(LocalDate.now().minusYears(ALLOWED_AGE_EXAMPLE)).build();
        when(userService.findByID(any(long.class))).thenReturn(Optional.of(user));

        //setting PlayerProfile with apps - User is signed up for tomorrow's Match
        when(playerProfileRepository.findByUser(user)).thenAnswer(a -> Optional.of(PlayerProfile.builder()
                .apps(Set.of(
                        Slot.builder().event(
                                Match.builder()
                                        .dateTime(LocalDateTime.now().plusDays(1))
                                        .isActive(true).build()).build()))
                .level(LEVEL)
                .benefitCardNumber(BENEFIT_CARD_NUMBER).build()));


        PlayerProfileResponse response = playerProfileService.addOrUpdateProfile(request);

        assertEquals(PlayerProfileInfo.YOU_ARE_SIGNED_UP_FOR_EVENT, response.getInfo());

    }

    @Test
    void addOrUpdateProfileTestIncorrectBenefitNumber() { //User has PlayerProfile and tries to update his/her Benefit Card number

        user = User.builder().birthday(LocalDate.now().minusYears(ALLOWED_AGE_EXAMPLE)).build();
        when(userService.findByID(any(long.class))).thenReturn(Optional.of(user));

        when(playerProfileRepository.findByUser(any(User.class))).thenAnswer(a -> Optional.of(PlayerProfile.builder()
                .apps(Set.of())
                .level(LEVEL)
                .benefitCardNumber(BENEFIT_CARD_NUMBER).build()));

        //setting result of checking the Benefit Card
        when(benefitService.isCorrect(any(String.class))).thenReturn(false);

        PlayerProfileResponse response = playerProfileService.addOrUpdateProfile(request);

        assertEquals(PlayerProfileInfo.INCORRECT_BENEFIT_NUMBER, response.getInfo());
    }


    @Test
    void getPlayerProfileOfAuthenticatedUserTestUserHasProfile() throws NotAuthenticatedException {

        user = User.builder().birthday(LocalDate.now().minusYears(ALLOWED_AGE_EXAMPLE)).build();
        when(userService.findByID(any(long.class))).thenReturn(Optional.of(user));

        when(playerProfileRepository.findByUser(any(User.class))).thenAnswer(a -> Optional.of(PlayerProfile.builder()
                .apps(Set.of())
                .level(LEVEL)
                .benefitCardNumber(BENEFIT_CARD_NUMBER).build()));

        when(benefitService.isActive(any(String.class))).thenReturn(false);

        PlayerProfile playerProfile = playerProfileService.getPlayerProfileOfAuthenticatedUser().orElseThrow();

        assertEquals(ALLOWED_AGE_EXAMPLE, playerProfile.getAge());
        assertFalse(playerProfile.isActiveBenefit());
    }

    @Test
    void getPlayerProfileOfAuthenticatedUserTestUserNotAuthenticated() {

        when(userService.findByID(any(long.class))).thenReturn(Optional.empty());

        try {
            playerProfileService.getPlayerProfileOfAuthenticatedUser();
            fail("Exception expected!");
        } catch (NotAuthenticatedException e) {
            verifyNoInteractions(playerProfileRepository);
        }

    }

    @Test
    void isPlayerSignedUpForActiveEventTestReturnsTrue() {

        PlayerProfile playerProfile = PlayerProfile.builder().apps(Set.of(
                Slot.builder().event(
                        Match.builder()
                                .dateTime(LocalDateTime.now().plusDays(1))
                                .isActive(true)
                                .build())
                        .build())).build();

        assertTrue(playerProfileService.isPlayerSignedUpForActiveEvent(playerProfile));

    }

    @Test
    void isPlayerSignedUpForActiveEventTestReturnsFalse() {

        PlayerProfile playerProfile = PlayerProfile.builder().apps(Set.of()).build();

        assertFalse(playerProfileService.isPlayerSignedUpForActiveEvent(playerProfile));

    }

    @Test
    void calculateAgeTest() {

        assertEquals(ALLOWED_AGE_EXAMPLE, calculateAge(LocalDate.now().minusYears(ALLOWED_AGE_EXAMPLE)));
        assertEquals(0, calculateAge(null));
        assertEquals(0, calculateAge(LocalDate.now().plusYears(ALLOWED_AGE_EXAMPLE)));

    }


}
