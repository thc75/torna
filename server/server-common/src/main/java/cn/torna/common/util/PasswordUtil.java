package cn.torna.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PasswordUtil {

    private static final List<Character> CHARS;
    private static final List<Character> SIMPLE_CHARS;

    static {
        CHARS = getCharList("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890~!@#$%^&*-=,.<>");
        SIMPLE_CHARS = getCharList("abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789");
    }

    private static List<Character> getCharList(String str) {
        char[] chars = str.toCharArray();
        List<Character> list = new ArrayList<>(chars.length);
        for (char c : chars) {
            list.add(c);
        }
        Collections.shuffle(list);
        return list;
    }

    public static void main(String[] args) {
        String password = getRandomPassword(32);
        System.out.println(password);
    }

    /**
     * 随机密码生成，仅字母数字
     * @param len 密码长度，必须大于等于6
     */
    public static String getRandomSimplePassword(int len) {
        if (len < 4) {
            throw new IllegalArgumentException("'len' must >= 4");
        }
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int x = 0; x < len; ++x) {
            sb.append(SIMPLE_CHARS.get(r.nextInt(SIMPLE_CHARS.size())));
        }
        return sb.toString();
    }

    /**
     * 随机密码生成
     * @param len 密码长度，必须大于等于6
     */
    public static String getRandomPassword(int len) {
        if (len < 6) {
            throw new IllegalArgumentException("'len' must >= 6");
        }
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int x = 0; x < len; ++x) {
            sb.append(CHARS.get(r.nextInt(CHARS.size())));
        }
        return sb.toString();
    }
}