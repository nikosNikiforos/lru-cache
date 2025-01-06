package org.hua.cache;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Ορίζουμε τρία διαφορετικά μεγέθη cache για να συγκρίνουμε την απόδοση
        // Επιλέγουμε 10 (μικρό), 20 (μεσαίο) και 40 (μεγάλο) για να δούμε πώς
        // επηρεάζει το μέγεθος την απόδοση
        int[] cacheSizes = {10, 20, 40};

        System.out.println("Cache Performance Analysis");
        System.out.println("=========================\n");

        // Εκτελούμε τα tests για κάθε μέγεθος cache
        for (int capacity : cacheSizes) {
            System.out.println("Cache Capacity: " + capacity);
            System.out.println("----------------");

            // Δοκιμή LRU
            System.out.println("\nLRU (Least Recently Used) Strategy:");
            testPolicy(CacheReplacementPolicy.LRU, capacity);

            // Δοκιμή  MRU
            System.out.println("\nMRU (Most Recently Used) Strategy:");
            testPolicy(CacheReplacementPolicy.MRU, capacity);

            System.out.println("\n=========================\n");
        }
    }

    private static void testPolicy(CacheReplacementPolicy policy, int capacity) {
        System.out.println("Testing " + policy.getDescription());
        // Δημιουργία νέας cache με το καθορισμένο μέγεθος και πολιτική
        LRUCache<Integer, String> cache = new LRUCache<>(capacity, policy);
        Random random = new Random();
        int operations = 100000;

        // Εκτέλεση προσομοίωσης με κατανομή 80/20
        for (int i = 0; i < operations; i++) {
            int key;
            // Υλοποίηση της κατανομής 80/20:
            // 80% των αιτημάτων πηγαίνουν στο 20% των κλειδιών (0-19)
            // 20% των αιτημάτων πηγαίνουν στο υπόλοιπο 80% των κλειδιών (20-99)
            if (random.nextDouble() < 0.8) {
                key = random.nextInt(20);  // Παράγει αριθμούς 0-19
            } else {
                key = random.nextInt(80) + 20;  // Παράγει αριθμούς 20-99
            }

            // Προσπαθούμε πρώτα να πάρουμε την τιμή
            String value = cache.get(key);
            // Αν δεν υπάρχει (cache miss), την προσθέτουμε
            if (value == null) {
                cache.put(key, "Value" + key);
            }
        }

        // Εκτύπωση των αποτελεσμάτων στη μορφή που ζητείται από την εργασία
        System.out.println("Total operations: " + operations);
        System.out.println("Cache Hits: " + cache.getHitCount());
        System.out.println("Cache Misses: " + cache.getMissCount());
        System.out.printf("Hit Rate: %.2f%%\n", cache.getHitRate());
        System.out.printf("Miss Rate: %.2f%%\n", cache.getMissRate());
    }
}