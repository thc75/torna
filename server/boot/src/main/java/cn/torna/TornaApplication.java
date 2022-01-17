package cn.torna;

import cn.torna.service.UpgradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.torna"})
@Slf4j
public class TornaApplication implements ApplicationRunner {

    @Autowired
    private UpgradeService upgradeService;

    public static void main(String[] args) {
        SpringApplication.run(TornaApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            upgradeService.upgrade();
        } catch (Exception e) {
            log.error("upgrade error", e);
            System.exit(0);
        }
    }

}
