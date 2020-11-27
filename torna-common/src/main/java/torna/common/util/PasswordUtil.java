package torna.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PasswordUtil {

    private static final List<Character> CHARS;

    static {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890~!@#$%^&*.?".toCharArray();
        List<Character> list = new ArrayList<>(chars.length);
        for (char c : chars) {
            list.add(c);
        }
        Collections.shuffle(list);
        CHARS = list;
    }

    public static void main(String[] args) {
        String password = getRandomPassword(32);
        System.out.println(password);
    }

    // 随机密码生成
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