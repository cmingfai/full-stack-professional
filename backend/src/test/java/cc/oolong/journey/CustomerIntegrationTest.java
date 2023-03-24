package cc.oolong.journey;

import cc.oolong.customer.Customer;
import cc.oolong.customer.CustomerRegistrationRequest;
import cc.oolong.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

    public static final String CUSTOMERS_URI = "/api/v1/customers";
    @Autowired
    private WebTestClient webTestClient;

    private static final Random RANDOM=new Random();
    @Test
    void canRegisterCustomer() {
        Faker faker=new Faker();
        Name fakerName = faker.name();
        String name=fakerName.fullName();
        String email=fakerName.lastName()+ "-"+UUID.randomUUID()+"@amigoscode.com";
        int age=RANDOM.nextInt(1,100);
        // create a registration request
        CustomerRegistrationRequest request =new CustomerRegistrationRequest(name,email,age);
        // send a post request
        webTestClient.post()
                .uri(CUSTOMERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customer

        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();




        // make sure that customer is present
        Customer expectedCustomer = new Customer(name,email,age);
        assertThat(allCustomers).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        int id=allCustomers.stream().filter(customer->customer.getEmail().equals(email))
                .map(Customer::getId).findFirst().orElseThrow();
        expectedCustomer.setId(id);

        // get customer by ID


        webTestClient.get()
                .uri(CUSTOMERS_URI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                }).isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        Faker faker=new Faker();
        Name fakerName = faker.name();
        String name=fakerName.fullName();
        String email=fakerName.lastName()+ "-"+UUID.randomUUID()+"@amigoscode.com";
        int age=RANDOM.nextInt(1,100);
        // create a registration request
        CustomerRegistrationRequest request =new CustomerRegistrationRequest(name,email,age);
        // send a post request
        webTestClient.post()
                .uri(CUSTOMERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customer

        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        int id=allCustomers.stream().filter(customer->customer.getEmail().equals(email))
                .map(Customer::getId).findFirst().orElseThrow();

        // delete customer
        webTestClient.delete()
                .uri(CUSTOMERS_URI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                ;


        // get customer by ID
        webTestClient.get()
                .uri(CUSTOMERS_URI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
               ;
    }

    @Test
    void canUpdateCustomer() {
        Faker faker=new Faker();
        Name fakerName = faker.name();
        String name=fakerName.fullName();
        String email=fakerName.lastName()+ "-"+UUID.randomUUID()+"@amigoscode.com";
        int age=RANDOM.nextInt(1,100);
        // create a registration request
        CustomerRegistrationRequest registrationRequest =new CustomerRegistrationRequest(name,email,age);
        // send a post request
        webTestClient.post()
                .uri(CUSTOMERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest),CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customer

        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // find id
        int id=allCustomers.stream().filter(customer->customer.getEmail().equals(email))
                .map(Customer::getId).findFirst().orElseThrow();



        // update customer
        String newName=fakerName.fullName();
        String newEmail=fakerName.lastName()+ "-"+UUID.randomUUID()+"@amigoscode.com";
        int newAge=age+RANDOM.nextInt(1,100);

        CustomerUpdateRequest updateRequest=new CustomerUpdateRequest(newName,newEmail,newAge);
        webTestClient.put()
                .uri(CUSTOMERS_URI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest),CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get customer by ID

        // make sure that customer is present
        Customer expectedCustomer = new Customer(id,newName,newEmail,newAge);


        webTestClient.get()
                .uri(CUSTOMERS_URI+"/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                }).isEqualTo(expectedCustomer);
    }
}
