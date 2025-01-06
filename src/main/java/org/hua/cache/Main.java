package org.hua.cache;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Test both policies
        testPolicy(CacheReplacementPolicy.LRU);
        System.out.println("\n-------------------\n");
        testPolicy(CacheReplacementPolicy.MRU);
    }

    private static void testPolicy(CacheReplacementPolicy policy) {
        System.out.println("Testing " + policy.getDescription());
        LRUCache<Integer, String> cache = new LRUCache<>(20, policy);
        Random random = new Random();
        int operations = 100000;

        for (int i = 0; i < operations; i++) {
            int key;
            if (random.nextDouble() < 0.8) {
                key = random.nextInt(20);
            } else {
                key = random.nextInt(80) + 20;
            }

            String value = cache.get(key);
            if (value == null) {
                cache.put(key, "Value" + key);
            }
        }

        System.out.println("Total operations: " + operations);
        System.out.println("Cache Hits: " + cache.getHitCount());
        System.out.println("Cache Misses: " + cache.getMissCount());
        System.out.printf("Hit Rate: %.2f%%\n", cache.getHitRate());
        System.out.printf("Miss Rate: %.2f%%\n", cache.getMissRate());
    }
}
