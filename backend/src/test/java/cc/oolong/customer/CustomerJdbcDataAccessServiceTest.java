package cc.oolong.customer;

import cc.oolong.TestcontainersTest;
//import org.junit.BeforeClass;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
class CustomerJdbcDataAccessServiceTest extends TestcontainersTest {

    private CustomerJdbcDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper=new CustomerRowMapper();

   @BeforeEach
    void setUp() {

       underTest=new CustomerJdbcDataAccessService(getJdbcTemplate(),customerRowMapper);
    }


    @AfterEach
    void tearDown() {

    }

    @Test
    void selectAllCustomers() {
        // given
         Customer customer=new Customer(FAKER.name().fullName(),
                 FAKER.internet().safeEmailAddress()+"-"+ UUID.randomUUID(),
                 "password", 28, Gender.MALE);
         underTest.insertCustomer(customer);

        // when
        List<Customer> customers=underTest.selectAllCustomers();

        // then
        assertThat(customers).isNotEmpty();

    }

    @Test
    void selectCustomerById() {
        // given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer=new Customer(FAKER.name().fullName(),
                email,
                "password", 28,
                Gender.MALE);
        underTest.insertCustomer(customer);

        int id=underTest.selectAllCustomers().stream().filter(c->c.getEmail().equals(email))
                .map(Customer::getId).findFirst().orElseThrow();

        // when
        Optional<Customer> actual=underTest.selectCustomerById(id);

