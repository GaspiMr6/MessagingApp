package ConcurrentTest;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                counter.increment(); 
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                counter.increment(); 
            }
        });

        thread1.start();
        thread2.start();

        thread1.join(); // Wait for the Thread to finish
        thread2.join(); // Wait for the Thread to finish

        System.out.println("Valor final del counter: " + counter.getValue());
    }
}