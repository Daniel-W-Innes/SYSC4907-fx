package ca.carleton.sysc4907fx;

import javafx.scene.image.Image;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Cash {
    private final Map<Location, Double> distances;
    private final Map<Location, Map<Integer, Image>> images;
    private final Car car;
    private final FileWriter writerIn, writerOut;

    public Cash(Car car) throws IOException {
        this.car = car;
        images = new ConcurrentHashMap<>();
        distances = new ConcurrentHashMap<>();
        writerIn = new FileWriter("cash_add_log.csv");
        writerOut = new FileWriter("cash_peek_log.csv");
    }

    public synchronized boolean has(Location location) {
        return distances.containsKey(location);
    }

    public synchronized void add(Location location, int angle, Image image) {
        try {
            writerIn.write(location + "\n");
        } catch (IOException ignored) {}
        distances.put(location, location.distance(car.getLatLong()));
        if (images.containsKey(location)) {
            images.get(location).put(angle, image);
        } else {
            images.put(location, new ConcurrentHashMap<>(Map.of(angle, image)));
        }
    }

    public synchronized Optional<Image> peek() {
        Location curLoc = car.getLatLong();
        Image next = null;
        Location nextLocation = null;
        double minDistance = Double.MAX_VALUE;
        for (Map.Entry<Location, Double> entry : distances.entrySet()) {
            double distance = entry.getKey().distance(curLoc);
            if (distance < minDistance) {
                minDistance = distance;
                nextLocation = entry.getKey();
                next = images.get(nextLocation).get(car.getAngle());
            }
            if (distance > entry.getValue() && entry.getKey() != nextLocation) {
                distances.remove(entry.getKey());
                images.remove(entry.getKey());
            }
            entry.setValue(distance);
        }
        try {
            writerOut.write(nextLocation + "\n");
        } catch (IOException ignored) {}
        return Optional.ofNullable(next);
    }

    public void clear() {
        distances.clear();
        images.clear();
    }

    public void exit() {
        try {
            writerIn.flush();
            writerOut.flush();
            writerIn.close();
            writerOut.close();
        } catch (IOException ignored) {}
    }

    public int size() {
        return images.size();
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }
}
