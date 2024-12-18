import org.hua.cache.LRUCache;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




class LRUCacheTest {
    @Test
    void testHeadTailOrder() {
        // Έλεγχος της σωστής σειράς στοιχείων head/tail
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        assertEquals("Three", cache.getHead());
        assertEquals("One", cache.getTail());
    }

    @Test
    void testAccessUpdatesOrder() {
        // Έλεγχος ενημέρωσης σειράς μετά από πρόσβαση σε στοιχείο
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        cache.get(1);
        assertEquals("One", cache.getHead());
        assertEquals("Two", cache.getTail());
    }

    @Test
    void testEvictionOrder() {
        // Έλεγχος σωστής απομάκρυνσης στοιχείων όταν γεμίσει η cache
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");
        cache.put(4, "Four");

        assertEquals("Four", cache.getHead());
        assertEquals("Two", cache.getTail());
        assertNull(cache.get(1));
    }

    @Test
    void testUpdateExisting() {
        // Έλεγχος ενημέρωσης υπάρχοντος στοιχείου
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(1, "One Updated");

        assertEquals("One Updated", cache.getHead());
        assertEquals("Two", cache.getTail());
    }

    @Test
    void testUpdateExisting2() {
        // Εκτεταμένος έλεγχος ενημέρωσης υπαρχόντων κλειδιών
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(1, "One Updated");

        assertEquals("One Updated", cache.get(1));
        assertEquals("Two", cache.get(2));

        cache.put(3, "Three");
        cache.put(4, "Four");

        assertNull(cache.get(1));
        assertEquals("Two", cache.get(2));
        assertEquals("Three", cache.get(3));
        assertEquals("Four", cache.get(4));
    }

    @Test
    void testFrequentAccess() {
        // Έλεγχος συμπεριφοράς με συχνή πρόσβαση στο ίδιο στοιχείο
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        for (int i = 0; i < 5; i++) {
            cache.get(1);
            assertEquals("One", cache.getHead());
        }
    }

    @Test
    void testCapacityEnforcement() {
        // Έλεγχος τήρησης ορίου χωρητικότητας
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        for (int i = 0; i < 10; i++) {
            cache.put(i, "Value" + i);
        }

        assertEquals(3, cache.size());
        assertEquals("Value9", cache.getHead());
    }

    @Test
    void testBasicFunctionality() {
        // Έλεγχος βασικής λειτουργικότητας και εκτοπισμού
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        assertEquals("One", cache.get(1));
        assertEquals("Two", cache.get(2));
        assertEquals("Three", cache.get(3));

        cache.put(4, "Four");

        assertNull(cache.get(1));
        assertEquals("Two", cache.get(2));
        assertEquals("Three", cache.get(3));
        assertEquals("Four", cache.get(4));
    }

    @Test
    void testSingleCapacity() {
        // Έλεγχος συμπεριφοράς cache μεγέθους 1
        LRUCache<Integer, String> singleCache = new LRUCache<>(1);
        singleCache.put(1, "One");
        assertEquals("One", singleCache.get(1));

        singleCache.put(2, "Two");
        assertNull(singleCache.get(1));
        assertEquals("Two", singleCache.get(2));
    }

    @Test
    void testAccessPattern() {
        // Έλεγχος μοτίβου πρόσβασης και επίδρασης στη σειρά LRU
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        cache.get(1);
        cache.get(2);

        cache.put(4, "Four");

        assertNull(cache.get(3));
        assertEquals("One", cache.get(1));
        assertEquals("Two", cache.get(2));
        assertEquals("Four", cache.get(4));
    }

    @Test
    void testLargeCapacityAndOperations() {
        // Tests cache behavior with larger capacity and many operations
       LRUCache<Integer, String> largeCache = new LRUCache<>(100);

        // Add more items than cache capacity
        for (int i = 0; i < 150; i++) {
            largeCache.put(i, "Value" + i);
        }

        // Verify last 100 items are present
        for (int i = 50; i < 150; i++) {
            assertEquals("Value" + i, largeCache.get(i));
        }

        // Verify first 50 items were evicted
        for (int i = 0; i < 50; i++) {
            assertNull(largeCache.get(i));
        }
    }

