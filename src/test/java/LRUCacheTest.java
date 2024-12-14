
import org.hua.cache.Cache;
import org.hua.cache.LRUCache;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {

    @Test
    void testPutAndGet() {
        Cache<Integer, String> cache = new LRUCache<>(2);

        // Προσθήκη τιμών
        cache.put(1, "A");
        cache.put(2, "B");

        // Έλεγχος τιμών
        assertEquals("A", cache.get(1)); // Πρόσφατα χρησιμοποιημένο: 1
        assertEquals("B", cache.get(2)); // Πρόσφατα χρησιμοποιημένο: 2
    }

    @Test
    void testEvictionPolicy() {
        Cache<Integer, String> cache = new LRUCache<>(2);

        // Προσθήκη τιμών
        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(3, "C"); // 1 πρέπει να αφαιρεθεί

        assertNull(cache.get(1));
        assertEquals("B", cache.get(2));
        assertEquals("C", cache.get(3));
    }

    @Test
    void testOverwriteValue() {
        Cache<Integer, String> cache = new LRUCache<>(2);

        // Προσθήκη τιμών
        cache.put(1, "A");
        cache.put(1, "B"); // Αντικατάσταση της τιμής για το κλειδί 1

        assertEquals("B", cache.get(1)); // Έλεγχος αντικατάστασης τιμής
    }

    @Test
    void testEdgeCases() {
        Cache<Integer, String> cache = new LRUCache<>(1);

        // Μία μόνο θέση στη μνήμη
        cache.put(1, "A");
        assertEquals("A", cache.get(1));

        cache.put(2, "B"); // 1 πρέπει να αφαιρεθεί
        assertNull(cache.get(1));
        assertEquals("B", cache.get(2));
    }
}
