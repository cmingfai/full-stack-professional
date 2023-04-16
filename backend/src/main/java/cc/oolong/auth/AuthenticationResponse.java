package cc.oolong.auth;

import cc.oolong.customer.CustomerDTO;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO
) {
}
