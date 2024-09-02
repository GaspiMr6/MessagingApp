package ConcurrentTest;

public class Counter {
    private int count = 0;

    public void increment() {
        synchronized(this) { 
            count++;
        }
    }

    public int getValue() {
        return count;
    }
}