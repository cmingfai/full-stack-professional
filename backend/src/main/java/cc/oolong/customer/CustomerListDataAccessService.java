package cc.oolong.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {
     private static List<Customer> customers;

    static {
        customers=new ArrayList<>();

        Customer alex=new Customer(1,
                "Alex",
                "alex@gmail.com",
                "password",
                22,
                Gender.MALE);
        customers.add(alex);

        Customer jamila=new Customer(2,
                "Jamila",
                "jamila@gmail.com",
                "password",
                19,
                Gender.FEMALE);
        customers.add(jamila);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customers.stream().filter(c->c.getId().equals(id))
                .findFirst();
    }



    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customers.stream().anyMatch(c->c.getEmail().equals(email));
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        return customers.stream().anyMatch(c->c.getId().equals(id));
    }

    @Override
    public void deleteCustomerById(Integer id) {
//        customers=customers.stream().filter(c->!c.getId().equals(id)).collect(toList());
          customers.stream().filter(c->c.getId().equals(id)).findFirst().ifPresent(customers::remove);
    }

    @Override
    public void updateCustomer(Customer customer) {
        customers.stream().filter(c->c.getId().equals(customer.getId())).findFirst().ifPresent(c-> {
            c.setName(customer.getName());
            c.setEmail(customer.getEmail());
            c.setPassword(customer.getPassword());
            c.setAge(customer.getAge());
            c.setGender(customer.getGender());
        });
    }

    @Override
    public void updateCustomerProfileImageId(String profileImageId, Integer customerId) {
        // TODO: implement this
    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        return customers.stream().filter(c->c.getUsername().equals(email))
                .findFirst();
    }
}
