package org.hua.cache;

public interface Counter {
    int getHitCount();      // Τα hits
    int getMissCount();     // Τα misses
    double getHitRate();    // Ποσοστό επιτυχιών
    double getMissRate();   // Ποσοστό αποτυχιών
}
