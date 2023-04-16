package cc.oolong.journey;

import cc.oolong.auth.AuthenticationRequest;
import cc.oolong.auth.AuthenticationResponse;
import cc.oolong.customer.CustomerDTO;
import cc.oolong.customer.CustomerRegistrationRequest;
import cc.oolong.customer.Gender;
import cc.oolong.jwt.JWTUtil;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthenticationIT {

    public static final String CUSTOMER_PATH = "/api/v1/customers";
    public static final String AUTHENTICATION_PATH = "/api/v1/auth";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JWTUtil jwtUtil;

    private static final Random RANDOM=new Random();

    @Test
    void canLogin() {
        Faker faker=new Faker();
        Name fakerName = faker.name();
        String name=fakerName.fullName();
        String email=fakerName.lastName()+ "-"+ UUID.randomUUID()+"@amigoscode.com";
        String password="password";
        int age=RANDOM.nextInt(1,100);
        Gender gender= Gender.MALE;

        // create a registration request
        CustomerRegistrationRequest registrationRequest =new CustomerRegistrationRequest(name,email, password, age,gender);


        AuthenticationRequest authenticationRequest=new AuthenticationRequest(
                email, password
        );

        // login with non existing customer
       webTestClient.post()
                .uri(AUTHENTICATION_PATH+"/login")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(authenticationRequest),AuthenticationRequest.class)
                .exchange()
                .expectStatus()
               .isUnauthorized();

        // register a new customer

      webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
               ;


        // login with the created customer

        AuthenticationResponse response=webTestClient.post()
                .uri(AUTHENTICATION_PATH+"/login")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(authenticationRequest),AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult().getResponseBody()
                ;

        String token=response.token();
         CustomerDTO customerDTO=response.customerDTO();
        assertThat(jwtUtil.isTokenValid(token,customerDTO.username())).isTrue();
        assertThat(customerDTO.email()).isEqualTo(email);
        assertThat(customerDTO.name()).isEqualTo(name);
        assertThat(customerDTO.age()).isEqualTo(age);
        assertThat(customerDTO.gender()).isEqualTo(gender);
        assertThat(customerDTO.username()).isEqualTo(email);
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));
    }

}
