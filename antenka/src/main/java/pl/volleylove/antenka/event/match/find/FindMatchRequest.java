package pl.volleylove.antenka.event.match.find;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
//todo: find by address / location
public class FindMatchRequest {

   private BigDecimal maxPrice;

   public FindMatchRequest(String price) {
      this.maxPrice = new BigDecimal(price);
   }

}
