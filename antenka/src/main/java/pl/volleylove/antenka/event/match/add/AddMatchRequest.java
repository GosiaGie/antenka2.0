package pl.volleylove.antenka.event.match.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import pl.volleylove.antenka.entity.Address;
import pl.volleylove.antenka.event.PlayerWanted;
import pl.volleylove.antenka.event.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AddMatchRequest {

    private static final int AGE_MIN_ALLOWED = 16;
    private static final int AGE_MAX_ALLOWED = 150;

    private static final int SLOT_MAX_ALLOWED = 24;

    private static final int NAME_MIN_LENGTH = 3;
    private static final int NAME_MAX_LENGTH = 30;

    @NotNull
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = "Name has to contain from "
            + NAME_MIN_LENGTH  + " to " + NAME_MAX_LENGTH + " characters")
    private String name;
    private LocalDateTime dateTime;
    private Price price;
    private Address address;
    @NotNull
    @Min(value = 1, message = "Match needs at least one player slot")
    @Max(value = SLOT_MAX_ALLOWED, message = "Match can have max 24 players")
    private int playersNum;
    //Here I'm using List, not Set - to allow adding players with the same requirements
    @JsonProperty("playersWanted")
    private List<PlayerWanted> players;


    @AssertTrue(message = "Incorrect price. Price can't be under 0.0 and regular price can't be higher than benefit price")
    public boolean isPriceCorrect() {
        if (price == null || price.getRegularPrice() == null || price.getBenefitPrice() == null) {
            return false;
        }

        int pReg = price.getRegularPrice().compareTo(new BigDecimal("0.0"));
        int pBen = price.getBenefitPrice().compareTo(new BigDecimal("0.0"));
        int regHigherThanBen = price.getRegularPrice().compareTo(getPrice().getBenefitPrice());

        return pReg >= 0 && pBen >= 0 && regHigherThanBen >= 0;
    }

    @AssertTrue(message = "Date can't be: past date, date under 3 h from now or date after 6 months from now")
    public boolean isDateTimeCorrect() {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now().plusHours(3)) && dateTime.isBefore(LocalDateTime.now().plusMonths(6));
    }

    @AssertTrue(message = "Incorrect address")
    public boolean isAddressCorrect() {
        if (address == null) {
            return false;
        }

        boolean correctNum = !address.getNumber().isEmpty() && address.getNumber().length() <= 10;
        boolean correctStreet = address.getStreet().length() >= 3 && address.getStreet().length() <= 30 && !address.getStreet().contains(" ");
        boolean correctZipCode = address.getZipCode().length() == 5 && address.getZipCode().chars().allMatch(Character::isDigit);
        boolean correctLocality = address.getLocality().length() >= 3 && address.getLocality().chars().allMatch(Character::isLetter);

        return correctNum && correctStreet && correctZipCode && correctLocality;
    }

    @AssertTrue(message = "Incorrect age: under 16, over 150 or min>max")
    public boolean isAgeRangeCorrect() {

        if (players == null) {
            return false;
        }

        return players.stream().allMatch(p -> p.getAgeRange() != null
                && p.getAgeRange().getAgeMin() >= AGE_MIN_ALLOWED && p.getAgeRange().getAgeMax() <= AGE_MAX_ALLOWED
                && p.getAgeRange().getAgeMin() <= p.getAgeRange().getAgeMax());
    }

    @AssertTrue(message = "Check your slots number")
    public boolean isPlayerNumEqualPlayerListSizeAndBelowLimit() {

        if (players == null) {
            return false;
        }

        return playersNum == players.size();
    }

    @AssertTrue(message = "Fill all player's requirements")
    public boolean isListWithRequirements() {

        if (players == null) {
            return false;
        }

        return players.stream().allMatch(playerWanted -> playerWanted.getPosition() != null
                || playerWanted.getGender() != null
                || playerWanted.getLevel() != null
                || playerWanted.getAgeRange() != null);
    }


}
