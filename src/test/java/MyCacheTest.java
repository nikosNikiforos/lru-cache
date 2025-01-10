import org.hua.cache.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;

class MyCacheTest {
    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testHeadTailOrder(CacheReplacementPolicy policy) {
        // Έλεγχος της σωστής σειράς στοιχείων head/tail
        MyCache<Integer, String> cache = new MyCache<>(3, policy);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        assertEquals("Three", cache.getHead());
        assertEquals("One", cache.getTail());
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testAccessUpdatesOrder(CacheReplacementPolicy policy) {
        // Έλεγχος ενημέρωσης σειράς μετά από πρόσβαση σε στοιχείο
        MyCache<Integer, String> cache = new MyCache<>(3, policy);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        cache.get(1);
        assertEquals("One", cache.getHead());
        assertEquals("Two", cache.getTail());
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testEvictionOrder(CacheReplacementPolicy policy) {
        // Έλεγχος σωστής απομάκρυνσης στοιχείων όταν γεμίσει η cache
        MyCache<Integer, String> cache = new MyCache<>(3, policy);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");
        cache.put(4, "Four");

        assertEquals("Four", cache.getHead());
        if (policy == CacheReplacementPolicy.LRU) {
            assertEquals("Two", cache.getTail());
            assertNull(cache.get(1));
        } else {
            assertEquals("One", cache.getTail());
            assertNull(cache.get(3));
        }
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testUpdateExisting(CacheReplacementPolicy policy) {
        // Έλεγχος ενημέρωσης υπάρχοντος στοιχείου
        MyCache<Integer, String> cache = new MyCache<>(3, policy);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(1, "One Updated");

        assertEquals("One Updated", cache.getHead());
        assertEquals("Two", cache.getTail());
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testUpdateExisting2(CacheReplacementPolicy policy) {
        // Εκτεταμένος έλεγχος ενημέρωσης υπαρχόντων κλειδιών
        MyCache<Integer, String> cache = new MyCache<>(3, policy);


        cache.put(1, "One");
        cache.put(2, "Two");


        cache.put(1, "One Updated");

        // Έλεγχος ότι και τα δύο στοιχεία υπάρχουν αφού η μνήμη δεν έχει γεμίσει
        assertEquals("One Updated", cache.get(1));
        assertEquals("Two", cache.get(2));


        cache.put(3, "Three");
        cache.put(4, "Four");

        if (policy == CacheReplacementPolicy.LRU) {
            // Για LRU, το παλαιότερο στοιχείο πρέπει να εκτοπιστεί
            assertNull(cache.get(1), "LRU should evict least recently used");
            assertEquals("Three", cache.get(3));
            assertEquals("Four", cache.get(4));
        } else {
            // Για MRU, το πιο πρόσφατα χρησιμοποιημένο στοιχείο πριν το 4 πρέπει να εκτοπιστεί
            assertNull(cache.get(3), "MRU should evict most recently used");
            assertEquals("One Updated", cache.get(1));
            assertEquals("Four", cache.get(4));
        }
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testFrequentAccess(CacheReplacementPolicy policy) {
        // Έλεγχος συμπεριφοράς με συχνή πρόσβαση στο ίδιο στοιχείο
        MyCache<Integer, String> cache = new MyCache<>(3, policy);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        for (int i = 0; i < 5; i++) {
            cache.get(1);
            assertEquals("One", cache.getHead());
        }
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testCapacityEnforcement(CacheReplacementPolicy policy) {
        // Έλεγχος τήρησης ορίου χωρητικότητας
        MyCache<Integer, String> cache = new MyCache<>(3, policy);
        for (int i = 0; i < 10; i++) {
            cache.put(i, "Value" + i);
        }

        assertEquals(3, cache.size());
        assertEquals("Value9", cache.getHead());
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testBasicFunctionality(CacheReplacementPolicy policy) {
        // Έλεγχος βασικής λειτουργικότητας και εκτοπισμού
        MyCache<Integer, String> cache = new MyCache<>(3, policy);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        assertEquals("One", cache.get(1));
        assertEquals("Two", cache.get(2));
        assertEquals("Three", cache.get(3));

        cache.put(4, "Four");

        if (policy == CacheReplacementPolicy.LRU) {
            assertNull(cache.get(1));
            assertEquals("Two", cache.get(2));
        } else {
            assertNull(cache.get(3));
            assertEquals("One", cache.get(1));
        }
        assertEquals("Four", cache.get(4));
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testSingleCapacity(CacheReplacementPolicy policy) {
        // Έλεγχος συμπεριφοράς cache μεγέθους 1
        MyCache<Integer, String> singleCache = new MyCache<>(1, policy);
        singleCache.put(1, "One");
        assertEquals("One", singleCache.get(1));

        singleCache.put(2, "Two");
        assertNull(singleCache.get(1));
        assertEquals("Two", singleCache.get(2));
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testAccessPattern(CacheReplacementPolicy policy) {
        // Έλεγχος μοτίβου πρόσβασης και επίδρασης στη σειρά LRU
        MyCache<Integer, String> cache = new MyCache<>(3, policy);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        cache.get(1);
        cache.get(2);

        cache.put(4, "Four");

        if (policy == CacheReplacementPolicy.LRU) {
            assertNull(cache.get(3));
            assertEquals("One", cache.get(1));
            assertEquals("Two", cache.get(2));
        } else {
            assertNull(cache.get(2));
            assertEquals("Three", cache.get(3));
            assertEquals("One", cache.get(1));
        }
        assertEquals("Four", cache.get(4));
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testLargeCapacityAndOperations(CacheReplacementPolicy policy) {
        //Έλεγχος για μεγάλο capacity και πολλές λειτουργίες
        MyCache<Integer, String> largeCache = new MyCache<>(100, policy);

        for (int i = 0; i < 150; i++) {
            largeCache.put(i, "Value" + i);
        }

        if (policy == CacheReplacementPolicy.LRU) {
            for (int i = 50; i < 150; i++) {
                assertEquals("Value" + i, largeCache.get(i));
            }
            for (int i = 0; i < 50; i++) {
                assertNull(largeCache.get(i));
            }
        } else {
            for (int i = 0; i < 50; i++) {
                assertEquals("Value" + i, largeCache.get(i));
            }
            for (int i = 99; i < 149; i++) {
                assertNull(largeCache.get(i));
            }
        }
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testRepeatedKeyUpdateAndOrder(CacheReplacementPolicy policy) {
        // Έλεγχος για επαναλαμβανόμενη ενημέρωση του ίδιου κλειδιού ενώ παράλληλα γίνονται άλλες προσθήκες
        MyCache<Integer, String> bigCache = new MyCache<>(3, policy);
        bigCache.put(1, "One");
        bigCache.put(2, "Two");

        for (int i = 0; i < 10; i++) {
            bigCache.put(1, "One-" + i);
            bigCache.put(i + 3, "Value" + i);

            if (policy == CacheReplacementPolicy.LRU) {
                assertEquals("One-" + i, bigCache.get(1));
            } else {
                if (i > 0) {
                    assertNull(bigCache.get(1));
                }
            }
        }
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testEmptyCache(CacheReplacementPolicy policy) {
        // Δοκιμή για κενή μνήμη
        MyCache<Integer, String> cache = new MyCache<>(3, policy);
        assertNull(cache.get(1), "Empty cache so any key is null");
        assertNull(cache.get(0), "Empty cache so any key is null");
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testNullHandling(CacheReplacementPolicy policy) {

        MyCache<Integer, String> cache = new MyCache<>(3, policy);
        // Έλεγχος ότι το null key ρίχνει NullPointerException
        assertThrows(NullPointerException.class, () -> cache.put(null, "Value"));
        // Έλεγχος ότι το null value ρίχνει NullPointerException
        assertThrows(NullPointerException.class, () -> cache.put(1, null));
        // Έλεγχος ότι η get με null κλειδί επιστρέφει null
        assertNull(cache.get(null));

        // Κανονική εισαγωγή
        cache.put(1, "One");
        cache.put(2, "Two");
        assertEquals("One", cache.get(1));

        //Πρέπει να παραμείνει με 2 στοιχεία
        assertEquals(2, cache.size());
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testMixedOperations(CacheReplacementPolicy policy) {
        //Έλεγχος υπό διάφορα operations
        MyCache<Integer, String> cache = new MyCache<>(3, policy);
        for (int i = 0; i < 5; i++) {
            cache.put(i, "Value" + i);
            if (i > 0) {
                cache.get(i - 1);
            }
        }

        if (policy == CacheReplacementPolicy.LRU) {
            assertNull(cache.get(0), "First item should be out");
            assertNotNull(cache.get(4), "Last item should be in cache");
            assertNotNull(cache.get(3), "Recently accessed item should be in cache");
        } else {
            assertNull(cache.get(2), "Most recently used item should be out");
            assertNotNull(cache.get(0), "Oldest item should be in cache");
            assertNotNull(cache.get(4), "Last item should be in cache");
        }
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testHeadTailPositions(CacheReplacementPolicy policy) {
        //Εξετάζουμε πως όντως παίρνουμε σωστά την κορυφή και την ουρά της μνήμης μέσω των getHead/Tail αντίστοιχα
        MyCache<Integer, String> cache = new MyCache<>(3, policy);

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
        cache.get(1);

            assertEquals("One", cache.getHead(), "Head should be recently accessed item");
            assertEquals("Two", cache.getTail(), "Tail should be least recently used");

        // Έλεγχος νέας προσθήκης και διαγραφής κόμβου
        cache.put(4, "Four");
        assertEquals("Four", cache.getHead(), "Head should be new item");
        if (policy == CacheReplacementPolicy.LRU) {
            assertEquals("Three", cache.getTail(), "Tail should be oldest remaining item");
            assertNull(cache.get(2), "LRU: Kicked item should be null");
        } else {
            assertEquals("Two", cache.getTail(), "Tail should be oldest remaining item");
            assertNull(cache.get(1), "MRU: Most recently used item should be evicted");
        }
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testRepeatedPutsOnSameKey(CacheReplacementPolicy policy) {
        //Ελέγχει την επαναλαμβανόμενη τοποθέτηση στο ίδιο κλειδί
        MyCache<Integer, String> cache = new MyCache<>(3, policy);

        // Επαναλαμβανόμενη εισαγωγή στο ίδιο κλειδί
        for (int i = 0; i < 100; i++) {
            cache.put(1, "Value" + i);
        }
        assertEquals("Value99", cache.get(1));

        // Γέμισμα της υπόλοιπης μνήμης
        cache.put(2, "Two");
        cache.put(3, "Three");


        cache.put(4, "Four");

        if (policy == CacheReplacementPolicy.LRU) {
            // Ελέγχει οτι το κλειδί 2 βγήκε από τη μνήμη γιατί είναι το λιγότερο πρόσφατα χρησιμοποιημένο
            assertNull(cache.get(1));

        } else {
            // Στη MRU το κλειδί 3 θα βγει γιατί είναι το πιο πρόσφατα χρησιμοποιημένο πριν το 4
            assertNull(cache.get(3));

        }
    }
    }