package org.hua.cache;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Διαφορετικά μεγέθη cache για ανάλυση απόδοσης
        int[] cacheSizes = {10, 20, 40};

        System.out.println("Cache Performance Analysis");
        System.out.println("=========================\n");

        for (int capacity : cacheSizes) {
            System.out.println("Cache Capacity: " + capacity);
            System.out.println("----------------");

            // Δοκιμή LRU
            System.out.println("\nLRU (Least Recently Used) Strategy:");
            testPolicy(capacity , CacheReplacementPolicy.LRU);

            // Δοκιμή MRU
            System.out.println("\nMRU (Most Recently Used) Strategy:");
            testPolicy(capacity, CacheReplacementPolicy.MRU);

            System.out.println("\n=========================\n");
        }
    }

    private static void testPolicy(int capacity, CacheReplacementPolicy policy ) {
        System.out.println("Testing " + policy.getDescription());

        // Δημιουργία cache με συγκεκριμένη στρατηγική και χωρητικότητα
        MyCache<Integer, String> cache = new MyCache<>(capacity, policy);
        Random random = new Random();
        int operations = 100000;

        for (int i = 0; i < operations; i++) {
            int key;
            if (random.nextDouble() < 0.8) {
                key = random.nextInt(20); // 80% αιτήματα για hot keys (0-19)
            } else {
                key = random.nextInt(80) + 20; // 20% αιτήματα για cold keys (20-99)
            }

            // Ανάκτηση ή εισαγωγή στοιχείου
            String value = cache.get(key);
            if (value == null) {
                cache.put(key, "Value" + key);
            }
        }

        // Εκτύπωση αποτελεσμάτων
        System.out.println("Total operations: " + operations);
        System.out.println("Cache Hits: " + cache.getHitCount());
        System.out.println("Cache Misses: " + cache.getMissCount());
        System.out.printf("Hit Rate: %.2f%%\n", cache.getHitRate());
        System.out.printf("Miss Rate: %.2f%%\n", cache.getMissRate());
    }
}
