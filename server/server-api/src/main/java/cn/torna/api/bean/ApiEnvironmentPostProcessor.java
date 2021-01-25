package cn.torna.api.bean;

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
public class ApiEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private final Properties properties = new Properties();

    private static final String FILE_CLASS_PATH = "META-INF/api.properties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource resource = new ClassPathResource(FILE_CLASS_PATH);
        // 加载成PropertySource对象，并添加到Environment环境中
        environment.getPropertySources().addLast(loadProfiles(resource));
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
