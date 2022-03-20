package ca.carleton.sysc4907fx;

import javafx.scene.image.Image;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
class CashTest {
    private Car car;
    private Cash cash;
    private Location location;

    @BeforeEach
    void setUp() throws IOException {
        location = new Location(45.324428, -75.718196);
        car = new Car(location, 160);
        cash = new Cash(car);
    }


    @AfterEach
    void teardown() {
        cash.exit();
    }

    @Test
    void has() {
        Image image = mock(Image.class);
        cash.add(location,160,image);
        assertTrue(cash.has(location));
    }

    @Test
    void add() {
        Image image = mock(Image.class);
        cash.add(location,160,image);
        cash.add(location,161,image);
        assertEquals(1,cash.numOfLocations());
        assertEquals(2,cash.numOfImages());
    }

    @Test
    void peek() throws InterruptedException {
        Image image = mock(Image.class);
        cash.add(location,160,image);
        Location location2 = new Location(location.lat() + 9./2500000, location.lng());
        Image image2 = mock(Image.class);
        cash.add(location2,160,image2);
        Location location3 = new Location(location.lat() - 9./2500000, location.lng());
        Image image3 = mock(Image.class);
        cash.add(location3,160,image3);
        assertEquals(3,cash.numOfImages());
        assertEquals(Optional.of(image),cash.peek());
        assertEquals(3,cash.numOfImages());
        car.cruiseControl();
        Thread.sleep(100);
        car.stop();
        assertEquals(Optional.of(image3),cash.peek());
        assertEquals(2,cash.numOfImages());
    }
    @Test
    void peek_empty() {
        assertEquals(Optional.empty(),cash.peek());
    }

    @Test
    void clear() {
        Image image = mock(Image.class);
        cash.add(location,160,image);
        cash.add(location,161,image);
        Location location2 = new Location(location.lat() + 2, location.lng());
        cash.add(location2,160,image);
        assertEquals(3,cash.numOfImages());
        cash.clear();
        assertEquals(0,cash.numOfImages());
    }
}