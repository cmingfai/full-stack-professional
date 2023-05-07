package cc.oolong.customer;

import cc.oolong.exception.DuplicateResourceException;
import cc.oolong.exception.RequestValidationException;
import cc.oolong.exception.ResourceNotFoundException;
import cc.oolong.s3.S3Buckets;
import cc.oolong.s3.S3Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
public class CustomerService {
    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;
    private final S3Buckets s3Buckets;
    private final S3Service s3Service;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao,
                           CustomerDTOMapper customerDTOMapper,
                           PasswordEncoder passwordEncoder,
                           S3Buckets s3Buckets,
                           S3Service s3Service) {
        this.customerDao = customerDao;
        this.customerDTOMapper = customerDTOMapper;
        this.passwordEncoder = passwordEncoder;

        this.s3Buckets = s3Buckets;
        this.s3Service = s3Service;
    }

    public List<CustomerDTO> getAllCustomers() {

        return customerDao.selectAllCustomers()
                .stream()
                .map(customerDTOMapper).collect(toList());
    }

    public CustomerDTO getCustomer(Integer id) {
        return customerDao
                .selectCustomerById(id).map(customerDTOMapper)
                .orElseThrow(()->
                        createResourceNotFoundException(id));
    }

    private static ResourceNotFoundException createResourceNotFoundException(Integer id) {
        return new ResourceNotFoundException("Customer with id [%s] not found".formatted(id));
    }

    private static ResourceNotFoundException createEmailNotFoundException(String email) {
        return new ResourceNotFoundException("Customer with email [%s] not found".formatted(email));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        // check if email exists
        if (customerDao.existsPersonWithEmail(customerRegistrationRequest.email())) {
           throw new DuplicateResourceException("email already taken");
        }
        // add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender());
        customerDao.insertCustomer(customer);

    }

    public void deleteCustomer(Integer customerId) {
        checkIfCustomerExistsOrThrow(customerId);
        customerDao.deleteCustomerById(customerId);
    }

    private void checkIfCustomerExistsOrThrow(Integer customerId) {
        boolean existsCustomer=customerDao.existsPersonWithId(customerId);
        if (!existsCustomer) throw createResourceNotFoundException(customerId);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest request) {
        // check if customer exists by id
        Customer customer=customerDao
                .selectCustomerById(customerId)
                .orElseThrow(()->
                        createResourceNotFoundException(customerId));

        // check if any updated value present in request
        String name=request.name();
        String email=request.email();
        Integer age=request.age();
        Gender gender=request.gender();

        boolean isNameChanged=name!=null && !name.equals(customer.getName());
        boolean isEmailChanged=email!=null && !email.equals(customer.getEmail());
        boolean isAgeChanged=age!=null && !age.equals(customer.getAge());
        boolean isGenderChanged=gender!=null && !gender.equals(customer.getGender());
        boolean isDataChangesFound=isNameChanged || isEmailChanged || isAgeChanged || isGenderChanged;

        if (!isDataChangesFound) {
            throw new RequestValidationException("No data changes found.");
        }

        if (isNameChanged) {
            customer.setName(name);
        }

        if (isEmailChanged)  {
            if (customerDao.existsPersonWithEmail(email)) {
                throw new DuplicateResourceException("email already taken");
            }
            customer.setEmail(email);
        }

        if (isAgeChanged)  {
            customer.setAge(age);
        }

        if (isGenderChanged)  {
            customer.setGender(gender);
        }

        customerDao.updateCustomer(customer);
    }

    public CustomerDTO getCustomerByEmail(String email) {
        return customerDao
                .selectUserByEmail(email).map(customerDTOMapper)
                .orElseThrow(()->
                        createEmailNotFoundException(email));
    }

    public byte[] getCustomerProfileImage(Integer customerId) {
        CustomerDTO customer=customerDao
                .selectCustomerById(customerId)
                .map(customerDTOMapper)
                .orElseThrow(()->
                        createResourceNotFoundException(customerId));

        var profileImageId=customer.profileImageId();
        if (StringUtils.isBlank(profileImageId)) {
            throw new ResourceNotFoundException(
                    "Customer with id [%s] profile image not found".formatted(customerId));
        }

        byte[] profileImage = s3Service.getObject(s3Buckets.getCustomer(),
                "profile-images/%s/%s".formatted(customerId, profileImageId));
        return profileImage;
    }

    public void uploadCustomerProfileImage(Integer customerId, MultipartFile file) {
        checkIfCustomerExistsOrThrow(customerId);
        String profileImageId = UUID.randomUUID().toString();
        try {
            s3Service.putObject(
                    s3Buckets.getCustomer(),
                    "profile-images/%s/%s".formatted(customerId, profileImageId),file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("failed to upload profile image", e);
        }

        this.customerDao.updateCustomerProfileImageId(profileImageId,customerId);
    }
}
