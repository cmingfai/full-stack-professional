package cc.oolong.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJdbcDataAccessService implements  CustomerDao {
    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJdbcDataAccessService(JdbcTemplate jdbcTemplate,
                                         CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql= """
                SELECT id, name, email, password, age, gender, profile_image_id
                FROM customer
                LIMIT 50
                """;
        List<Customer> customers= jdbcTemplate.query(sql, customerRowMapper);
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {

            var sql = """
                SELECT id, name, email, password, age, gender, profile_image_id
                FROM customer
                WHERE id=?
                """;

            return jdbcTemplate.query(sql,customerRowMapper,id).stream().findFirst();

    }



    @Override
    public void insertCustomer(Customer customer) {
        var sql= """
                INSERT INTO customer (name, email, password, age, gender)
                VALUES (?, ?, ?, ?, ?)
                """;
        int update=jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getPassword(),
                customer.getAge(),
                customer.getGender().name());

        System.out.println("jdbcTemplate.update="+update);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql= """
                SELECT count(id)
                FROM customer
                WHERE email=?
                """;

        Integer count=jdbcTemplate.queryForObject(sql,Integer.class,email);

        return count!=null && count>0;
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        var sql= """
                SELECT count(id)
                FROM customer
                WHERE id=?
                """;

        Integer count=jdbcTemplate.queryForObject(sql,Integer.class,id);

        return count!=null && count>0;
    }

    @Override
    public void deleteCustomerById(Integer id) {
       var sql= """
               DELETE FROM customer
               WHERE id=?
               """;

       jdbcTemplate.update(sql,id);
    }

    @Override
    public void updateCustomer(Customer customer) {
       if (customer.getName()!=null && customer.getName().trim().length()>0) {
           jdbcTemplate.update("UPDATE customer SET name=? WHERE id=?",
                   customer.getName(),customer.getId());

       }

        if (customer.getEmail()!=null && customer.getEmail().trim().length()>1) {
            jdbcTemplate.update("UPDATE customer SET email=? WHERE id=?",
                    customer.getEmail(),customer.getId());

        }

        if (customer.getPassword()!=null && customer.getPassword().trim().length()>1) {
            jdbcTemplate.update("UPDATE customer SET password=? WHERE id=?",
                    customer.getPassword(),customer.getId());

        }

        if (customer.getAge()!=null) {
            jdbcTemplate.update("UPDATE customer SET age=? WHERE id=?",
                    customer.getAge(),customer.getId());

        }

        if (customer.getGender()!=null) {
            jdbcTemplate.update("UPDATE customer SET gender=? WHERE id=?",
                    customer.getGender().name(),customer.getId());

        }
    }

    @Override
    public void updateCustomerProfileImageId(String profileImageId, Integer customerId) {
        var sql= """
                UPDATE customer
                SET profile_image_id=?
                WHERE id=?
                """;
        jdbcTemplate.update(sql,profileImageId,customerId);
    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        var sql = """
                SELECT id, name, email, password, age, gender, profile_image_id
                FROM customer
                WHERE email=?
                """;

        return jdbcTemplate.query(sql,customerRowMapper,email).stream().findFirst();
    }
}
