package cc.oolong.customer;

import cc.oolong.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsCustomerByEmail() {
        // given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer=new Customer(FAKER.name().fullName(),
                email,
                28,
                Gender.MALE);
        underTest.save(customer);

        // when
        var actual=underTest.existsCustomerByEmail(email);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByEmailFailsWhenEmailDoesNotPresent() {
        // given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer=new Customer(FAKER.name().fullName(),
                email,
                28,
                Gender.MALE);

        // when
        var actual=underTest.existsCustomerByEmail(email);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById() {
        // given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer=new Customer(FAKER.name().fullName(),
                email,
                28,
                Gender.MALE);
        underTest.save(customer);

        int id=underTest.findAll().stream().filter(c->c.getEmail().equals(email))
                .map(Customer::getId).findFirst().orElseThrow();

        // when
        var actual=underTest.existsCustomerById(id);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByIdFailsWhenIdDoesNotPresent() {
        // given
       int id=-1;

        // when
        var actual=underTest.existsCustomerById(id);

        // then
        assertThat(actual).isFalse();
    }
}