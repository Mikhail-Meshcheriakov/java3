package lesson5;

import java.util.concurrent.BrokenBarrierException;

public class Car implements Runnable {
    private static int CARS_COUNT;
    private static int CARS_PLACE;

    private Race race;
    private int speed;
    private String name;
    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }
    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep((long) (500 + Math.random() * 800));
            MainClass.cb.await();
            System.out.println(this.name + " готов");
            MainClass.cdl1.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        MainClass.lock.lock();
        MainClass.lock.unlock();

        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        try {
            MainClass.semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finished();
        MainClass.semaphore.release();
        MainClass.cdl2.countDown();
    }

    private void finished() {
        CARS_PLACE++;
        if (CARS_PLACE == 1) {
            System.out.println(getName() + " - WIN");
        } else {
            System.out.println(getName() + " занял " + CARS_PLACE + " место");
        }
    }
}
