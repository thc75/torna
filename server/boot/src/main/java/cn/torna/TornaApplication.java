package cn.torna;

import cn.torna.service.UpgradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.torna"})
public class TornaApplication implements ApplicationRunner {

    @Autowired
    private UpgradeService upgradeService;

    public static void main(String[] args) {
        SpringApplication.run(TornaApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        upgradeService.upgrade();
    }

}
