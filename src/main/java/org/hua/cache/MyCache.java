package org.hua.cache;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

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
                } else if (linkedList.isEmpty()) {
                    linkedList.addFirst(node);
                }
            }
        }


    // Υλοποίηση στρατηγικής LFU
    private class LFUStrategy implements CacheStrategy<K,V> {
        // TreeMap για την παρακολούθηση συχνοτήτων χρήσης
        // Key: συχνότητα, Value: λίστα κόμβων με αυτή τη συχνότητα
        private final TreeMap<Integer, LinkedList<Node<K,V>>> frequencyMap = new TreeMap<>();

        @Override
        public Node<K,V> chooseNodeToRemove() {
            // Έλεγχος αν το frequencyMap είναι άδειο
            if (frequencyMap.isEmpty()) {
                // Αν είναι άδειο, παίρνουμε τον πρώτο κόμβο από τη λίστα
                Node<K,V> firstNode = linkedList.getHead();
                linkedList.remove(firstNode);
                return firstNode;
            }

            // Παίρνουμε τη μικρότερη συχνότητα (πρώτο κλειδί του TreeMap)
            int minFrequency = frequencyMap.firstKey();

            // Παίρνουμε τη λίστα κόμβων με τη μικρότερη συχνότητα
            LinkedList<Node<K,V>> leastFrequentNodes = frequencyMap.get(minFrequency);

            // Αφαιρούμε τον πρώτο κόμβο από τη λίστα (LRU )
            Node<K,V> nodeToRemove = leastFrequentNodes.removeFirst();

            // Αν η λίστα άδειασε, αφαιρούμε την καταχώρηση από το frequencyMap
            if (leastFrequentNodes.isEmpty()) {
                frequencyMap.remove(minFrequency);
            }

            linkedList.remove(nodeToRemove);  // Αφαίρεση από τη λίστα
            return nodeToRemove;
        }

        @Override
        public void accessNode(Node<K,V> node) {
            // Εξασφάλιση ότι ο κόμβος έχει αρχικοποιημένη συχνότητα
            if (node.frequency == 0) {
                node.frequency = 1;
            }

            // Αφαίρεση του κόμβου από την τρέχουσα συχνότητα
            removeFromFrequencyMap(node);

            // Αύξηση της συχνότητας του κόμβου
            node.frequency++;

            // Προσθήκη του κόμβου στη νέα συχνότητα
            addToFrequencyMap(node);

            // Ενημέρωση θέσης στη λίστα
            linkedList.remove(node);
            linkedList.addFirst(node);
        }

        // Βοηθητική μέθοδος για την αφαίρεση κόμβου από το frequencyMap
        private void removeFromFrequencyMap(Node<K,V> node) {
            LinkedList<Node<K,V>> nodes = frequencyMap.get(node.frequency);
            if (nodes != null) {
                nodes.remove(node);
                if (nodes.isEmpty()) {
                    frequencyMap.remove(node.frequency);
                }
            }
        }

        // Βοηθητική μέθοδος για την προσθήκη κόμβου στο frequencyMap
        private void addToFrequencyMap(Node<K,V> node) {
            // Εξασφάλιση ότι νέοι κόμβοι ξεκινούν με συχνότητα 1
            if (node.frequency == 0) {
                node.frequency = 1;
            }

            // Παίρνουμε τη λίστα κόμβων για τη συγκεκριμένη συχνότητα
            LinkedList<Node<K,V>> nodesWithSameFrequency = frequencyMap.get(node.frequency);

            // Αν δεν υπάρχει λίστα για αυτή τη συχνότητα, δημιουργούμε μία καινούρια
            if (nodesWithSameFrequency == null) {
                nodesWithSameFrequency = new LinkedList<>();
                frequencyMap.put(node.frequency, nodesWithSameFrequency);
            }

            // Προσθέτουμε τον κόμβο στη λίστα
            nodesWithSameFrequency.add(node);
        }

        // Βοηθητική μέθοδος για την αρχικοποίηση νέου κόμβου
        public void initializeNewNode(Node<K,V> node) {
            node.frequency = 1;  // Αρχικοποίηση συχνότητας
            addToFrequencyMap(node);  // Προσθήκη στο frequency map
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
                switch(policy) {
                    case LRU:
                        this.strategy = new LRUStrategy();
                        break;
                    case MRU:
                        this.strategy = new MRUStrategy();
                        break;
                    case LFU:
                        this.strategy = new LFUStrategy();
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

                    if (strategy instanceof LFUStrategy) {
                        ((LFUStrategy) strategy).initializeNewNode(newNode);
                    }
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