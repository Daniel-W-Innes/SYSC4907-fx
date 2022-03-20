package ca.carleton.sysc4907fx;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

//45.324428, -75.718196 160
public class Application extends javafx.application.Application {
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Car car;
    private final Scene scene;
    private final ImageView imageView;
    private final Text text;
    private final Predictor predictor;
    private final Downloader downloader;
    private final Cash cash;
    private final boolean testingMode;


    private Dialog<Car> getCar(){
        Dialog<Car> dialog = new Dialog<>();
        dialog.setTitle("Input starting location");

        TextField latitude = new TextField();
        latitude.setPromptText("latitude");
        TextField longitude = new TextField();
        longitude.setPromptText("longitude");
        TextField angle = new TextField();
        angle.setPromptText("angle");

        ButtonType confirm = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(confirm);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        gridPane.add(new Label("Latitude:"), 0, 0);
        gridPane.add(latitude, 1,0 );
        gridPane.add(new Label("Longitude:"), 0, 1);
        gridPane.add(longitude, 1, 1);
        gridPane.add(new Label("angle:"), 0, 2);
        gridPane.add(angle, 1, 2);

        dialog.getDialogPane().setContent(gridPane);
        Platform.runLater(latitude::requestFocus);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirm) {
                try {
                    return new Car(new Location(Double.parseDouble(latitude.getText()),Double.parseDouble(longitude.getText())),Integer.parseInt(angle.getText()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
        return dialog;

    }

    public Application() throws Exception {
        String apiKey = System.getenv("API_KEY");
        boolean debug = Boolean.parseBoolean(System.getenv("DEBUG"));
        testingMode = Boolean.parseBoolean(System.getenv("TESTING"));
        if (testingMode){
            car = new Car(new Location(45.324428, -75.718196),160);
        }else {
            Optional<Car> result = getCar().showAndWait();
            if (result.isPresent()){
                car = result.get();
            }else {
                throw new Exception("Missing dialogue");
            }
        }

        StackPane pane = new StackPane();
        Image image = new Image(new FileInputStream(Objects.requireNonNull(Application.class.getResource("test.jpeg")).getPath()));
        imageView = new ImageView(image);
        pane.getChildren().add(imageView);

        text = new Text("loading");
        pane.getChildren().add(text);
        pane.setAlignment(Pos.TOP_CENTER);
        scene = new Scene(pane);

        TransferQueue<DownloadRequest> requests = new LinkedTransferQueue<>();
        cash = new Cash(car);
        predictor = new Predictor(car, apiKey, requests, testingMode);
        downloader = debug ? new Downloader(apiKey, requests, cash, 10, testingMode) : new Downloader(apiKey, requests, cash, testingMode);
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
        if (!testingMode){
            cash.peek().ifPresent(imageView::setImage);
        }
        String s = car.toString();
        if (s != null){
            text.setText(car.toString());
        }
    }
}