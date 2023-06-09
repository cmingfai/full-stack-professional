package cc.oolong;

import cc.oolong.customer.Customer;
import cc.oolong.customer.CustomerRepository;
import cc.oolong.customer.Gender;
import cc.oolong.s3.S3Buckets;
import cc.oolong.s3.S3Service;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class Main {


    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Main.class, args);
//        printBeans(ctx);
    }


    @Bean
    @ConditionalOnProperty(
            prefix = "command.line.runner",
            value = "enabled",
            havingValue = "true",
            matchIfMissing = true)
    CommandLineRunner runner(CustomerRepository customerRepository,
                             PasswordEncoder passwordEncoder,
                             S3Service s3Service,
                             S3Buckets s3Buckets) {
        return args-> {
            createRandomCustomer(customerRepository, passwordEncoder);

//            s3BucketsTest(s3Service, s3Buckets);
        };
    }

    private static void s3BucketsTest(S3Service s3Service, S3Buckets s3Buckets) {
        String key = "foo/bar";
        String bucketName = s3Buckets.getCustomer();
        s3Service.putObject(bucketName,
                key,
                  "Hello World".getBytes());

        byte[] obj= s3Service.getObject(bucketName,
                key);

        System.out.println("Hooray: "+new String(obj));
    }

    private static void createRandomCustomer(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        Random rand = new Random();
        Faker faker = new Faker();
        String firstName = faker.name().firstName(); // Emory
        String lastName = faker.name().lastName(); // Barton

        String fullName = "%s %s".formatted(firstName, lastName);
        String email = "%s.%s@gmail.com".formatted(firstName, lastName).toLowerCase();
        Random random = new Random();
        Integer age = random.nextInt(19, 55);

        Gender gender=rand.nextInt() % 2==0? Gender.MALE:Gender.FEMALE;
        Customer c = new Customer(fullName, email, passwordEncoder.encode("password"), age, gender);

        customerRepository.save(c);

        System.out.println(email);
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
