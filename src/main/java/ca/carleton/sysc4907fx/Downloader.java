package ca.carleton.sysc4907fx;

import javafx.scene.image.Image;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TransferQueue;

public class Downloader implements Runnable {
    public static final int NUM_THREADS = 10;
    public static final String SIZE = "1280x960";
    private final TransferQueue<DownloadRequest> requests;
    private final BlockingQueue<DownloadRequest> subRequests;
    private final Cash cash;
    private final String apiKey;
    private final int angleTolerance;
    private boolean exit;

    public Downloader(String apiKey, TransferQueue<DownloadRequest> requests, Cash cash) {
        this(apiKey, requests, cash, 180);
    }

    public Downloader(String apiKey, TransferQueue<DownloadRequest> requests, Cash cash, int angleTolerance) {
        this.apiKey = apiKey;
        this.requests = requests;
        this.cash = cash;
        this.angleTolerance = angleTolerance;
        subRequests = new LinkedBlockingDeque<>();
    }

    @Override
    public void run() {
        for (int i = 0; i < NUM_THREADS; i++) {
            new Thread(new Consumer(subRequests)).start();
        }
        while (!exit) {
            try {
                DownloadRequest request = requests.take();
                subRequests.put(request);
                if (!cash.has(request.location())) {
                    int minAngle = request.angle() - angleTolerance / 2;
                    int maxAngle = request.angle() + angleTolerance / 2;
                    if (minAngle < 0) {
                        for (int i = 0; i < maxAngle; i++) {
                            subRequests.put(new DownloadRequest(request.location(), i));
                        }
                        for (int i = 360 + minAngle; i < 360; i++) {
                            subRequests.put(new DownloadRequest(request.location(), i));
                        }
                    } else if (maxAngle > 360) {
                        for (int i = 0; i < -(360 - maxAngle); i++) {
                            subRequests.put(new DownloadRequest(request.location(), i));
                        }
                        for (int i = 360; i > minAngle; i--) {
                            subRequests.put(new DownloadRequest(request.location(), i));
                        }
                    } else {
                        for (int i = minAngle; i < maxAngle; i++) {
                            subRequests.put(new DownloadRequest(request.location(), i));
                        }
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        exit = true;
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
            while (!exit) {
                try {
                    DownloadRequest request = requests.take();
                    HttpGet httpget = new HttpGet("https://maps.googleapis.com/maps/api/streetview?size=" + SIZE + "&location=" + request.location().toString() + "&heading=" + request.angle() + "&key=" + apiKey);
                    try (CloseableHttpResponse response = client.execute(httpget)) {
                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            cash.add(request.location(), request.angle(), new Image(response.getEntity().getContent()));
                        }
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}