package tests;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class Randomer {

    private final static Random RANDOM = new SecureRandom();

    public static <T> T random(List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }

    public static <T> T random(T... list) {
        return list[RANDOM.nextInt(list.length)];
    }

    public static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }

}
