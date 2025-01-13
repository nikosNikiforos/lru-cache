package org.hua.cache;

public class CustomLinkedList <K, V>{
    private final Node<K, V> head; // Dummy αρχή
    private final Node<K, V> tail; // Dummy τέλος
    private int size;              // Μέγεθος λίστας

    public CustomLinkedList() {
        head = new Node<>(null, null);
        tail = new Node<>(null, null);
        head.next = tail;//Σύνδεση
        tail.prev = head;//λίστας
        size = 0;
    }

    // Προσθήκη κόμβου στην αρχή
    public void addFirst(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
        size++;
    }

    // Αφαίρεση κόμβου από τη λίστα
    public void remove(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
    }

    // Αφαίρεση του τελευταίου κόμβου
    public Node<K, V> removeLast() {
        if (size == 0) return null;
        Node<K, V> lastNode = tail.prev;
        remove(lastNode);
        return lastNode;
    }
    //Αφαίρεση του πρώτου κόμβου για MRU
    public Node<K, V> removeFirst() {
        if (size == 0) return null;
        Node<K, V> firstNode = head.next;
        remove(firstNode);
        return firstNode;
    }

    // Επιστροφή του πρώτου κόμβου
    public Node<K, V> getHead() {
        return size == 0 ? null : head.next;
    }

    // Επιστροφή του τελευταίου κόμβου
    public Node<K, V> getTail() {
        return size == 0 ? null : tail.prev;
    }

    // Επιστροφή μεγέθους της λίστας
    public int size() {
        return size;
    }

    // Έλεγχος αν η λίστα είναι άδεια
    public boolean isEmpty() {
        return size == 0;  // Αν το μέγεθος είναι 0, η λίστα είναι άδεια
    }
}
