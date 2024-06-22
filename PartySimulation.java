public class PartySimulation {

    public static final int NUM_GUESTS = 8;
    public static final int BOREK_TOTAL = 30;
    public static final int CAKE_TOTAL = 15;
    public static final int DRINK_TOTAL = 30;
    public static final int TRAY_CAPACITY = 5;
    
    

    public static void main(String[] args) {
        // Paylaşılan kaynaklar
        FoodTray borekTray = new FoodTray("Börek", TRAY_CAPACITY);
        FoodTray cakeTray = new FoodTray("Cake", TRAY_CAPACITY);
        FoodTray drinkTray = new FoodTray("Drink", TRAY_CAPACITY);
        

        // Guest threads
        Thread[] guests = new Thread[NUM_GUESTS];
        for (int i = 0; i < NUM_GUESTS; i++) {
            guests[i] = new Thread(new Guest(borekTray, cakeTray, drinkTray));
            guests[i].start();
        }

        // Waiter thread
        Thread waiter = new Thread(new Waiter(borekTray, cakeTray, drinkTray, BOREK_TOTAL, CAKE_TOTAL, DRINK_TOTAL));
        waiter.start();

        // Wait for all threads to finish
        for (Thread guest : guests) {
            try {
                guest.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            waiter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Party is over!");
    }
}

class FoodTray {
    private String name;
    private int capacity;
    private int current;

    public FoodTray(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.current = capacity;
    }

    public synchronized void takeItem() throws InterruptedException {
        while (current == 0) {
            wait();
        }
        current--;
        System.out.println(Thread.currentThread().getName() + " takes 1 " + name);
    }

    public synchronized void refill() {
        current = capacity;
        notifyAll();
        System.out.println("Waiter refilled " + name + " tray");
    }

    public boolean isEmpty() {
        return current == 0;
    }
}

class Guest implements Runnable {
    private FoodTray borekTray;
    private FoodTray cakeTray;
    private FoodTray drinkTray;
    private int borekEaten;
    private int cakeEaten;
    private int drinkConsumed;

    public Guest(FoodTray borekTray, FoodTray cakeTray, FoodTray drinkTray) {
        this.borekTray = borekTray;
        this.cakeTray = cakeTray;
        this.drinkTray = drinkTray;
        this.borekEaten = 0;
        this.cakeEaten = 0;
        this.drinkConsumed = 0;
    }

    @Override
    public void run() {
    int MAX_BOREK_PER_GUEST = 4;

    int MAX_CAKE_PER_GUEST = 2;
    int MAX_DRINK_PER_GUEST = 4;
        while (true) {
            try {
                if (borekEaten < MAX_BOREK_PER_GUEST) {
                    borekTray.takeItem();
                    borekEaten++;
                } else if (cakeEaten < MAX_CAKE_PER_GUEST) {
                    cakeTray.takeItem();
                    cakeEaten++;
                } else if (drinkConsumed < MAX_DRINK_PER_GUEST) {
                    drinkTray.takeItem();
                    drinkConsumed++;
                } else {
                    System.out.println(Thread.currentThread().getName() + " is full!");
                    break;
                }
                Thread.sleep(1000); // Simulate eating/drinking time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Waiter implements Runnable {
    private FoodTray borekTray;
    private FoodTray cakeTray;
    private FoodTray drinkTray;
    private int borekRemaining;
    private int cakeRemaining;
    private int drinkRemaining;

    public Waiter(FoodTray borekTray, FoodTray cakeTray, FoodTray drinkTray, int borekRemaining, int cakeRemaining, int drinkRemaining) {
        this.borekTray = borekTray;
        this.cakeTray = cakeTray;
        this.drinkTray = drinkTray;
        this.borekRemaining = borekRemaining;
        this.cakeRemaining = cakeRemaining;
        this.drinkRemaining = drinkRemaining;
    }

    @Override
    public void run() {
      int TRAY_CAPACITY = 5;
        while (true) {
            try {
                if (borekRemaining > 0 && borekTray.isEmpty()) {
                    borekRemaining -= TRAY_CAPACITY;
                    borekTray.refill();
                } else if (cakeRemaining > 0 && cakeTray.isEmpty()) {
                    cakeRemaining -= TRAY_CAPACITY;
                    cakeTray.refill();
                } else if (drinkRemaining > 0 && drinkTray.isEmpty()) {
                    drinkRemaining -= TRAY_CAPACITY;
                    drinkTray.refill();
                } else if (borekRemaining == 0 && cakeRemaining == 0 && drinkRemaining == 0) {
                    System.out.println("Waiter is done!");
                    break;
                }
                Thread.sleep(1000); // Simulate waiting time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
