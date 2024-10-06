package pl.volleylove.antenka.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.volleylove.antenka.security.UserPrincipal;

//todo: admin features
@RestController
public class AdminController {

    @GetMapping("/admin")
    public String admin(@AuthenticationPrincipal UserPrincipal userPrincipal){

        return "if you see this, you are an admin " + userPrincipal.getEmail() + " " + userPrincipal.getUserID();

    }

}
