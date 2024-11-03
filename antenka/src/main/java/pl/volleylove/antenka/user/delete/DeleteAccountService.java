package pl.volleylove.antenka.user.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.volleylove.antenka.entity.User;
import pl.volleylove.antenka.enums.DeleteAccountInfo;
import pl.volleylove.antenka.repository.UserRepository;
import pl.volleylove.antenka.security.UserPrincipal;

@Service
public class DeleteAccountService {

    private UserRepository userRepository;

    @Autowired
    public DeleteAccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public DeleteAccountResponse deleteAccountAttempt(String email) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        if(email.equals(userPrincipal.getEmail())) {
            User user = User.builder().userID(userPrincipal.getUserID()).build();
            userRepository.delete(user);
            authentication.setAuthenticated(false);
            return DeleteAccountResponse.builder().email(email).info(DeleteAccountInfo.OK).build();
        }
        else {
            return DeleteAccountResponse.builder().email(email).info(DeleteAccountInfo.INVALID_EMAIL).build();
        }
    }
}
