package lesson4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private final Object monitor = new Object();
    private static volatile char currentLetter = 'A';

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(3);
        Main main = new Main();

        service.execute(() -> {
            for (int i = 0; i < 5; i++) {
                main.showA();
            }
        });
        service.execute(() -> {
            for (int i = 0; i < 5; i++) {
                main.showB();
            }
        });

        service.execute(() -> {
            for (int i = 0; i < 5; i++) {
                main.showC();
            }
        });

        service.shutdown();
    }

    private void showA() {
        synchronized (monitor) {
            try {
                while (currentLetter != 'A') {
                    monitor.wait();
                }
                System.out.println("A");
                currentLetter = 'B';
                Thread.sleep(1000);
                monitor.notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void showB() {
        synchronized (monitor) {
            try {
                while (currentLetter != 'B') {
                    monitor.wait();
                }
                System.out.println("B");
                currentLetter = 'C';
                Thread.sleep(1000);
                monitor.notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void showC() {
        synchronized (monitor) {
            try {
                while (currentLetter != 'C') {
                    monitor.wait();
                }
                System.out.println("C\n");
                currentLetter = 'A';
                Thread.sleep(1000);
                monitor.notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
