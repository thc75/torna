package cn.torna.service.gen.dto;

import cn.torna.common.util.IdGen;
import org.apache.commons.lang.math.RandomUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

// 系统变量
public class SysVar {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");


    private final LocalDateTime localDateTime = LocalDateTime.now();

    public String getDatetime() {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }

    public String getDate() {
        return localDateTime.format(DATE_FORMATTER);
    }

    public String getTime() {
        return localDateTime.format(TIME_FORMATTER);
    }

    public int getRandomInt() {
        return RandomUtils.nextInt(Integer.MAX_VALUE);
    }

    public long getRandomLong() {
        return RandomUtils.nextLong();
    }

    public boolean getRandomBoolean() {
        return RandomUtils.nextBoolean();
    }

    public String getUuid() {
        return UUID.randomUUID().toString();
    }

    public String getUuid2() {
        return getUuid().replace("-", "");
    }

    public long getNextId() {
        return IdGen.genId();
    }
}
