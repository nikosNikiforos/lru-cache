package org.hua.cache;

public enum CacheReplacementPolicy {
    LRU("Least Recently Used"),
    MRU("Most Recently Used");

    private final String description;

    CacheReplacementPolicy(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
