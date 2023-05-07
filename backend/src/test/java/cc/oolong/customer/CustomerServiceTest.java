package cc.oolong.customer;

import cc.oolong.exception.DuplicateResourceException;
import cc.oolong.exception.RequestValidationException;
import cc.oolong.exception.ResourceNotFoundException;
import cc.oolong.s3.S3Buckets;
import cc.oolong.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private S3Buckets s3Buckets;
    @Mock
    private S3Service s3Service;

    private CustomerService underTest;
    private CustomerDTOMapper customerDTOMapper=new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest=new CustomerService(customerDao,
                customerDTOMapper,
                passwordEncoder,
                s3Buckets,
                s3Service);
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
                "password", 28,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected=customerDTOMapper.apply(customer);
        // when
        CustomerDTO actual
                = underTest.getCustomer(id);

        // then
        assertThat(actual).isEqualTo(expected);
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
       CustomerRegistrationRequest request=new CustomerRegistrationRequest(
               "Alex",
               email,
               "password",
               18,
               Gender.MALE);

        String passwordHash="$sdfsdfsd%$^$%^dfjsdklfjl";
        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);
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
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);

    }



    @Test
    void willThrowWhenAddingCustomerWithEmailExists() {
        // given
        String email="alex@gmail.com";

        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest request=new CustomerRegistrationRequest("Alex",email,"password",18, Gender.MALE);

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
        CustomerUpdateRequest request=new CustomerUpdateRequest("Alex","alex@gmail.com", "password", 28, Gender.MALE);
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
        Customer customer=new Customer(id, name, email, "password", age, gender);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request=new CustomerUpdateRequest("Jamila",null, "password", null, null);

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
        Customer customer=new Customer(id, name, email, "password", age, gender);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request=new CustomerUpdateRequest(null,"alex.to@gmail.com", "password", null,null);

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
        Customer customer=new Customer(id, name, email, "password", age, gender);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newExistingEmail="jamila@gmail.com";
        CustomerUpdateRequest request=new CustomerUpdateRequest(name, newExistingEmail, "password", age, gender);
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
        Customer customer=new Customer(id, name, email, "password", age, gender);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request=new CustomerUpdateRequest("jamila","jamila@gmail.com", "password", 38, Gender.FEMALE);

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
        Customer customer=new Customer(id, name, email, "password", age, gender);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request=new CustomerUpdateRequest(null,null, "password", 38, null);

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
        Customer customer=new Customer(id, name, email, "password", age, gender);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request=new CustomerUpdateRequest(name,email, "password", age, gender);

        // then
        assertThatThrownBy(()->underTest.updateCustomer(id,request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found.");

        verify(customerDao,never()).updateCustomer(any());
    }

    @Test
    void canUploadCustomerProfileImage() {
        // given
        int customerId=10;
        byte[] data="Hello World".getBytes();

        MultipartFile file=new MockMultipartFile("file", data);
        // when
        when(customerDao.existsPersonWithId(customerId)).thenReturn(true);

        underTest.uploadCustomerProfileImage(customerId, file);

        // then
        ArgumentCaptor<String> profileImageIdCaptor=ArgumentCaptor.forClass(String.class);
        verify(customerDao).updateCustomerProfileImageId(
                profileImageIdCaptor.capture(),
                eq(customerId));

        String key="profile-images/%s/%s".formatted(customerId, profileImageIdCaptor.getValue());
        verify(s3Service).putObject(s3Buckets.getCustomer(), key,data);
    }

    @Test
    void cannotUploadCustomerProfileImageWhenCustomerDoesNotExists() {
        // given
        int customerId=10;
        when(customerDao.existsPersonWithId(customerId)).thenReturn(false);

        // when
        // then

        assertThatThrownBy(()->
                underTest.uploadCustomerProfileImage(customerId, mock(MultipartFile.class)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(customerId))
                ;

        verify(customerDao).existsPersonWithId(customerId);
        verifyNoMoreInteractions(customerDao);
        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);


    }


    @Test
    void cannotUploadCustomerProfileImageWhenExceptionIsThrown() throws IOException {
        // given
        int customerId=10;

        when(customerDao.existsPersonWithId(customerId)).thenReturn(true);

        MultipartFile multipartFile=mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(IOException.class);


        String bucket="customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);
        // when
        // then
            assertThatThrownBy(()->{
            underTest.uploadCustomerProfileImage(customerId, multipartFile);
        }).isInstanceOf(RuntimeException.class)
                .hasMessage("failed to upload profile image")
                .hasRootCauseInstanceOf(IOException.class);

            verify(s3Service,never()).putObject(eq(bucket),any(),any());
            verify(customerDao,never()).updateCustomerProfileImageId(any(), any());

    }

    @Test
    void canDownloadImage() {
        // given
        int customerId=1;
        String profileImageId = "2222";
        Customer customer=new Customer(
                customerId,
                "foo bar",
                "foo@gmail.com",
                "password", 28,
                Gender.MALE,
                profileImageId);
        when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        byte[] expectedImage="image".getBytes();
        String bucket="customer-bucket";

        when(s3Buckets.getCustomer()).thenReturn(bucket);
        when(s3Service.getObject(s3Buckets.getCustomer(),
                        "profile-images/%s/%s".formatted(customerId, profileImageId)))
                .thenReturn(expectedImage);
        // when

        byte[] actualImage=underTest.getCustomerProfileImage(customerId);

        // then
        assertThat(expectedImage).isEqualTo(actualImage);

    }

    @Test
    void cannotDownloadImageWhenNoProfileImageId() {
        // given
        int customerId=1;
         Customer customer=new Customer(
                customerId,
                "foo bar",
                "foo@gmail.com",
                "password", 28,
                Gender.MALE);
        when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        // when
        // then

       assertThatThrownBy(
               ()-> underTest.getCustomerProfileImage(customerId)
       ).isInstanceOf(ResourceNotFoundException.class)
               .hasMessage("Customer with id [%s] profile image not found".formatted(customerId));
       verifyNoInteractions(s3Service);
       verifyNoInteractions(s3Buckets);

    }

    @Test
    void cannotDownloadImageWhenCustomerDoesNotExists() {
        // given
        int customerId=1;

        when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.empty());

        // when
        // then

        assertThatThrownBy(
                ()-> underTest.getCustomerProfileImage(customerId)
        ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(customerId));
        verifyNoInteractions(s3Service);
        verifyNoInteractions(s3Buckets);

    }
}