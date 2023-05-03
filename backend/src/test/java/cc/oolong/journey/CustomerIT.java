package cc.oolong.journey;

import cc.oolong.customer.*;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;

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
public class CustomerIT {

    public static final String CUSTOMER_PATH = "/api/v1/customers";
    @Autowired
    private WebTestClient webTestClient;

    private static final Random RANDOM=new Random();
    @Test
    void canRegisterCustomer() {
        Faker faker=new Faker();
        Name fakerName = faker.name();
        String name=fakerName.fullName();
        String email=fakerName.lastName()+ "-"+UUID.randomUUID()+"@amigoscode.com";
        String password="password";
        int age=RANDOM.nextInt(1,100);
        Gender gender= Gender.MALE;
        // create a registration request
        CustomerRegistrationRequest request =new CustomerRegistrationRequest(name,email, password, age,gender);
        // send a post request
        String jwt=webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(request),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class).getResponseHeaders().get(AUTHORIZATION).get(0);

        String authorizationHeader = "Bearer %s".formatted(jwt);



        // get customer by email
        CustomerDTO foundCustomer = webTestClient.get()
                .uri(CUSTOMER_PATH+"/email/"+email)
                .accept(APPLICATION_JSON)
                .header(AUTHORIZATION, authorizationHeader)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        int id=foundCustomer.id();

        // make sure that customer is present

        CustomerDTO expectedCustomer = new CustomerDTO(id, name,email, age, gender,List.of("ROLE_USER"),email);

        assertThat(foundCustomer)
                .isEqualTo(expectedCustomer);


        // get customer by ID
//        webTestClient.get()
//                .uri(CUSTOMER_PATH +"/{id}",id)
//                .accept(APPLICATION_JSON)
//                .header(AUTHORIZATION, authorizationHeader)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
//                }).isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        // create a new customer and get jwt
        Faker faker=new Faker();
        Name fakerName = faker.name();
        String name=fakerName.fullName();
        String email=fakerName.lastName()+ "-"+UUID.randomUUID()+"@amigoscode.com";
        String password="password";
        int age=RANDOM.nextInt(1,100);
        Gender gender=Gender.MALE;

         CustomerRegistrationRequest request =new CustomerRegistrationRequest(name,email, password, age,gender);

        // send a post request
        String jwt=webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(request),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders().get(AUTHORIZATION).get(0);

        String authorizationHeader = "Bearer %s".formatted(jwt);

        // create another customer and get ID for deletion

        String anotherName=fakerName.fullName();
        String anotherEmail=fakerName.lastName()+ "-"+UUID.randomUUID()+"@amigoscode.com";
        String anotherPassword="password";
        int anotherAge=RANDOM.nextInt(1,100);
        Gender anotherGender=Gender.FEMALE;

        CustomerRegistrationRequest anotherRequest =new CustomerRegistrationRequest(anotherName,anotherEmail, anotherPassword, anotherAge,anotherGender);

        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(anotherRequest),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // get customer by email
        CustomerDTO foundCustomer = webTestClient.get()
                .uri(CUSTOMER_PATH+"/email/"+anotherEmail)
                .accept(APPLICATION_JSON)
                .header(AUTHORIZATION, authorizationHeader)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        int id=foundCustomer.id();

        // delete customer
        webTestClient.delete()
                .uri(CUSTOMER_PATH +"/{id}",id)
                .accept(APPLICATION_JSON)
                .header(AUTHORIZATION, authorizationHeader)
                .exchange()
                .expectStatus()
                .isOk()
                ;


        // get customer by ID
        webTestClient.get()
                .uri(CUSTOMER_PATH +"/{id}",id)
                .accept(APPLICATION_JSON)
                .header(AUTHORIZATION, authorizationHeader)
                .exchange()
                .expectStatus()
                .isNotFound()
               ;
    }

    @Test
    void canUpdateCustomer() {
        Faker faker=new Faker();
        Name fakerName = faker.name();

        // create a new customer and get jwt
        String name=fakerName.fullName();
        String email=fakerName.lastName()+ "-"+UUID.randomUUID()+"@amigoscode.com";
        String password="password";
        int age=RANDOM.nextInt(1,100);
        Gender gender=Gender.MALE;

        CustomerRegistrationRequest request =new CustomerRegistrationRequest(name,email, password, age,gender);

        // send a post request
        String jwt=webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(request),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders().
                get(AUTHORIZATION)
                .get(0);

        String authorizationHeader = "Bearer %s".formatted(jwt);

        // create another customer and get ID for updating
         String anotherName=fakerName.fullName();
        String anotherEmail=fakerName.lastName()+ "-"+UUID.randomUUID()+"@amigoscode.com";
        String anotherPassword="password";
        int anotherAge=RANDOM.nextInt(1,100);
        Gender anotherGender=Gender.MALE;
        // create a registration request
        CustomerRegistrationRequest anotherRequest =
                new CustomerRegistrationRequest(
                        anotherName,anotherEmail,anotherPassword, anotherAge, anotherGender);
        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(anotherRequest),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        // get customer by email
        CustomerDTO foundCustomer = webTestClient.get()
                .uri(CUSTOMER_PATH+"/email/"+anotherEmail)
                .accept(APPLICATION_JSON)
                .header(AUTHORIZATION, authorizationHeader)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        int id=foundCustomer.id();



        // update customer
        String newName=fakerName.fullName();
        String newEmail=fakerName.lastName()+ "-"+UUID.randomUUID()+"@amigoscode.com";
        int newAge=age+RANDOM.nextInt(1,100);
         Gender newGender=Gender.FEMALE;
         String newPassword="newpassword";
        CustomerUpdateRequest updateRequest=
                new CustomerUpdateRequest(newName,newEmail, newPassword, newAge, newGender);
        webTestClient.put()
                .uri(CUSTOMER_PATH +"/{id}",id)
                .accept(APPLICATION_JSON)
                .header(AUTHORIZATION, authorizationHeader)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(updateRequest),CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get customer by ID

        // make sure that customer is present
        CustomerDTO expectedCustomer = new CustomerDTO(
                id,newName,newEmail, newAge, newGender,List.of("ROLE_USER"),newEmail);


        webTestClient.get()
                .uri(CUSTOMER_PATH +"/{id}",id)
                .accept(APPLICATION_JSON)
                .header(AUTHORIZATION, authorizationHeader)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                }).isEqualTo(expectedCustomer);
    }
}
