package org.hua.cache;
import java.util.HashMap;

// Υλοποίηση της LRU Cache
public class LRUCache<K,V> implements Cache<K,V> {
    private final int capacity;
    private final HashMap<K, Node<K,V>> cache;
    private final Node<K,V> head;  // Αρχική Κορυφή
    private final Node<K,V> tail;  //Αρχική ουρά
        //Constructors
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);

        // Αρχικοποίηση ουράς και κορυφής
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);

        // Ένωση ουράς-κορυφής
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            return null;
        }

        Node<K,V> node = cache.get(key);
        if (node == null) {
            return null;
        }

        // Μετακίνηση node στην κορυφή εφόσον κλήθηκε
        moveToFront(node);
        return node.value;
    }

    @Override
    public void put(K key, V value) {
        //Έλεγχος για έγκυρο κλειδί
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        Node<K,V> existingNode = cache.get(key);
        //Έλγχος για ύπαρξη κόμβου στη cache
        if (existingNode != null) {
            // Ενημέρωση node
            existingNode.value = value;
            moveToFront(existingNode);
        } else {
            // Δημιουργία κόμβου
            Node<K,V> newNode = new Node<>(key, value);

            //Αν η cache είνα γεμάτη και διώχνει αυτό που χρησιμοποιήθηκε πιο παλιά

            if (cache.size() >= capacity) {
                removeLRU();
            }

            cache.put(key, newNode);
            addToFront(newNode);
        }
    }

    //Μετακίνηση του κόμβου στην πρώτη θέση
    private void moveToFront(Node<K,V> node) {
        removeNode(node);
        addToFront(node);
    }

    //Προσθέτει έναν κόμβο στην αρχή της cache
    private void addToFront(Node<K,V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    //Αφαιρεί έναν κόμβο από τη λίστα
    private void removeNode(Node<K,V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    // Αφαιρεί το κόμβο που χρησιμοποιήθηκε λιγότερο πρόσφατα α.
    private void removeLRU() {
        Node<K,V> lru = tail.prev;
        removeNode(lru);
        cache.remove(lru.key);
    }

    //Getters για head και tail της cache.
    public V getHead() {
        return head.next == tail ? null : head.next.value;
    }

    public V getTail() {
        return tail.prev == head ? null : tail.prev.value;
    }


    public int size() {
        return cache.size();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }
}