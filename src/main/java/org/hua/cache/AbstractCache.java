package org.hua.cache;

public abstract class AbstractCache implements Counter {
    protected int missCount;
    protected int hitCount;
    public AbstractCache() {
            this.hitCount = 0;
            this.missCount = 0;
        }


    public int getHitCount() {
        return hitCount;
    }

    public int getMissCount() {
        return missCount;
    }
    public double getHitRate() {
        int total = hitCount + missCount;
        return total == 0 ? 0.0 : (double) hitCount / total * 100;
    }

    public double getMissRate() {
        int total = hitCount + missCount;
        return total == 0 ? 0.0 : (double) missCount / total * 100;
    }
}
