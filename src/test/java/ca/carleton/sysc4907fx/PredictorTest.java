package ca.carleton.sysc4907fx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import static org.junit.jupiter.api.Assertions.*;

class PredictorTest {
    private String apiKey;;
    private TransferQueue<DownloadRequest> requests;
    private static final Map<Location,Location> LOCATIONS = Map.of(new Location(45.389614, -75.693626), new Location(45.38955367223915,-75.69367997043345));

    @BeforeEach
    void setUp() {
        apiKey = System.getenv("API_KEY");
        assertNotNull(apiKey);
        requests = new LinkedTransferQueue<>();
    }

    @Test
    void run() {
        LOCATIONS.forEach(
                (location, location2) -> {
                    Predictor predictor = null;
                    try {
                        predictor = new Predictor(new Car(location.lat(),location.lng()),apiKey,requests);
                    } catch (IOException e) {
                        fail();
                    }
                    predictor.run();
                    try {
                        DownloadRequest  request = requests.take();
                        assertEquals( new DownloadRequest(location2,0),request);
                    } catch (InterruptedException e) {
                        fail();
                    }
                    predictor.stop();
                }
        );
    }
}