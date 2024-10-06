package pl.volleylove.antenka.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.volleylove.antenka.map.resultcomponents.ResultRecord;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleMapsApiResult {

    List <ResultRecord> results;

    String status;

    @Override
    public String toString() {
        return "Result{" +
                "results=" + results +
                ", status='" + status + '\'' +
                '}';
    }
}
