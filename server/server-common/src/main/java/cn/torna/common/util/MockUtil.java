package cn.torna.common.util;

import com.gitee.fastmybatis.core.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author thc
 */
@Slf4j
public class MockUtil {

    private static final int LIMIT_VERSION = 55;
    private static final String JAVA_CLASS_VERSION = "java.class.version";

    private static final String FACTORY_IN_JDK8 = "jdk.nashorn.api.scripting.NashornScriptEngineFactory";
    private static final String FACTORY_SINCE_JDK11 = "org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory";

    private static ScriptEngine engine;

    static float javaVersion;

    static {
        String version = System.getProperty(JAVA_CLASS_VERSION);
        javaVersion = Float.parseFloat(version);
    }

    static {
        // 初始化脚本引擎
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager(new MyClassLoader());
        try {
            Class<?> clazz = javaVersion < LIMIT_VERSION ? Class.forName(FACTORY_IN_JDK8)
                    : Class.forName(FACTORY_SINCE_JDK11);
            scriptEngineManager.registerEngineName("mockRandomJs", (ScriptEngineFactory) ClassUtil.newInstance(clazz));
        } catch (Exception e) {
            log.error("Init ScriptEngine error", e);
        }
        engine = scriptEngineManager.getEngineByName("js");
        if (null == engine) {
            engine = scriptEngineManager.getEngineByMimeType("js");
        }
    }

    private static final String FUN = "(function(){\n %s \n})()";

    private static volatile boolean init = false;

    /**
     * 调用mockJs生成模拟数据.
     *
     * @param script js脚本
     * @throws ScriptException 脚本书写错误将会发生异常
     */
    public static Object mock(String script) throws ScriptException {
        if (!init) {
            synchronized (MockUtil.class) {
                if (!init) {
                    loadLocalJs("mock-min.js");
                    loadLocalJs("moment.min.js");
                    init = true;
                }
            }
        }
        String fn = String.format(FUN, script);
        return engine.eval("JSON.stringify(" + fn + ")");
    }

    private static void loadLocalJs(String filename) throws ScriptException {
        ClassPathResource classPathResource = new ClassPathResource(filename);
        String jsContent = null;
        try {
            jsContent = IOUtils.toString(classPathResource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ScriptException("load mock-min.js error");
        }
        engine.eval(jsContent);
    }

    // 自定义一个ClassLoader，避免加载class报错
    private static final class MyClassLoader extends ClassLoader {

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            // 如果是低于java11(55)，始终返回jdk.nashorn.api.scripting.NashornScriptEngineFactory
            if (javaVersion < LIMIT_VERSION && FACTORY_SINCE_JDK11.equals(name)) {
                return Class.forName(FACTORY_IN_JDK8);
            }
            return super.loadClass(name, resolve);
        }
    }

}
