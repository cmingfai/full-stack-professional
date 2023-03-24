package cc.oolong.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {
     private static List<Customer> customers;

    static {
        customers=new ArrayList<>();

        Customer alex=new Customer(1,"Alex","alex@gmail.com",22);
        customers.add(alex);

        Customer jamila=new Customer(2,"Jamila","jamila@gmail.com",19);
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
            c.setAge(customer.getAge());
        });
    }
}
