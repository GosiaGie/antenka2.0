package pl.volleylove.antenka.playerprofile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.volleylove.antenka.entity.PlayerProfile;
import pl.volleylove.antenka.enums.PlayerProfileInfo;

@Getter
@Setter
@Builder
public class PlayerProfileResponse {

    private PlayerProfileInfo info;
    @JsonIgnoreProperties({"user", "randomMatchApps"})
    private PlayerProfile playerProfile;

}
