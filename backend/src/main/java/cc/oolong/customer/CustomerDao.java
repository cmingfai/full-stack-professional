package cc.oolong.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer id);
    Optional<Customer> selectUserByEmail(String email);
    void insertCustomer(Customer customer);
    boolean existsPersonWithEmail(String email);
    boolean existsPersonWithId(Integer id);
    void deleteCustomerById(Integer id);
    void updateCustomer(Customer customer);
    void updateCustomerProfileImageId(String profileImageId, Integer customerId);
}
