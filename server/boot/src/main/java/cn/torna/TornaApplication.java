package cn.torna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.torna", "com.anji.captcha"})
public class TornaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TornaApplication.class, args);
    }

}
