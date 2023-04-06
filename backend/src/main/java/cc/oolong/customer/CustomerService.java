package cc.oolong.customer;

import cc.oolong.exception.DuplicateResourceException;
import cc.oolong.exception.RequestValidationException;
import cc.oolong.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDao
                .selectCustomerById(id)
                .orElseThrow(()->
                        createResourceNotFoundException(id));
    }

    private static ResourceNotFoundException createResourceNotFoundException(Integer id) {
        return new ResourceNotFoundException("Customer with id [%s] not found".formatted(id));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        // check if email exists
        if (customerDao.existsPersonWithEmail(customerRegistrationRequest.email())) {
           throw new DuplicateResourceException("email already taken");
        }
        // add
        Customer customer = new Customer(customerRegistrationRequest.name(), customerRegistrationRequest.email(), customerRegistrationRequest.age(), customerRegistrationRequest.gender());
        customerDao.insertCustomer(customer);

    }

    public void deleteCustomer(Integer customerId) {
        boolean existsCustomer=customerDao.existsPersonWithId(customerId);
        if (!existsCustomer) throw createResourceNotFoundException(customerId);
        customerDao.deleteCustomerById(customerId);
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
}
