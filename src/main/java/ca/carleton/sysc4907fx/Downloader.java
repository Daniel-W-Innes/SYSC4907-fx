package ca.carleton.sysc4907fx;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import javafx.scene.image.Image;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.*;

public class Downloader implements Runnable{
    private final TransferQueue<DownloadRequest> requests;
    private final BlockingQueue<DownloadRequest> subRequests;
    private final Cash cash;
    private final String apiKey;
    public static final long timeout =1;
    public static final TimeUnit timeOutUnit = TimeUnit.SECONDS;
    public static final int numThreads = 10;
    public static final int angleTolerance = 180;
    public static final String size = "1280x960";
    private boolean exit;

    public Downloader(Car car, String apiKey) {
        this.apiKey = apiKey;
        requests = new LinkedTransferQueue<>();
        cash = new Cash(car);
        subRequests = new LinkedBlockingDeque<>();
    }

    private class Consumer implements Runnable {
        private final BlockingQueue<DownloadRequest> requests;
        private final CloseableHttpClient client;

        private Consumer(BlockingQueue<DownloadRequest> requests) {
            this.requests = requests;
             client = HttpClients.createDefault();
        }


        @Override
        public void run() {
            while (!exit){
                try {
                    DownloadRequest request = requests.take();
                    HttpGet httpget = new HttpGet("https://maps.googleapis.com/maps/api/streetview?size="+size+"&location="+request.location().toString()+"&heading="+request.angle()+"&amp;key="+apiKey);
                    try (CloseableHttpResponse response = client.execute(httpget)){
                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                            cash.add(request.location(),request.angle(),new Image(response.getEntity().getContent()));
                        }
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < numThreads; i++) {
            new Thread(new Consumer(subRequests)).start();
        }
        while (!exit){
            try {
                DownloadRequest request = requests.take();
                if (!cash.has(request.location())){
                    int minAngle = request.angle() -angleTolerance/2;
                    if (minAngle < 0){
                        minAngle = 360 + minAngle;
                    }
                    int maxAngle = request.angle()+ angleTolerance/2;
                    if (maxAngle > 360){
                        maxAngle = 360 - maxAngle;
                    }
                    for (int i = minAngle; i <maxAngle; i++) {
                        subRequests.offer(new DownloadRequest(request.location(), i));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Optional<Image> peek(){
        return cash.peek();
    }

    public void stop() {
        exit = true;
    }

    public boolean tryTransfer(DownloadRequest request) throws InterruptedException {
        return requests.tryTransfer(request, timeout,timeOutUnit);
    }
}