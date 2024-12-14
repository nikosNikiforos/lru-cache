
package org.hua.cache;


class Node<K, V> {
    K key;        // Κλειδί του στοιχείου
    V value;      // Τιμή του στοιχείου
    Node<K, V> prev; // Δείκτης στον προηγούμενο κόμβο
    Node<K, V> next; // Δείκτης στον επόμενο κόμβο

    Node(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
