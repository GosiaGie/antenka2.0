package pl.volleylove.antenka.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.volleylove.antenka.entity.User;
import pl.volleylove.antenka.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {

        User user = userRepository.findUserByEmail(email);

        if(user!=null){
            var userVar = User.builder().build();
            userVar.setUserID(user.getUserID());
            userVar.setEmail(user.getEmail());
            userVar.setPassword(user.getPassword());
            userVar.setRole(user.getRole());
            return Optional.of(userVar);
        }
        else {
            return Optional.empty();
        }
    }

    public Optional<User> findByID(Long ID){

        return userRepository.findById(ID);

    }


}
