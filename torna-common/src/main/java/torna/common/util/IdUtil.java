package torna.common.util;

import lombok.extern.slf4j.Slf4j;
import org.hashids.Hashids;

import java.util.UUID;

/**
 * @author tanghc
 */
@Slf4j
public class IdUtil {

    private static final Hashids hashids = new Hashids("@r9#8e.N$z>09=dG", 8);

    public static final long MAX = 9007199254740992L;

    public static String encode(Long id) {
        if (id == null || id == 0) {
            return "";
        }
        return hashids.encode(id);
    }

    public static Long decode(String id) {
        if (id == null || "".equals(id)) {
            return null;
        }
        try {
            long[] arr = hashids.decode(id);
            if (arr == null || arr.length == 0) {
                return null;
            }
            return arr[0];
        } catch (Exception e) {
            log.error("id decode error, id:{}", id, e);
            throw new IllegalArgumentException("id错误");
        }
    }

}
