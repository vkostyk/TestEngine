package vk.testeng.service;

import java.util.List;
import java.util.Random;

public class Shuffle {
    public static <T> void withSeed(List<T> array, long seed)
    {
        Random rnd = new Random(seed);
        for (int i = array.size() - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            T a = array.get(index);
            array.set(index, array.get(i));
            array.set(i, a);
        }
    }
}
