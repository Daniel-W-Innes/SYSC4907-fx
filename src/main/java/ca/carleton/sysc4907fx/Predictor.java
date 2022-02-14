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
    private static final Set<Integer> OFFSETS = Set.of(1,2,3,5,8);
    private static final int UPDATE_FREQUENCY = 3;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private final Car car;
    private final TransferQueue<DownloadRequest> requests;
    private final String apiKey;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final CloseableHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();


    public Predictor(Car car ,String apiKey,TransferQueue<DownloadRequest> requests) {
        this.apiKey = apiKey;
        this.car = car;
        this.requests = requests;
        client = HttpClients.createDefault();
    }

    @Override
    public void run() {
        executor.scheduleAtFixedRate(()-> OFFSETS.parallelStream().forEach((offset)->{
            HttpGet httpget = new HttpGet("https://maps.googleapis.com/maps/api/streetview/metadata?size="+Downloader.size+"&location="+car.getLatLongOffset(offset)+"&amp;key="+apiKey);
            try (CloseableHttpResponse response = client.execute(httpget)){
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    Metadata metadata = mapper.readValue(response.getEntity().getContent(), Metadata.class);
                    requests.tryTransfer(new DownloadRequest(new Location(metadata.lat(),metadata.lng()),car.getAngle()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }),0,UPDATE_FREQUENCY,TIME_UNIT);
    }
}
