package testcase.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class BalanceSearchAPIStarter {

    public static void main(String[] args) {
        SpringApplication.run(BalanceSearchAPIStarter.class, args);
    }
}
