package pl.volleylove.antenka.map;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public class GoogleMapsApiHelper {

    private final GoogleMapsApiProperties googleMapsApiProperties;

    @Autowired
    public GoogleMapsApiHelper(GoogleMapsApiProperties googleMapsApiProperties) {
        this.googleMapsApiProperties = googleMapsApiProperties;
    }

    public String createURI(String street, String number, String zipCode, String locality){

        return googleMapsApiProperties.getApiPath()+"json?address="+ street + "+" + number
                + "+" + zipCode + "+" +locality + "&key=" + googleMapsApiProperties.getApiKey();
    }


}
