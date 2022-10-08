package cn.torna.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author tanghc
 */
@Slf4j
public class GenerateUtil {

    public static String getUserPassword(String username, String password, String salt) {
        return DigestUtils.md5DigestAsHex((username + password + salt).getBytes(StandardCharsets.UTF_8));
    }

    public static String getUUID() {
        return IdGen.uuid();
    }

}
