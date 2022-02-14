package ca.carleton.sysc4907fx;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application extends javafx.application.Application {
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Car car;
    private final Cash cash;
    private final Scene scene;
    private final ImageView imageView;
    private final Text text;
    private boolean p;
    private final Downloader downloader;
    private final Location location;
    private final Predictor predictor;
    private final String apiKey;
    private final TransferQueue<DownloadRequest> requests;
    private final int angleTolerance;

    public Application() throws FileNotFoundException {
        car = new Car(45.386601, -75.691193);
        angleTolerance = 4;
        apiKey = System.getenv("API_KEY");
        requests = new LinkedTransferQueue<>();
        downloader = new Downloader(apiKey,requests,cash,angleTolerance);
        predictor = new Predictor(car,apiKey,requests);
        location = new Location(45.386601, -75.691193);
        cash = new Cash(car);
        p = true;
        
       
        
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            switch (key.getCode()) {
                case W -> car.Forward();
                case S -> car.Backward();
                case A -> car.Left();
                case D -> car.Right();
            }
        });
        stage.setTitle("SYSC 4907 Simulator");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> this.exit());
        executorService.scheduleAtFixedRate(this::updateImage, 0, 16700, TimeUnit.MICROSECONDS);
        stage.show();
    }

    private void exit() {
        car.exit();
        executorService.shutdown();
        System.exit(0);
    }

    private void updateImage() {
        StackPane pane = new StackPane();
        Optional<Image> image = cash.peek();
        p = image.isPresent();
        if (p == true) { 
            imageView = new ImageView(String.valueOf(image));
        }
        pane.getChildren().add(imageView);
        text = new Text("loading");
        pane.getChildren().add(text);
        pane.setAlignment(Pos.TOP_CENTER);
        scene = new Scene(pane);
        text.setText(car.toString());
    }
}