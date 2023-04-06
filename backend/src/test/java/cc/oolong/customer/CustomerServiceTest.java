package cc.oolong.customer;

import cc.oolong.exception.DuplicateResourceException;
import cc.oolong.exception.RequestValidationException;
import cc.oolong.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest=new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        // when
        underTest.getAllCustomers();

        // then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        // given
        int id=1;
        Customer customer=new Customer(id,"foo bar",
                "foo@gmail.com",
                28,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // when
        Customer actual
                = underTest.getCustomer(id);

        // then
        assertThat(actual).isEqualTo(customer);
    }



    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        // given
        int id=1;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());


        // then
        assertThatThrownBy(()->underTest.getCustomer(id)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        // given

        String email="alex@gmail.com";

       when(customerDao.existsPersonWithEmail(email)).thenReturn(false);
       CustomerRegistrationRequest request=new CustomerRegistrationRequest("Alex",email,18, Gender.MALE);

        // when
        underTest.addCustomer(request);

        // then
        ArgumentCaptor<Customer> customerArgumentCaptor=ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer=customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());

    }



    @Test
    void willThrowWhenAddingCustomerWithEmailExists() {
        // given
        String email="alex@gmail.com";

        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest request=new CustomerRegistrationRequest("Alex",email,18, Gender.MALE);

        // then
        assertThatThrownBy(()->{
           underTest.addCustomer(request);
        }).isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDao,never()).insertCustomer(any());

    }

    @Test
    void canDeleteCustomer() {
        // given
        int customerId=1;
        when(customerDao.existsPersonWithId(customerId)).thenReturn(true);

        // when
        underTest.deleteCustomer(customerId);

        // then
        verify(customerDao).deleteCustomerById(customerId);
    }

    @Test
    void willThrowWhenDeletingCustomerWhileIdDoesNotExist() {
        // given
        int id=1;
        when(customerDao.existsPersonWithId(id)).thenReturn(false);

         // then
        assertThatThrownBy(()->underTest.deleteCustomer(id)).isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("Customer with id [%s] not found".formatted(id));
        verify(customerDao,never()).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenUpdateCustomerWhileIdDoesNotExist() {
        // given
        int id=1;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());
        CustomerUpdateRequest request=new CustomerUpdateRequest("Alex","alex@gmail.com",28, Gender.MALE);
        assertThatThrownBy(()->underTest.updateCustomer(id,request)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));
    }

    @Test
    void willUpdateOnlyName() {
       // given
        int id=1;
        String name="Alex";
        String email="alex@gmail.com";
        int age=28;
        Gender gender=Gender.MALE;
        Customer customer=new Customer(id, name, email, age, gender);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request=new CustomerUpdateRequest("Jamila",null,null, null);

        // when
        underTest.updateCustomer(id,request);

        ArgumentCaptor<Customer> customerArgumentCaptor=ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer=customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());

        verify(customerDao).updateCustomer(capturedCustomer);
    }

    @Test
    void willUpdateOnlyEmail() {
        // given
        int id=1;
        String name="Alex";
        String email="alex@gmail.com";
        int age=28;
        Gender gender=Gender.MALE;
        Customer customer=new Customer(id, name, email, age, gender);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request=new CustomerUpdateRequest(null,"alex.to@gmail.com",null,null);

        // when
        underTest.updateCustomer(id,request);

        ArgumentCaptor<Customer> customerArgumentCaptor=ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer=customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());


        verify(customerDao).updateCustomer(capturedCustomer);
    }

    @Test
    void willThrowWhenUpdatingEmailToAnotherExistingEmail() {
        // given
        int id=1;
        String name="Alex";
        String email="alex@gmail.com";
        int age=28;
        Gender gender=Gender.MALE;
        Customer customer=new Customer(id, name, email, age, gender);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newExistingEmail="jamila@gmail.com";
        CustomerUpdateRequest request=new CustomerUpdateRequest(name, newExistingEmail, age, gender);
        when(customerDao.existsPersonWithEmail(newExistingEmail)).thenReturn(true);

        // then
        assertThatThrownBy(()->underTest.updateCustomer(id,request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");
        verify(customerDao,never()).updateCustomer(any());
    }

    @Test
    void canUpdateAllCustomerProperties() {
        // given
        int id=1;
        String name="Alex";
        String email="alex@gmail.com";
        int age=28;
        Gender gender=Gender.MALE;
        Customer customer=new Customer(id, name, email, age, gender);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request=new CustomerUpdateRequest("jamila","jamila@gmail.com",38, Gender.FEMALE);

        // when
        underTest.updateCustomer(id,request);

        ArgumentCaptor<Customer> customerArgumentCaptor=ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer=customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(request.gender());

        verify(customerDao).updateCustomer(capturedCustomer);
    }

    @Test
    void willUpdateOnlyAge() {
        // given
        int id=1;
        String name="Alex";
        String email="alex@gmail.com";
        int age=28;
        Gender gender=Gender.MALE;
        Customer customer=new Customer(id, name, email, age, gender);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request=new CustomerUpdateRequest(null,null,38, null);

        // when
        underTest.updateCustomer(id,request);

        ArgumentCaptor<Customer> customerArgumentCaptor=ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer=customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());

        verify(customerDao).updateCustomer(capturedCustomer);
    }

    @Test
    void willThrowWhenUpdatingCustomerWhileNoDataChanges() {
        // given
        int id=1;
        String name="Alex";
        String email="alex@gmail.com";
        int age=28;
        Gender gender=Gender.MALE;
        Customer customer=new Customer(id, name, email, age, gender);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request=new CustomerUpdateRequest(name,email,age, gender);

        // then
        assertThatThrownBy(()->underTest.updateCustomer(id,request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found.");

        verify(customerDao,never()).updateCustomer(any());


    }
}