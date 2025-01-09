package org.hua.cache;

import java.util.HashMap;
import java.util.Map;

public class MyCache<K, V> extends AbstractCache implements Cache<K, V>    {
    private final int capacity; // Χωρητικότητα
    private final Map<K, Node<K, V>> cache; // Κατακερματισμός O(1) αναζήτηση
    private final CustomLinkedList<K, V> linkedList; // Διπλά συνδεδεμένη λίστα
    private final CacheReplacementPolicy policy; // Στρατηγική

    // Constructor με επιλογή στρατηγικής
    public MyCache(int capacity, CacheReplacementPolicy policy) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);
        this.linkedList = new CustomLinkedList<>();
        this.policy = policy;

    }

    @Override
    public V get(K key) {
        if (key == null) {
            return null; // Αν το κλειδί είναι null επιστρέφουμε null
        }
        if (!cache.containsKey(key)) {
            missCount++;
            return null; // Δεν υπάρχει στη μνήμη
        }
        hitCount++;
        Node<K, V> node = cache.get(key);
        linkedList.remove(node);
        linkedList.addFirst(node); // Για LRU Μεταφορά στην αρχή
        return node.value;
    }

    @Override
    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("Key and value cannot be null.");
        }

        if (cache.containsKey(key)) {
            // Αν το κλειδί υπάρχει ενημερώνουμε την τιμή και τη θέση
            Node<K, V> node = cache.get(key);
            node.value = value;
            linkedList.remove(node);
            linkedList.addFirst(node); // Μεταφορά στην αρχή για LRU
        } else {
            if (cache.size() >= capacity) {
                Node<K, V> nodeToRemove;
                // Επιλογή στρατηγικής αντικατάστασης
                if (policy == CacheReplacementPolicy.LRU) {
                    nodeToRemove = linkedList.removeLast(); // Αφαίρεση LRU
                } else { // MRU
                    nodeToRemove = linkedList.removeFirst(); // Αφαίρεση MRU
                }
                cache.remove(nodeToRemove.key);
            }
            // Προσθήκη νέου στοιχείου
            Node<K, V> newNode = new Node<>(key, value);
            linkedList.addFirst(newNode);
            cache.put(key, newNode);
        }
    }




    public int getCapacity() {
        return capacity;
    }

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

    public CacheReplacementPolicy getPolicy() {
        return policy;
    }
}
