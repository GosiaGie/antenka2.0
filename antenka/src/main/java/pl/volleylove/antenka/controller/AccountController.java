package pl.volleylove.antenka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.volleylove.antenka.user.auth.AuthService;
import pl.volleylove.antenka.user.auth.LoginRequest;
import pl.volleylove.antenka.user.auth.LoginResponse;
import pl.volleylove.antenka.user.delete.DeleteAccountRequest;
import pl.volleylove.antenka.user.delete.DeleteAccountResponse;
import pl.volleylove.antenka.user.delete.DeleteAccountService;
import pl.volleylove.antenka.user.register.RegisterRequest;
import pl.volleylove.antenka.user.register.RegisterResponse;
import pl.volleylove.antenka.user.register.RegisterService;

@RestController
public class AccountController {

    private final AuthService authService;

    private final RegisterService registerService;

    private final DeleteAccountService deleteAccountService;

    @Autowired
    public AccountController(AuthService authService, RegisterService registerService, DeleteAccountService deleteAccountService) {
        this.authService = authService;
        this.registerService = registerService;
        this.deleteAccountService = deleteAccountService;
    }

    @PostMapping("auth/login")
    public LoginResponse login (@RequestBody @Validated LoginRequest loginRequest){

        return authService.loginAttempt(loginRequest.getEmail(), loginRequest.getPassword());

    }

    @PostMapping("auth/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest){

        return registerService.register(registerRequest);

    }

    @PostMapping("auth/deleteAccount")
    public DeleteAccountResponse deleteAccount(@RequestBody DeleteAccountRequest deleteAccountRequest){

        return deleteAccountService.deleteAccountAttempt(deleteAccountRequest.getEmail());

    }

}