    @Test
    void testRepeatedKeyUpdateAndOrder() {
        // Tests cache behavior when repeatedly updating the same key
        // while adding other entries
        LRUCache<Integer, String> largeCache = new LRUCache<>(3);
        largeCache.put(1, "One");
        largeCache.put(2, "Two");

        for (int i = 0; i < 10; i++) {
            largeCache.put(1, "One-" + i);
            largeCache.put(i + 3, "Value" + i);

            // Verify key 1 remains in cache and has correct value
            assertEquals("One-" + i, largeCache.get(1));
        }

        // Verify key 1 is still in cache after all operations
        assertNotNull(largeCache.get(1));
    }
    @Test
    void testEmptyCache() {
        // Tests behavior of an empty cache
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        assertNull(cache.get(1), "Empty cache should return null for any key");
        assertNull(cache.get(0), "Empty cache should return null for any key");
    }

    @Test
    void testNullHandling() {
        // Δημιουργία LRUCache με χωρητικότητα 3
        LRUCache<Integer, String> cache = new LRUCache<>(3);

        // Έλεγχος ότι το null key ρίχνει NullPointerException
        assertThrows(NullPointerException.class, () -> cache.put(null, "Value"));

        // Έλεγχος ότι το null value ρίχνει NullPointerException
        assertThrows(NullPointerException.class, () -> cache.put(1, null));

        // Έλεγχος ότι η get με null κλειδί επιστρέφει null
        assertNull(cache.get(null));

        // Κανονική εισαγωγή
        cache.put(1, "One");
        cache.put(2, "Two");
        assertEquals("One", cache.get(1)); // Ελέγχουμε την τιμή

        // Cache πρέπει να παραμείνει με 2 στοιχεία
        assertEquals(2, cache.size());
    }

    @Test
    void testMixedOperations() {
        // Tests cache behavior under a mix of operations
        // Simulates realistic usage patterns
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        for (int i = 0; i < 5; i++) {
            cache.put(i, "Value" + i);

            // Intermix gets with puts
            if (i > 0) {
                cache.get(i - 1);
            }
        }

        // Verify cache state after mixed operations
        assertNull(cache.get(0), "First item should be evicted");
        assertNotNull(cache.get(4), "Last item should be present");
        assertNotNull(cache.get(3), "Recently accessed item should be present");
    }

    @Test
    void testRepeatedPutsOnSameKey() {
        // Tests repeated updates to the same key
        // Verifies that repeated updates maintain correct LRU order
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        for (int i = 0; i < 100; i++) {
            cache.put(1, "Value" + i);
        }

        assertEquals("Value99", cache.get(1));

        // Fill remaining cache
        cache.put(2, "Two");
        cache.put(3, "Three");

        // Verify key 1 wasn't evicted despite many operations
        assertEquals("Value99", cache.get(1));
    }
    @Test
    void testHeadTailPositions() {
        // Assuming we've modified LRUCache to expose these methods for testing:
        // getHead() - returns the most recently used item
        // getTail() - returns the least recently used item
        LRUCache<Integer, String> cache = new LRUCache<>(3);

        // Test initial state
        assertNull(cache.getHead(), "Head should be null in empty cache");
        assertNull(cache.getTail(), "Tail should be null in empty cache");

        // Test single insertion
        cache.put(1, "One");
        assertEquals("One", cache.getHead(), "Head should be the only item");
        assertEquals("One", cache.getTail(), "Tail should be the only item");

        // Test multiple insertions
        cache.put(2, "Two");
        cache.put(3, "Three");
        assertEquals("Three", cache.getHead(), "Head should be most recent item");
        assertEquals("One", cache.getTail(), "Tail should be oldest item");

        // Test access affecting order
        cache.get(1);  // Access oldest item
        assertEquals("One", cache.getHead(), "Head should be recently accessed item");
        assertEquals("Two", cache.getTail(), "Tail should be least recently used");

        // Test eviction
        cache.put(4, "Four");
        assertEquals("Four", cache.getHead(), "Head should be new item");
        assertEquals("Three", cache.getTail(), "Tail should be oldest remaining item");
        assertNull(cache.get(2), "Evicted item should be null");
    }

}



