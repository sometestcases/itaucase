package testcase.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class BalanceManagerAPIStarter {

    public static void main(String[] args) {
        SpringApplication.run(BalanceManagerAPIStarter.class, args);
    }
}
