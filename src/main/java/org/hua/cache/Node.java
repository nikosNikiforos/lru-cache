package org.hua.cache;

public class Node<K, V> {
    K key;           // Το κλειδί του κόμβου
    V value;         // Η τιμή του κόμβου
    Node<K, V> prev; // Δείκτης στον προηγούμενο κόμβο
    Node<K, V> next;// Δείκτης στον επόμενο κόμβο
    int frequency; // Μετρητής LFU

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
        this.frequency = 1;
    }
}

