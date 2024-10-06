package pl.volleylove.antenka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.volleylove.antenka.playerprofile.PlayerProfileRequest;
import pl.volleylove.antenka.playerprofile.PlayerProfileResponse;
import pl.volleylove.antenka.playerprofile.PlayerProfileService;

@RestController
public class PlayerProfileController {

    private final PlayerProfileService playerProfileService;

    @Autowired
    public PlayerProfileController(PlayerProfileService playerProfileService) {

        this.playerProfileService = playerProfileService;

    }


    @PostMapping("/addPlayerProfile")
    public PlayerProfileResponse addPlayerProfile(@RequestBody @Validated PlayerProfileRequest playerProfileRequest){

        return playerProfileService.addOrUpdateProfile(playerProfileRequest);

    }

}
