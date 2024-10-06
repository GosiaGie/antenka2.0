package pl.volleylove.antenka.user.delete;

import lombok.Builder;
import lombok.Getter;
import pl.volleylove.antenka.enums.DeleteAccountInfo;

@Getter
@Builder
public class DeleteAccountResponse {

    private String email;
    private DeleteAccountInfo info;

}
