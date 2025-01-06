package org.hua.cache;
import java.util.HashMap;
import java.util.Map;


// Υλοποίηση της LRU
public class LRUCache<K,V> implements Cache<K,V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final CustomLinkedList<K, V> linkedList;
    private final CacheReplacementPolicy policy;
    private int hitCount;
    private int missCount;

    public LRUCache(int capacity) {
        this(capacity, CacheReplacementPolicy.LRU);
    }

    public LRUCache(int capacity, CacheReplacementPolicy policy) {
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);
        this.linkedList = new CustomLinkedList<>();
        this.hitCount = 0;
        this.missCount = 0;
        this.policy = policy;
    }
    @Override
    public V get(K key) {
        if (!cache.containsKey(key)) {
            return null;
        }
        hitCount++;
        Node<K, V> node = cache.get(key);
        linkedList.remove(node);
        linkedList.addFirst(node);
        return node.value;
    }

    @Override
    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("Null keys or values not allowed");
        }

        if (cache.containsKey(key)) {
            hitCount++;
            Node<K, V> node = cache.get(key);
            node.value = value;
            linkedList.remove(node);
            linkedList.addFirst(node);
        } else {
            missCount++;
            if (cache.size() >= capacity) {
                // Here's where the policy makes a difference
                Node<K, V> nodeToRemove = (policy == CacheReplacementPolicy.LRU) ?
                        linkedList.removeLast() :    // LRU removes least recently used (from tail)
                        linkedList.removeFirst();    // MRU removes most recently used (from head)
                cache.remove(nodeToRemove.key);
            }
            Node<K, V> newNode = new Node<>(key, value);
            linkedList.addFirst(newNode);
            cache.put(key, newNode);
        }
    }
    public int getHitCount() {
        return hitCount;
    }
    public int getCapacity() {
        return capacity;
    }

    public int getMissCount() {
        return missCount;
    }
    public double getHitRate() {
        long total = hitCount + missCount;
        return total == 0 ? 0.0 : (double) hitCount / total * 100;
    }


    public double getMissRate() {
        long total = hitCount + missCount;
        return total == 0 ? 0.0 : (double) missCount / total * 100;
    }
    //Υλοποίηση μεθόδων ΚΑΙ στη Cache για να ζητάμε με cache.getHead/Tail και cache.size
    public int size() {
        return linkedList.size();
    }

    public V getHead() {
        Node<K, V> headNode = linkedList.getHead();
        return headNode != null ? headNode.value : null;
    }

    public V getTail() {
        Node<K, V> tailNode = linkedList.getTail();
        return tailNode != null ? tailNode.value : null;
    }

}