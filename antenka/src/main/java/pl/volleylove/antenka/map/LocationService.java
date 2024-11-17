package pl.volleylove.antenka.map;

import eu.dattri.jsonbodyhandler.JsonBodyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import pl.volleylove.antenka.entity.Address;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

@ConfigurationProperties("google.maps")
@Service
public class LocationService {

    private final GoogleMapsApiHelper googleMapsApiHelper;

    @Autowired
    public LocationService(GoogleMapsApiHelper googleMapsApiHelper) {
        this.googleMapsApiHelper = googleMapsApiHelper;
    }

    public Address setLocationInAddress(Address address) throws IOException, InterruptedException {

        //client
        var client = HttpClient.newHttpClient();

        //request
        var request = HttpRequest.newBuilder(
                        URI.create(googleMapsApiHelper.createURI
                                (address.getStreet(), address.getNumber(), address.getZipCode(), address.getLocality())))
                .header("accept", "application/json")
                .build();

        JsonBodyHandler<GoogleMapsApiResult> jsonBodyHandler = JsonBodyHandler.jsonBodyHandler(GoogleMapsApiResult.class);
        //response
        var response = client.send(request, jsonBodyHandler);

        address.setLocation(response.body().getResults().get(0).getGeometry().getLocation());

        return address;
    }

}
