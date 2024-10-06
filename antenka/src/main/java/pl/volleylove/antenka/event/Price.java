package pl.volleylove.antenka.event;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Price {


    @Column(name = "regular_price")
    private BigDecimal regularPrice;

    @Column(name = "benefit_price")
    private BigDecimal benefitPrice;

    public Price(String regularPrice, String priceBenefit) {
        this.regularPrice = new BigDecimal(regularPrice);
        this.benefitPrice = new BigDecimal(priceBenefit);
    }


    @Override
    public String toString() {
        return "Price{" +
                "regularPrice=" + regularPrice +
                ", benefitPrice=" + benefitPrice +
                '}';
    }

}
