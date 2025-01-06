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
    public Node<K, V> removeFirst() {
        if (size == 0) return null;
        Node<K, V> firstNode = head.next;  // Get first real node (after dummy head)
        remove(firstNode);                 // Use existing remove method
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
}
