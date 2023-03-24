package cc.oolong;

import cc.oolong.customer.Customer;
import cc.oolong.customer.CustomerRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@SpringBootApplication
public class Main {


    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Main.class, args);
//        printBeans(ctx);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args-> {

            Faker faker = new Faker();

            String firstName = faker.name().firstName(); // Emory
            String lastName = faker.name().lastName(); // Barton

            String fullName = "%s %s".formatted(firstName, lastName);
            String email="%s.%s@gmail.com".formatted(firstName, lastName).toLowerCase();
            Random random = new Random();
            Integer age=random.nextInt(19,55);
            Customer c = new Customer(fullName, email, age );

            customerRepository.save(c);

        };
    }

    record Foo(String name) {}

    @Bean("foo")
    public Foo getFoo() {
        return new Foo("bar");
    }

    private static void printBeans(ConfigurableApplicationContext applicationContext) {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }


}
