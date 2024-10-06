package pl.volleylove.antenka.benefit;

import org.springframework.stereotype.Service;

@Service
public class BenefitService {

    //todo: move to database and create logic
    public boolean isActive(String cardNumber){
        return true;
    }
    public boolean isCorrect(String cardNumber) {return true;}

}
