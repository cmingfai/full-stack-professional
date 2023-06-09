package cc.oolong.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;


class CustomerJPADataAccessServiceTest {
    private CustomerJPADataAccessService underTest;
    @Mock
    private CustomerRepository customerRepository;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable=MockitoAnnotations.openMocks(this);
        underTest =new CustomerJPADataAccessService(customerRepository);
    }


    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

//    void selectAllCustomers() {
//        // when
//        underTest.selectAllCustomers();
//
//        // then
//        verify(customerRepository).findAll();
//    }

    @Test
    void selectAllCustomers() {
        Page<Customer> page = mock(Page.class);
        List<Customer> customers = List.of(new Customer());
        when(page.getContent()).thenReturn(customers);
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);
        // When
        List<Customer> expected = underTest.selectAllCustomers();

        // Then
        assertThat(expected).isEqualTo(customers);
        ArgumentCaptor<Pageable> pageArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(customerRepository).findAll(pageArgumentCaptor.capture());
        assertThat(pageArgumentCaptor.getValue()).isEqualTo(Pageable.ofSize(50));
    }

    @Test
    void selectCustomerById() {
        // given
        int id=1;

        // when
        underTest.selectCustomerById(id);

        // then
        verify(customerRepository).findById(id);

    }

    @Test
    void insertCustomer() {
        // given
        Customer customer=new Customer(1,"foo bar",
                "foo@gmail.com",
                "password", 28,
                Gender.MALE);

        // when
        underTest.insertCustomer(customer);

        // then
        verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        // given
        String email = "foo@gmail.com";

        // when
        underTest.existsPersonWithEmail(email);

        // then
        verify(customerRepository).existsCustomerByEmail(email);

    }

    @Test
    void existsPersonWithId() {
        // given
        int id=1;

        // when
        underTest.existsPersonWithId(id);

        // then
        verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void deleteCustomerById() {
        // given
        int id=1;

        // when
        underTest.deleteCustomerById(id);

        // then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {
        // given
        Customer customer=new Customer(1,"foo bar",
                "foo@gmail.com",
                "password", 28,
                Gender.MALE);

        // when
        underTest.updateCustomer(customer);

        // then
        verify(customerRepository).save(customer);
    }

    @Test
    void canUpdateProfileImageId() {
        // given
        String profileImageId="2222";
        Integer customerId=1;

        // when
        underTest.updateCustomerProfileImageId(profileImageId,customerId);

        // then
        verify(customerRepository).updateProfileImageId(
                profileImageId,
                customerId);
    }
}