package ca.carleton.sysc4907fx;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Car {
    public static final int maxJ = 1;
    public static final int maxA = 12;
    public static final int timeDilation = 2;
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final WGS84 wgs84;
    private double v, a;
    private int j, angle;
    private long lastUpdated;

    private final FileWriter writer;

    public Car(Location location) throws IOException {
        this(location, 0);
    }

    public Car(Location location, int angle) throws IOException {
        lastUpdated = System.nanoTime();
        wgs84 = location.WGS84();
        this.angle = angle;
        writer = new FileWriter("location_log.csv");
        executorService.scheduleAtFixedRate(this::update, 0, 1, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(()-> {
            try {
                writer.write(getLatLong()  "\n");
            } catch (IOException ignored) {}
        }, 0, 1, TimeUnit.SECONDS);
    }

    public synchronized void forward() {
        a += maxJ;
        if (a > maxA) {
            a = maxA;
        }
    }

    public synchronized void backward() {
        a -= maxJ;
        if (a < -maxA) {
            a = -maxA;
        }
    }

    public synchronized void left() {
        if (angle == 0) {
            angle = 359;
        } else {
            angle -= 1;
        }
    }

    public synchronized void right() {
        if (angle == 359) {
            angle = 0;
        } else {
            angle += 1;
        }
    }

    public synchronized void cruiseControl() {
        a = 0;
        v = 10;
    }

    public synchronized void stop() {
        a = 0;
        v = 0;
    }

    public int getAngle() {
        return angle;
    }

    public void exit() {
        try {
            writer.flush();
            writer.close();
        } catch (IOException ignored) {}
        executorService.shutdown();
    }

    private void update() {
        long next = System.nanoTime();
        double dt = ((next - lastUpdated) * Math.pow(10, -9)) / timeDilation;
        j = a > 0 ? -maxJ : maxJ;
        double d = PhysicsEngine.deltaD(dt, v, a, j);
        wgs84.offset(PhysicsEngine.xComponent(d, angle), PhysicsEngine.yComponent(d, angle));
        v += PhysicsEngine.deltaV(dt, a, j);
        a += PhysicsEngine.deltaA(dt, a, j);
        lastUpdated = next;
    }

    public Location getLatLongOffset(double dt) {
        double d = PhysicsEngine.deltaD(dt, v, a, j);
        return wgs84.getLatLongOffset(PhysicsEngine.xComponent(d, angle),PhysicsEngine.yComponent(d, angle));
    }

    public Location getLatLong() {
        return wgs84.getLatLong();
    }

    @Override
    public String toString() {
        return "Car{" + wgs84.toString() + ", Velocity=" + v + ", Acceleration=" + a + ", angle=" + angle + '}';
    }
}
