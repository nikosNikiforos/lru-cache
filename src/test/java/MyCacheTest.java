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

        // Διαφορετικές προσβάσεις για να επηρεάσουν τις συχνότητες
        if (policy == CacheReplacementPolicy.LFU) {
            // Δημιουργία διαφορετικών συχνοτήτων
            cache.get(3); // frequency: 2
            cache.get(3); // frequency: 3
            cache.get(2); // frequency: 2
            assertEquals("Two", cache.getHead());
            assertEquals("One", cache.getTail()); // Μικρότερη συχνότητα
        }
        else {
            assertEquals("Three", cache.getHead());
            assertEquals("One", cache.getTail()); // Μικρότερη συχνότητα
        }
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

        if (policy == CacheReplacementPolicy.LFU) {
            // Δημιουργία διαφορετικών συχνοτήτων
            cache.get(1); // freq 2
            cache.get(1); // freq 3
            cache.get(2); // freq 2
        }

        cache.put(4, "Four");

        assertEquals("Four", cache.getHead());
        if (policy == CacheReplacementPolicy.LRU) {
            assertEquals("Two", cache.getTail());
            assertNull(cache.get(1));
        } else if (policy == CacheReplacementPolicy.MRU) {
            assertEquals("One", cache.getTail());
            assertNull(cache.get(3));
        } else { // LFU
            assertNull(cache.get(3));
            assertNotNull(cache.get(1));
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

        // Αύξηση συχνότητας για LFU
        if (policy == CacheReplacementPolicy.LFU) {
            cache.get(1);
            cache.get(1);
            cache.get(2);
        }

        cache.put(1, "One Updated");

        assertEquals("One Updated", cache.get(1));
        assertEquals("Two", cache.get(2));

        cache.put(3, "Three");
        cache.put(4, "Four");

        if (policy == CacheReplacementPolicy.LFU) {
            // Το "One Updated" έχει υψηλότερη συχνότητα (3)
            assertNotNull(cache.get(1));
            assertNotNull(cache.get(2));
            assertNull(cache.get(3));
            assertNotNull(cache.get(4));
        } else if (policy == CacheReplacementPolicy.LRU) {
            assertNull(cache.get(1));
            assertEquals("Three", cache.get(3));
            assertEquals("Four", cache.get(4));
        } else {
            assertNull(cache.get(3));
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

        if (policy == CacheReplacementPolicy.LFU) {
            // Δημιουργία διαφορετικών συχνοτήτων χρήσης
            cache.get(1); // frequency: 2
            cache.get(1); // frequency: 3
            cache.get(2); // frequency: 2
        } else {
            assertEquals("One", cache.get(1));
            assertEquals("Two", cache.get(2));
            assertEquals("Three", cache.get(3));
        }

        cache.put(4, "Four");

        if (policy == CacheReplacementPolicy.LFU) {
            assertNotNull(cache.get(1));
            assertNotNull(cache.get(2));
            assertNull(cache.get(3));
        } else if (policy == CacheReplacementPolicy.LRU) {
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
        // Έλεγχος μοτίβου πρόσβασης και επίδρασης στη σειρά
        MyCache<Integer, String> cache = new MyCache<>(3, policy);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        if (policy == CacheReplacementPolicy.LFU) {
            // Δημιουργία διαφορετικών συχνοτήτων
            cache.get(1); // frequency: 2
            cache.get(1); // frequency: 3
            cache.get(2); // frequency: 2
        } else {
            cache.get(1);
            cache.get(2);
        }

        cache.put(4, "Four");

        if (policy == CacheReplacementPolicy.LFU) {
            assertNull(cache.get(3));
            assertNotNull(cache.get(1));
            assertNotNull(cache.get(2));
        } else if (policy == CacheReplacementPolicy.LRU) {
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

        // Εισαγωγή στοιχείων με διαφορετικές συχνότητες για LFU
        for (int i = 0; i < 150; i++) {
            largeCache.put(i, "Value" + i);
            if (policy == CacheReplacementPolicy.LFU && i < 50) {
                // Αύξηση συχνότητας για τα πρώτα 50 στοιχεία
                largeCache.get(i); // frequency: 2
                if (i < 25) {
                    largeCache.get(i); // frequency: 3 για τα πρώτα 25
                }
            }
        }

        if (policy == CacheReplacementPolicy.LFU) {
            // Έλεγχος ότι τα στοιχεία με υψηλότερη συχνότητα παραμένουν
            for (int i = 0; i < 25; i++) {
                assertNotNull(largeCache.get(i), "Στοιχεία με συχνότητα 3 πρέπει να παραμείνουν");
            }
            for (int i = 25; i < 50; i++) {
                assertNotNull(largeCache.get(i), "Στοιχεία με συχνότητα 2 πρέπει να παραμείνουν");
            }
            // Τα υπόλοιπα εξαρτώνται από τη χωρητικότητα και τη σειρά εισαγωγής
        } else if (policy == CacheReplacementPolicy.LRU) {
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

        if (policy == CacheReplacementPolicy.LFU) {
            // Αυξάνουμε τη συχνότητα του "One" στην αρχή
            bigCache.get(1); // frequency: 2
            bigCache.get(1); // frequency: 3
        }

        for (int i = 0; i < 10; i++) {
            bigCache.put(1, "One-" + i);
            bigCache.put(i + 3, "Value" + i);

            if (policy == CacheReplacementPolicy.LFU) {
                // Το "One" διατηρεί την υψηλή συχνότητα παρά τις ενημερώσεις
                assertNotNull(bigCache.get(1), "Στοιχείο με υψηλή συχνότητα πρέπει να παραμείνει");
            } else if (policy == CacheReplacementPolicy.LRU) {
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
        assertNull(cache.get(1), "Empty cache ");
        assertNull(cache.get(0), "Empty cache ");
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
            assertNull(cache.get(0), "Το πρώτο στοιχείο πρέπει να είναι εκτός");
            assertNotNull(cache.get(4), "Το τελευταίο στοιχείο πρέπει να είναι στην cache");
            assertNotNull(cache.get(3), "Το πρόσφατα προσπελασμένο στοιχείο πρέπει να είναι στην cache");
        } else {
            assertNull(cache.get(2), "Το πιο πρόσφατα  στοιχείο πρέπει να είναι εκτός");
            assertNotNull(cache.get(0), "Το παλαιότερο στοιχείο πρέπει να είναι στην cache");
            assertNotNull(cache.get(4), "Το τελευταίο στοιχείο πρέπει να είναι στην cache");
        }
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testHeadTailPositions(CacheReplacementPolicy policy) {
        //Εξετάζουμε πως όντως παίρνουμε σωστά την κορυφή και την ουρά της μνήμης μέσω των getHead/Tail αντίστοιχα
        MyCache<Integer, String> cache = new MyCache<>(3, policy);

        // Αρχικός Έλεγχος
        assertNull(cache.getHead(), "Tο head είναι κενή σε άδεια cache");
        assertNull(cache.getTail(), "To tail είναι κενή σε άδεια cache");

        // Μοναδική εισαγωγή
        cache.put(1, "One");
        assertEquals("One", cache.getHead(), "Tο head πρέπει να είναι το μοναδικό στοιχείο");
        assertEquals("One", cache.getTail(), "To tail πρέπει να είναι το μοναδικό στοιχείο");

        //Πολλαπλές εισαγωγές
        cache.put(2, "Two");
        cache.put(3, "Three");
        assertEquals("Three", cache.getHead(), "Tο head πρέπει να είναι το πιο πρόσφατο στοιχείο");
        assertEquals("One", cache.getTail(), "To tail πρέπει να είναι το παλαιότερο στοιχείο");

        // Αφού πειράξουμε τη σειρά
        cache.get(1);

        assertEquals("One", cache.getHead(), "Tο head πρέπει να είναι το πρόσφατα προσπελασμένο στοιχείο");
        assertEquals("Two", cache.getTail(), "To tail πρέπει να είναι το λιγότερο πρόσφατα χρησιμοποιημένο");

        // Έλεγχος νέας προσθήκης και διαγραφής κόμβου
        cache.put(4, "Four");
        assertEquals("Four", cache.getHead(), "Tο head πρέπει να είναι το νέο στοιχείο");
        if (policy == CacheReplacementPolicy.LFU) {
            assertNull(cache.get(2), "LFU: Το στοιχείο με τη μικρότερη συχνότητα πρέπει να εξωθηθεί");
            assertNotNull(cache.get(1), "LFU: Το στοιχείο με τη μεγαλύτερη συχνότητα πρέπει να παραμείνει");
            assertNotNull(cache.get(3), "LFU: Το στοιχείο με μεσαία συχνότητα πρέπει να παραμείνει");

        }else if (policy == CacheReplacementPolicy.LRU) {
            assertEquals("Three", cache.getTail(), "To tail πρέπει να είναι το παλαιότερο εναπομείναν στοιχείο");
            assertNull(cache.get(2), "LRU: Το εξωθημένο στοιχείο πρέπει να είναι κενό");
        } else {
            assertEquals("Two", cache.getTail(), "To tail πρέπει να είναι το παλαιότερο εναπομείναν στοιχείο");
            assertNull(cache.get(1), "MRU: Το πιο πρόσφατα χρησιμοποιημένο στοιχείο πρέπει να έχει εξωθηθεί");
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
            if (policy == CacheReplacementPolicy.LFU && i % 10 == 0) {
                // Περιοδική πρόσβαση για αύξηση συχνότητας στο LFU
                cache.get(1);
            }
        }
        assertEquals("Value99", cache.get(1));

        // Γέμισμα της υπόλοιπης μνήμης
        cache.put(2, "Two");
        cache.put(3, "Three");

        if (policy == CacheReplacementPolicy.LFU) {
            // Αύξηση συχνότητας του "Two"
            cache.get(2);
            cache.get(2);
        }

        cache.put(4, "Four");

        if (policy == CacheReplacementPolicy.LFU) {
            assertNull(cache.get(3));
            assertNotNull(cache.get(1));
            assertNotNull(cache.get(2));
        } else if (policy == CacheReplacementPolicy.LRU) {
            assertNull(cache.get(1));
        } else {
            assertNull(cache.get(3));
        }
    }

    @ParameterizedTest
    @EnumSource(CacheReplacementPolicy.class)
    void testLFUSpecificBehavior(CacheReplacementPolicy policy) {
        // Έλεγχος LFU με διαφορετικές συχνότητες χρήσης
        MyCache<Integer, String> cache = new MyCache<>(3, policy);

        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        // Δημιουργία διαφορετικών συχνοτήτων
        cache.get(1); // frequency: 2
        cache.get(1); // frequency: 3
        cache.get(2); // frequency: 2

        // Προσθήκη νέου στοιχείου
        cache.put(4, "Four");

        if (policy == CacheReplacementPolicy.LFU) {
            assertNull(cache.get(3));
            assertNotNull(cache.get(1));
            assertNotNull(cache.get(2));
            assertNotNull(cache.get(4));
        }
    }
    }