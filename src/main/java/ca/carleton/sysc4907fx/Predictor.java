package ca.carleton.sysc4907fx;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

public class Predictor implements Runnable {
    private static final Set<Integer> OFFSETS = Set.of(1, 2, 3, 5, 8);
    private static final int UPDATE_FREQUENCY = 3;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final CloseableHttpClient CLIENT = HttpClients.createDefault();
    private final Car car;
    private final TransferQueue<DownloadRequest> requests;
    private final boolean testingMode;
    private final String apiKey;


    public Predictor(Car car, String apiKey, TransferQueue<DownloadRequest> requests, boolean testingMode) {
        this.apiKey = apiKey;
        this.car = car;
        this.requests = requests;
        this.testingMode = testingMode;
    }

    @Override
    public void run() {
        if (!testingMode) {
            EXECUTOR.scheduleAtFixedRate(() -> OFFSETS.parallelStream().forEach((offset) -> {
                HttpGet httpget = new HttpGet("https://maps.googleapis.com/maps/api/streetview/metadata?size=" + Downloader.SIZE + "&location=" + car.getLatLongOffset(offset) + "&key=" + apiKey);
                try (CloseableHttpResponse response = CLIENT.execute(httpget)) {
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        Metadata metadata = MAPPER.readValue(response.getEntity().getContent(), Metadata.class);
                        if (metadata.status().equals("OK")) {
                            requests.tryTransfer(new DownloadRequest(metadata.location(), car.getAngle()));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }), 0, UPDATE_FREQUENCY, TIME_UNIT);
        }
    }

    public void stop() {
        EXECUTOR.shutdown();
    }
}
