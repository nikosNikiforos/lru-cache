package org.hua.cache;

import java.util.HashMap;
import java.util.Map;

public class MyCache<K, V> extends AbstractCache implements Cache<K, V> {
    private final int capacity; // Χωρητικότητα
    private final Map<K, Node<K, V>> cache; // Κατακερματισμός O(1) αναζήτηση
    private final CustomLinkedList<K, V> linkedList; // Διπλά συνδεδεμένη λίστα
    private final CacheReplacementPolicy policy; // Στρατηγική
    private final CacheStrategy<K,V> strategy; // Υλοποίηση στρατηγικής

    // Interface για τις στρατηγικές cache
    private interface CacheStrategy<K,V> {
        Node<K,V> chooseNodeToRemove();  // Επιλογή κόμβου για αφαίρεση
        void accessNode(Node<K,V> node);  // Χειρισμός πρόσβασης κόμβου
    }

    // Υλοποίηση στρατηγικής LRU
    private class LRUStrategy implements CacheStrategy<K,V> {
        @Override
        public Node<K, V> chooseNodeToRemove() {
            return linkedList.removeLast(); // Αφαίρεση του λιγότερο πρόσφατα χρησιμοποιημένου
        }


        @Override
        public void accessNode(Node<K,V> node) {
            if (linkedList.size() > 0 && node != linkedList.getHead()) {
                linkedList.remove(node);
                linkedList.addFirst(node);
            } else if (linkedList.size() == 0) {
                linkedList.addFirst(node);
            }
        }
    }


        // Υλοποίηση στρατηγικής MRU
        private class MRUStrategy implements CacheStrategy<K, V> {
            @Override
            public Node<K, V> chooseNodeToRemove() {
                return linkedList.removeFirst(); // Αφαίρεση του πιο πρόσφατα χρησιμοποιημένου
            }

            @Override
            public void accessNode(Node<K,V> node) {
                if (linkedList.size() > 0 && node != linkedList.getHead()) {
                    linkedList.remove(node);
                    linkedList.addFirst(node);
                } else if (linkedList.size() == 0) {
                    linkedList.addFirst(node);
                }
            }
        }

            // Constructor με προεπιλεγμένη στρατηγική LRU
            public MyCache(int capacity) {
                this(capacity, CacheReplacementPolicy.LRU);
            }

            // Constructor με επιλογή στρατηγικής
            public MyCache(int capacity, CacheReplacementPolicy policy) {
                if (capacity <= 0) {
                    throw new IllegalArgumentException("Capacity must be greater than 0.");
                }
                this.capacity = capacity;
                this.cache = new HashMap<>(capacity);
                this.linkedList = new CustomLinkedList<>();
                this.policy = policy;

                // Αρχικοποίηση στρατηγικής βάσει επιλογής
                switch (policy) {
                    case LRU:
                        this.strategy = new LRUStrategy();
                        break;
                    case MRU:
                        this.strategy = new MRUStrategy();
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown policy: " + policy);
                }
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
                strategy.accessNode(node); // Χρήση στρατηγικής για ενημέρωση θέσης
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
                    strategy.accessNode(node);
                } else {
                    if (cache.size() >= capacity) {
                        // Αφαίρεση κόμβου βάσει επιλεγμένης στρατηγικής
                        Node<K, V> nodeToRemove = strategy.chooseNodeToRemove();
                        cache.remove(nodeToRemove.key);
                    }
                    // Προσθήκη νέου στοιχείου
                    Node<K, V> newNode = new Node<>(key, value);
                    cache.put(key, newNode);
                    linkedList.addFirst(newNode);  // Add to list first
                }
            }

            // Μέθοδοι πρόσβασης στις ιδιότητες της cache
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