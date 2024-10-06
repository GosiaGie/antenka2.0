package pl.volleylove.antenka.user.delete;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteAccountRequest {

    private String email;

}
