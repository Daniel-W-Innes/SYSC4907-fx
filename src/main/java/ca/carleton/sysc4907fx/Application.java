package ca.carleton.sysc4907fx;

import io.grpc.ManagedChannelBuilder;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application extends javafx.application.Application {
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Car car;
    private final ImageDownloaderGrpc.ImageDownloaderBlockingStub stub;
    private final Scene scene;
    private final ImageView imageView;
    private final Text text;

    public Application() throws FileNotFoundException {
        car = new Car(45.386601, -75.691193);

        StackPane pane = new StackPane();
        Image image = new Image(new FileInputStream(Objects.requireNonNull(Application.class.getResource("test.jpeg")).getPath()));
        imageView = new ImageView(image);
        pane.getChildren().add(imageView);

         text = new Text("loading");
        pane.getChildren().add(text);
        pane.setAlignment(Pos.TOP_CENTER);

        scene = new Scene(pane);

        io.grpc.Channel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().maxInboundMessageSize(20000000).build();
        stub = ImageDownloaderGrpc.newBlockingStub(channel);
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
        text.setText(car.toString());
        Proxy.Image image = stub.getImage(car.getLatLong());
        imageView.setImage(new Image(new ByteArrayInputStream(image.getImageData().toByteArray())));
    }

    public static void main(String[] args) {
        launch();
    }
}