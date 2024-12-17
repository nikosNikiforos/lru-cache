import org.hua.cache.LRUCache;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




class LRUCacheTest {
    @Test
    void testHeadTailOrder() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        // Test head/tail ordering with multiple items
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        assertEquals("Three", cache.getHead());
        assertEquals("One", cache.getTail());
    }

    @Test
    void testAccessUpdatesOrder() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        // Test that accessing items updates head/tail correctly
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        cache.get(1);  // Access oldest item

        assertEquals("One", cache.getHead());
        assertEquals("Two", cache.getTail());
    }

    @Test
    void testEvictionOrder() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        // Test that eviction removes from tail
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");
        cache.put(4, "Four");  // Should evict "One"

        assertEquals("Four", cache.getHead());
        assertEquals("Two", cache.getTail());
        assertNull(cache.get(1));  // Verify "One" was evicted
    }

    @Test
    void testUpdateExisting() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        // Test updating existing item moves it to head
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(1, "One Updated");

        assertEquals("One Updated", cache.getHead());
        assertEquals("Two", cache.getTail());
    }

    @Test
    void testFrequentAccess() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        // Test frequent access to same item keeps it at head
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
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        // Test cache doesn't exceed capacity
        for (int i = 0; i < 10; i++) {
            cache.put(i, "Value" + i);
        }

        assertEquals(3, cache.size());  // Capacity should be maintained
        assertEquals("Value9", cache.getHead());
    }

}
