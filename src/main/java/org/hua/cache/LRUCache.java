package org.hua.cache;
import java.util.HashMap;
import java.util.Map;


// Υλοποίηση της LRU
public class LRUCache<K,V> implements Cache<K,V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final CustomLinkedList<K, V> linkedList;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);
        this.linkedList = new CustomLinkedList<>();
    }

    @Override
    public V get(K key) {
        if (key == null) {
            return null; // Αν το κλειδί είναι null επιστρέφουμε null.
        }
        if (!cache.containsKey(key)) {
            return null;
        }
        Node<K, V> node = cache.get(key);
        linkedList.remove(node);
        linkedList.addFirst(node);
        return node.value;
    }


    @Override
    public void put(K key, V value) {
        //Έλεγχοι για null, χρησιμοποιούνται και στα tests
        if (key == null) {
            throw new NullPointerException("No Null keys pls ");
        }
        if (value == null) {
            throw new NullPointerException("No null values pls");
        }

        if (cache.containsKey(key)) {
            // Αν το κλειδί υπάρχει, ενημερώνουμε την τιμή και τη σειρά
            Node<K, V> node = cache.get(key);
            node.value = value;
            linkedList.remove(node);
            linkedList.addFirst(node);
        } else {
            // Αν η cache είναι γεμάτη, αφαιρούμε το LRU
            if (cache.size() >= capacity) {
                Node<K, V> lruNode = linkedList.removeLast();
                cache.remove(lruNode.key);
            }
            // Προσθέτουμε νέο στοιχείο
            Node<K, V> newNode = new Node<>(key, value);
            linkedList.addFirst(newNode);
            cache.put(key, newNode);
        }
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