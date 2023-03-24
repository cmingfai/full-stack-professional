package cc.oolong.customer;

public record CustomerUpdateRequest(String name, String email, Integer age) {
}
