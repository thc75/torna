package cn.torna;

import cn.torna.service.UpgradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {"cn.torna"})
public class TornaApplication implements ApplicationRunner {

    @Autowired
    private UpgradeService upgradeService;

    @Value("${torna.time-zone:}")
    private String timeZone;

    public static void main(String[] args) {
        SpringApplication.run(TornaApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        initTimeZone();
        upgradeService.upgrade();
    }

    private void initTimeZone() {
        if (StringUtils.hasText(timeZone)) {
            TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
        }
    }
}
