package cc.oolong.customer;

import java.util.List;

public record CustomerDTO(
        Integer id,
        String name,
        String email,
        Integer age,
        Gender gender,
        List<String> roles,
        String username,
        String profileImageId) {
}
