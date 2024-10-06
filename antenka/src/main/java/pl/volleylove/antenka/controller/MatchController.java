package pl.volleylove.antenka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.volleylove.antenka.event.match.MatchService;
import pl.volleylove.antenka.event.match.add.AddMatchRequest;
import pl.volleylove.antenka.event.match.add.AddMatchResponse;
import pl.volleylove.antenka.event.match.find.FindMatchRequest;
import pl.volleylove.antenka.event.match.find.FindMatchResponse;
import pl.volleylove.antenka.event.match.signup.SignUpForMatchRequest;
import pl.volleylove.antenka.event.match.signup.SignUpForMatchResponse;
import pl.volleylove.antenka.security.NotAuthenticatedException;

import java.io.IOException;

@RestController
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {

        this.matchService = matchService;

    }

    @PostMapping("/findMatch")
    public FindMatchResponse findMatch(@RequestBody FindMatchRequest request) throws NotAuthenticatedException {

        return matchService.findMatch(request);

    }

    @PostMapping("/addMatch")
    public AddMatchResponse addMatch(@RequestBody @Validated AddMatchRequest request, Errors errors) throws IOException, InterruptedException {

        return matchService.add(request, errors);

    }

    @PostMapping("/signUp")
    public SignUpForMatchResponse signUp(@RequestBody SignUpForMatchRequest request) throws NotAuthenticatedException {

        return matchService.signUpForMatch(request);

    }


}





