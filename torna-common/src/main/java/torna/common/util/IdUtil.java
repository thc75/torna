package torna.common.util;

import lombok.extern.slf4j.Slf4j;
import org.hashids.Hashids;

/**
 * @author tanghc
 */
@Slf4j
public class IdUtil {

    private static final Hashids hashids = new Hashids("@r9#8e.N$z>09=dG", 8);

    public static String encode(long id) {
        if (id == 0) {
            return "";
        }
        return hashids.encode(id);
    }

    public static long decode(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id不能为空");
        }
        if ("".equals(id)) {
            return 0;
        }
        try {
            long[] arr = hashids.decode(id);
            if (arr == null || arr.length == 0) {
                throw new IllegalArgumentException("id错误");
            }
            return arr[0];
        } catch (Exception e) {
            log.error("id decode error, id:{}", id, e);
            throw new IllegalArgumentException("id错误");
        }
    }

}
