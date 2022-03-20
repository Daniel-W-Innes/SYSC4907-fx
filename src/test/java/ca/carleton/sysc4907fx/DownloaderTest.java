package ca.carleton.sysc4907fx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import static org.junit.jupiter.api.Assertions.*;

class DownloaderTest {
    private static final Location LOCATION = new Location(45.389614, -75.693626);
    private String apiKey;
    private TransferQueue<DownloadRequest> requests;
    private Car car;

    @BeforeEach
    void setUp() {
        apiKey = System.getenv("API_KEY");
        assertNotNull(apiKey);
        requests = new LinkedTransferQueue<>();
        try {
            car = new Car(LOCATION);
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void run() {
        Cash cash = null;
        try {
            cash = new Cash(car);
        } catch (IOException e) {
            fail();
        }
        Downloader downloader = new Downloader(apiKey, requests,cash,4, false);
        requests.add(new DownloadRequest(LOCATION,0));
        new Thread(downloader).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(1,cash.numOfImages());
    }
}