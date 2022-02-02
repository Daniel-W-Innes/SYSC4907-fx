package ca.carleton.sysc4907fx;

import javafx.scene.image.Image;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Cash{
    private final Map<Location, Double> distances;
    private final Map<Location, Map<Integer, Image>> images;
    private final Car car;

    public Cash(Car car) {
        this.car = car;
        images = new ConcurrentHashMap<>();
        distances = new ConcurrentHashMap<>();
    }

    public boolean has(Location location){
        return distances.containsKey(location);
    }

    public void add(Location location, int angle, Image image) {
        distances.put(location, location.distance(car.getLatLong()));
        if (images.containsKey(location)){
            images.get(location).put(angle, image);
        }else {
            images.put(location, new ConcurrentHashMap<>(Map.of(angle, image)));
        }
    }

    public Optional<Image> peek(int angle) {
        Location curLoc = car.getLatLong();
        Image next = null;
        double minDistance = Double.MAX_VALUE;
        for (Map.Entry<Location, Double>  entry: distances.entrySet() ) {
            double distance = entry.getKey().distance(curLoc);
            if (distance < minDistance){
                minDistance = distance;
                next = images.get(entry.getKey()).get(angle);
            }
            if (distance > entry.getValue()){
                distances.remove(entry.getKey());
                images.remove(entry.getKey());
            }
            entry.setValue(distance);
        }
        return Optional.ofNullable(next);
    }

    public void clear() {
        distances.clear();
        images.clear();
    }

    public int size() {
        return images.size();
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }
}
