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
import java.util.concurrent.*;

public class Application extends javafx.application.Application {
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Car car;
    private final Scene scene;
    private final ImageView imageView;
    private final Text text;
    private final Predictor predictor;
    private final Downloader downloader;
    private final Cash cash;

    public Application() throws FileNotFoundException {
        car = new Car(45.386601, -75.691193, 330);

        StackPane pane = new StackPane();
        Image image = new Image(new FileInputStream(Objects.requireNonNull(Application.class.getResource("test.jpeg")).getPath()));
        imageView = new ImageView(image);
        pane.getChildren().add(imageView);

        text = new Text("loading");
        pane.getChildren().add(text);
        pane.setAlignment(Pos.TOP_CENTER);
        scene = new Scene(pane);

        String apiKey = System.getenv("API_KEY");
        boolean debug = Boolean.parseBoolean(System.getenv("DEBUG"));
        TransferQueue<DownloadRequest> requests = new LinkedTransferQueue<>();
        cash = new Cash(car);
        predictor = new Predictor(car, apiKey, requests);
        downloader = debug ? new Downloader(apiKey, requests, cash, 10) : new Downloader(apiKey, requests, cash);
        predictor.run();
        new Thread(downloader).start();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            switch (key.getCode()) {
                case W -> car.forward();
                case S -> car.backward();
                case A -> car.left();
                case D -> car.right();
                case SPACE -> car.cruiseControl();
                case BACK_SPACE -> car.stop();
            }
        });
        stage.setTitle("SYSC 4907 Simulator");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> this.exit());
        executorService.scheduleAtFixedRate(this::updateImage, 0, 16700, TimeUnit.MICROSECONDS);
        stage.setMinHeight(700);
        stage.setMinWidth(700);
        stage.setResizable(true);
        stage.show();
    }

    private void exit() {
        car.exit();
        predictor.stop();
        downloader.stop();
        executorService.shutdown();
        System.exit(0);
    }

    private void updateImage() {
        cash.peek().ifPresent(imageView::setImage);
        text.setText(car.toString());
    }
}