package cn.torna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * 自定义环境处理，在运行SpringApplication之前加载任意配置文件到Environment环境中
 * https://www.jianshu.com/p/be6c818fe6ff
 */
public class TornaEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private final Properties properties = new Properties();

    private static final String[] PROPERTIES_FILES = {
            "META-INF/torna.properties",
    };

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        for (String propertiesFile : PROPERTIES_FILES) {
            Resource resource = new ClassPathResource(propertiesFile);
            // 加载成PropertySource对象，并添加到Environment环境中
            environment.getPropertySources().addLast(loadProfiles(resource));
        }
    }

    private PropertySource<?> loadProfiles(Resource resource) {
        if (resource == null || !resource.exists()) {
            throw new IllegalArgumentException("资源" + resource + "不存在");
        }
        try {
            properties.load(resource.getInputStream());
            return new PropertiesPropertySource(resource.getFilename(), properties);
        } catch (IOException ex) {
            throw new IllegalStateException("加载配置文件失败" + resource, ex);
        }
    }
}
