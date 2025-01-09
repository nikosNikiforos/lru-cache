import org.hua.cache.AbstractCache;
import org.hua.cache.Cache;
import org.hua.cache.CacheReplacementPolicy;
import org.hua.cache.MyCache;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.hua.cache.Counter;




class MyCacheTest {
    @Test
    void testHeadTailOrder() {
        // Έλεγχος της σωστής σειράς στοιχείων head/tail
        MyCache<Integer, String> cache = new MyCache<>(3, CacheReplacementPolicy.MRU);
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
        //Έλεγχος για μεγάλο capacity και πολλές λειτουργίες
        LRUCache<Integer, String> largeCache = new LRUCache<>(100);

        // Προσθήκη περισσότερων στοιχείων απο τη χωρητικότητα
        for (int i = 0; i < 150; i++) {
            largeCache.put(i, "Value" + i);
        }

        //Έλεγχος για τα τελευταία 100
        for (int i = 50; i < 150; i++) {
            assertEquals("Value" + i, largeCache.get(i));
        }

        //Επαλήθευση απομάκρυνσης των πρώτων
        for (int i = 0; i < 50; i++) {
            assertNull(largeCache.get(i));
        }
    }

    @Test
    void testRepeatedKeyUpdateAndOrder() {
        // Έλεγχος για επαναλαμβανόμενη ενημέρωση του ίδιου κλειδιού ενώ παράλληλα γίνονται άλλες προσθήκες

        LRUCache<Integer, String> bigCache = new LRUCache<>(3);
        bigCache.put(1, "One");
        bigCache.put(2, "Two");

        for (int i = 0; i < 10; i++) {
            bigCache.put(1, "One-" + i);
            bigCache.put(i + 3, "Value" + i);

            //Έλεγχος οτι υπάρχει στη μνήμη με σωστή τιμή
            assertEquals("One-" + i, bigCache.get(1));
        }


    }
    @Test
    void testEmptyCache() {
        // Δοκιμή για κενή μνήμη
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        assertNull(cache.get(1), "Empty cache so any key is null");
        assertNull(cache.get(0), "Empty cache so any key is null");
    }

    @Test
    void testNullHandling() {
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

        //Πρέπει να παραμείνει με 2 στοιχεία
        assertEquals(2, cache.size());
    }

    @Test
    void testMixedOperations() {
        //Έλεγχος υπό διάφορα operations
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        for (int i = 0; i < 5; i++) {
            cache.put(i, "Value" + i);

            // Intermix gets with puts
            if (i > 0) {
                cache.get(i - 1);
            }
        }

        //
        assertNull(cache.get(0), "First item should be out");
        assertNotNull(cache.get(4), "Last item should be in cache");
        assertNotNull(cache.get(3), "Recently accessed item should be in cache");
    }

    @Test
    void testRepeatedPutsOnSameKey() {
        //Ελέγχει την επαναλαμβανόμενη τοποθέτηση στο ίδιο κλειδί

        LRUCache<Integer, String> cache = new LRUCache<>(3);
        for (int i = 0; i < 100; i++) {
            cache.put(1, "Value" + i);
        }

        assertEquals("Value99", cache.get(1));

        // Fill remaining cache
        cache.put(2, "Two");
        cache.put(3, "Three");

        // Ελέγχει οτι το κλειδί 1 δεν βγήκε από τη μνήμη
        assertEquals("Value99", cache.get(1));
    }
    @Test
    void testHeadTailPositions() {
        //Εξετάζουμε πως όντως παίρνουμε σωστά την κορυφή και την ουρά της μνήμης μέσω των getHead/Tail αντίστοιχα
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        // Αρχικός Έλεγχος
        assertNull(cache.getHead(), "Head is null in empty cache");
        assertNull(cache.getTail(), "Tail is null in empty cache");

        // Μοναδική εισαγωγή
        cache.put(1, "One");
        assertEquals("One", cache.getHead(), "Head should be the only item");
        assertEquals("One", cache.getTail(), "Tail should be the only item");

        //Πολλαπλές εισαγωγές
        cache.put(2, "Two");
        cache.put(3, "Three");
        assertEquals("Three", cache.getHead(), "Head should be most recent item");
        assertEquals("One", cache.getTail(), "Tail should be oldest item");

        // Αφού πειράξουμε τη σειρά
        cache.get(1);  // Access oldest item
        assertEquals("One", cache.getHead(), "Head should be recently accessed item");
        assertEquals("Two", cache.getTail(), "Tail should be least recently used");

        // Έλεγχος νέας προσθήκης και διαγραφής LRU κόμβου
        cache.put(4, "Four");
        assertEquals("Four", cache.getHead(), "Head should be new item");
        assertEquals("Three", cache.getTail(), "Tail should be oldest remaining item");
        assertNull(cache.get(2), "Kicked item should be null");
    }

}




