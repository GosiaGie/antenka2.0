package pl.volleylove.antenka.event.match;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import pl.volleylove.antenka.entity.Address;
import pl.volleylove.antenka.entity.Match;
import pl.volleylove.antenka.entity.PlayerProfile;
import pl.volleylove.antenka.entity.User;
import pl.volleylove.antenka.enums.FindMatchInfo;
import pl.volleylove.antenka.enums.Gender;
import pl.volleylove.antenka.enums.Level;
import pl.volleylove.antenka.enums.Position;
import pl.volleylove.antenka.event.match.add.AddMatchRequest;
import pl.volleylove.antenka.event.match.add.AddMatchResponse;
import pl.volleylove.antenka.event.match.find.FindMatchRequest;
import pl.volleylove.antenka.event.match.find.FindMatchResponse;
import pl.volleylove.antenka.map.LocationService;
import pl.volleylove.antenka.playerprofile.PlayerProfileService;
import pl.volleylove.antenka.repository.MatchRepository;
import pl.volleylove.antenka.security.NotAuthenticatedException;
import pl.volleylove.antenka.user.UserService;
import pl.volleylove.antenka.user.auth.AuthService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @Test
    void addTestAddMatchRequestHasErrors() throws IOException, InterruptedException {

        String expectedErrorMessage = "Incorrect address";

        when(errors.hasErrors()).thenReturn(true);
        when(errors.getAllErrors()).thenReturn(List.of(new ObjectError("address error",
                "Incorrect address")));

        AddMatchResponse expectedResponse = AddMatchResponse
                .builder()
                .addMatchInfo(List.of(expectedErrorMessage))
                .build();

        AddMatchResponse actualResponse = matchService.add(AddMatchRequest.builder().build(), errors);

        assertEquals(expectedResponse, actualResponse);

    }

    @Test
    void addTestUserIsNotLoggedIn() throws IOException, InterruptedException {

        when(userService.findByID(any(long.class))).thenReturn(Optional.empty());

        AddMatchResponse expectedResponse = AddMatchResponse
                .builder()
                .addMatchInfo(List.of("USER_NOT_FOUND"))
                .build();

        assertEquals(expectedResponse, matchService.add(AddMatchRequest.builder().build(), errors));

    }

    @Test
    void addTestSuccess() throws IOException, InterruptedException {

        Match matchToAdd = Match.builder().name("test").build();

        when(userService.findByID(any(long.class))).thenReturn(Optional.of(new User()));
        when(locationService.setLocationInAddress(any(Address.class))).thenReturn(new Address());
        when(matchRepository.save(any(Match.class))).thenReturn(matchToAdd);

        AddMatchResponse expectedResponse = AddMatchResponse
                .builder()
                .match(matchToAdd)
                .addMatchInfo(List.of("OK"))
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

        FindMatchResponse actualResponse = matchService.findMatch(new FindMatchRequest("1"));

        assertEquals(expectedResponse, actualResponse);

    }

    @Test
    void findTestPlayerBenefit() throws NotAuthenticatedException {

        when(playerProfileService.getPlayerProfileOfAuthenticatedUser())
                .thenReturn(Optional.of(PlayerProfile.builder()
                                .gender(Gender.FEMALE)
                                .age(25)
                                .level(Level.BEGINNER)
                                .positions(Set.of(Position.SETTER))
                        .activeBenefit(true)
                        .build()));

        when(matchRepository.findByPlayerWantedBenefit(any(Gender.class), any(Level.class), anyInt(), anySet(), any(BigDecimal.class))).thenReturn(new HashSet<>());

        FindMatchResponse expectedResponse = FindMatchResponse.builder()
                .matches(new HashSet<>())
                .findMatchInfo(FindMatchInfo.OK)
                .build();

        FindMatchResponse actualResponse = matchService.findMatch(new FindMatchRequest("1"));

        assertEquals(expectedResponse, actualResponse);

        verify(matchRepository, times(1)).findByPlayerWantedBenefit(any(Gender.class), any(Level.class), anyInt(), anySet(), any(BigDecimal.class));
        verify(matchRepository, times(0)).findByPlayerWantedNoBenefit(any(Gender.class), any(Level.class), anyInt(), anySet(), any(BigDecimal.class));

    }

    @Test
    void findTestPlayerNoBenefit() throws NotAuthenticatedException {

        when(playerProfileService.getPlayerProfileOfAuthenticatedUser())
                .thenReturn(Optional.of(PlayerProfile.builder()
                        .gender(Gender.FEMALE)
                        .age(25)
                        .level(Level.BEGINNER)
                        .positions(Set.of(Position.SETTER))
                        .activeBenefit(false)
                        .build()));

        when(matchRepository.findByPlayerWantedNoBenefit(any(Gender.class), any(Level.class), anyInt(), anySet(), any(BigDecimal.class))).thenReturn(new HashSet<>());

        FindMatchResponse expectedResponse = FindMatchResponse.builder()
                .matches(new HashSet<>())
                .findMatchInfo(FindMatchInfo.OK)
                .build();

        FindMatchResponse actualResponse = matchService.findMatch(new FindMatchRequest("1"));

        assertEquals(expectedResponse, actualResponse);

        verify(matchRepository, times(0)).findByPlayerWantedBenefit(any(Gender.class), any(Level.class), anyInt(), anySet(), any(BigDecimal.class));
        verify(matchRepository, times(1)).findByPlayerWantedNoBenefit(any(Gender.class), any(Level.class), anyInt(), anySet(), any(BigDecimal.class));
    }



}