        // then
        assertThat(actual).isPresent().hasValueSatisfying(c->{
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(customer.getGender());
        });
    }


    @Test
    void willReturnEmptyWhenSelectCustomerById() {
       // given
        int id=-1;

        // when
        Optional<Customer> actual=underTest.selectCustomerById(id);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        // given
        List<Customer> originalCustomers=underTest.selectAllCustomers();

        int originalCustomerSize=originalCustomers.size();

        int numOfCustomersToAdd=3;
        for (int i=0; i<numOfCustomersToAdd; i++) {
            Customer customer = new Customer(FAKER.name().fullName(),
                    FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                    "password", 28,Gender.MALE);
            underTest.insertCustomer(customer);
        }
        // when
        List<Customer> customers=underTest.selectAllCustomers();

        // then
        assertThat(customers).isNotEmpty().hasSize(originalCustomerSize+numOfCustomersToAdd);
    }

    @Test
    void existsPersonWithEmail() {

        // given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer=new Customer(FAKER.name().fullName(),
                email,
                "password", 28,
                Gender.MALE);
        underTest.insertCustomer(customer);

        // when
        var actual=underTest.existsPersonWithEmail(email);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        // given
        String nonExistEmail="nonexists@example.com";

        // when
        var actual=underTest.existsPersonWithEmail(nonExistEmail);

        // then
        assertThat(actual).isFalse();

    }

    @Test
    void existsPersonWithId() {
        // given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer=new Customer(FAKER.name().fullName(),
                email,
                "password", 28,
                Gender.MALE);
        underTest.insertCustomer(customer);

        // when
        int id=underTest.selectAllCustomers().stream().filter(c->c.getEmail().equals(email))
                .map(Customer::getId).findFirst().orElseThrow();
        boolean actual=underTest.existsPersonWithId(id);

        // then
         assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithIdWillReturnFalseWhenIdNotPresent() {
        // given
        int id=-1;

        // when
        boolean actual=underTest.existsPersonWithId(id);

        // then
        assertThat(actual).isFalse();

    }

    @Test
    void deleteCustomerById() {
        // given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer=new Customer(FAKER.name().fullName(),
                email,
                "password", 28,
                Gender.MALE);
        underTest.insertCustomer(customer);

        int id=underTest.selectAllCustomers().stream().filter(c->c.getEmail().equals(email))
                .map(Customer::getId).findFirst().orElseThrow();
        underTest.deleteCustomerById(id);


        // then
        var actual=underTest.existsPersonWithId(id);
        assertThat(actual).isFalse();
    }

    @Test
    void updateCustomerName() {
        // given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String oldName=FAKER.name().fullName();
        Customer customer=new Customer(oldName,
                email,
                "password", 28,
                Gender.MALE);
        underTest.insertCustomer(customer);

        // when
        int id=underTest.selectAllCustomers().stream().filter(c->c.getEmail().equals(email))
                .map(Customer::getId).findFirst().orElseThrow();

        customer.setId(id);
        String newName=FAKER.name().fullName();
        customer.setName(newName);
        underTest.updateCustomer(customer);

        // then
        Optional<Customer> updated=underTest.selectCustomerById(id);

        assertThat(updated).isPresent().hasValueSatisfying(c->{
           assertThat(c.getName()).isEqualTo(newName);
           assertThat(c.getName()).isNotEqualTo(oldName);
        });



    }

    @Test
    void updateCustomerEmail(){
        // given
        String oldEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name=FAKER.name().fullName();
        Customer customer=new Customer(name,
                oldEmail,
                "password", 28,
                Gender.MALE);
       underTest.insertCustomer(customer);

        // when
        int id=underTest.selectAllCustomers().stream().filter(c->c.getEmail().equals(oldEmail))
                .map(Customer::getId).findFirst().orElseThrow();

        customer.setId(id);
        String newEmail=FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        customer.setEmail(newEmail);
        underTest.updateCustomer(customer);

        // then
        Optional<Customer> updated=underTest.selectCustomerById(id);

        assertThat(updated).isPresent().hasValueSatisfying(c->{
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getName()).isNotEqualTo(oldEmail);
        });

    }

    @Test
    void updateCustomerAge(){
        // given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name=FAKER.name().fullName();
        int oldAge=28;
        Gender gender=Gender.MALE;
        Customer customer=new Customer(name,
                email, "password", oldAge, gender);
        underTest.insertCustomer(customer);

        // when
        int id=underTest.selectAllCustomers().stream().filter(c->c.getEmail().equals(email))
                .map(Customer::getId).findFirst().orElseThrow();

        customer.setId(id);
        int newAge=38;
        customer.setAge(newAge);
        underTest.updateCustomer(customer);

        // then
        Optional<Customer> updated=underTest.selectCustomerById(id);

        assertThat(updated).isPresent().hasValueSatisfying(c->{
            assertThat(c.getAge()).isEqualTo(newAge);
            assertThat(c.getAge()).isNotEqualTo(oldAge);
        });
    }

    @Test
    void updateCustomerGender(){
        // given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name=FAKER.name().fullName();
        int age=28;
        Gender oldGender=Gender.MALE;
        Customer customer=new Customer(name,
                email, "password", age, oldGender);
        underTest.insertCustomer(customer);

        // when
        int id=underTest.selectAllCustomers().stream().filter(c->c.getEmail().equals(email))
                .map(Customer::getId).findFirst().orElseThrow();

        customer.setId(id);
        Gender newGender=Gender.FEMALE;
        customer.setGender(newGender);
        underTest.updateCustomer(customer);

        // then
        Optional<Customer> updated=underTest.selectCustomerById(id);

        assertThat(updated).isPresent().hasValueSatisfying(c->{
            assertThat(c.getGender()).isEqualTo(newGender);
            assertThat(c.getGender()).isNotEqualTo(oldGender);
        });
    }

    @Test
    void willUpdateAllPropertiesCustomer(){
        // given
        String oldEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String oldName=FAKER.name().fullName();
        int oldAge=28;
        Gender oldGender=Gender.MALE;
        Customer customer=new Customer(oldName, oldEmail, "password", oldAge, oldGender);
        underTest.insertCustomer(customer);

        // when
        int id=underTest.selectAllCustomers().stream().filter(c->c.getEmail().equals(oldEmail))
                .map(Customer::getId).findFirst().orElseThrow();

        customer.setId(id);
        String newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String newName=FAKER.name().fullName();
        int newAge=38;
        Gender newGender=Gender.FEMALE;
        customer.setEmail(newEmail);
        customer.setName(newName);
        customer.setAge(newAge);
        customer.setGender(newGender);
        underTest.updateCustomer(customer);

        // then
        Optional<Customer> updated=underTest.selectCustomerById(id);

        assertThat(updated).isPresent().hasValueSatisfying(c->{
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getName()).isNotEqualTo(oldName);
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getName()).isNotEqualTo(oldEmail);
            assertThat(c.getAge()).isEqualTo(newAge);
            assertThat(c.getAge()).isNotEqualTo(oldAge);
            assertThat(c.getGender()).isEqualTo(newGender);
            assertThat(c.getGender()).isNotEqualTo(oldGender);
        });

    }

    @Test
    void willNotUpdateWhenNothingToUpdate(){
        // given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name=FAKER.name().fullName();
        int age=28;
        Gender gender=Gender.MALE;
        Customer customer=new Customer(name, email, "password", age, gender);
        underTest.insertCustomer(customer);

        // when
        int id=underTest.selectAllCustomers().stream().filter(c->c.getEmail().equals(email))
                .map(Customer::getId).findFirst().orElseThrow();

        customer.setId(id); // only set ID, no other change
        underTest.updateCustomer(customer);

        // then
        Optional<Customer> updated=underTest.selectCustomerById(id);

        assertThat(updated).isPresent().hasValueSatisfying(c->{
            assertThat(c.getName()).isEqualTo(name);
            assertThat(c.getEmail()).isEqualTo(email);
            assertThat(c.getAge()).isEqualTo(age);
            assertThat(c.getGender()).isEqualTo(gender);
        });
    }

}