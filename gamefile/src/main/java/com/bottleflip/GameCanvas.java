package com.bottleflip;

import java.util.Arrays;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameCanvas extends MenuCanvas {

    private double GROUND_Y=0.0;
    private final double BOTTLE_HEIGHT = 64; // same as fitWidth if square image
    private Stage stage;
    private double height;
    private double width;
    private int score;
    ImageView bottle_image;
    private MenuCanvas menuCanvas;
    public List<String> select_a_game_background = Arrays.asList("file:gamefile/src/main/resources/GameBackGround/city 1/10.png",
    "file:gamefile/src/main/resources/GameBackGround/city 2/10.png");

    public GameCanvas(Stage stage, MenuCanvas menuCanvas, double width, double height, MediaPlayer mediaPlayer) {
        super(stage, height, width);
        this.stage = stage;
        this.menuCanvas = menuCanvas;
        this.height = height;
        this.width = width;
        this.mediaPlayer = mediaPlayer;
        this.score = 0;
        startGame(stage, width, height);
    }

    public void startGame(Stage stage, double width, double height) {
        stage.setHeight(height);
        stage.setWidth(width);
        stage.setResizable(false);
        stage.setTitle("Bottle Flip !");

        // HUD for game
        StackPane gameHud = new StackPane();
        gameHud.setPrefSize(width, height);

        Text gameScore = new Text("Game Score:" + score);
        VBox hudHolderTop = new VBox(gameScore);
        hudHolderTop.setAlignment(Pos.TOP_RIGHT);
        hudHolderTop.setPadding(new Insets(10));

        // Buttons for game menu
        Button buttonOptions = new Button("<-");
        Button buttonMainMenu = new Button("Main Menu");
        VBox buttonSettingsOptions = new VBox(buttonOptions, buttonMainMenu);
        buttonSettingsOptions.setAlignment(Pos.TOP_LEFT);

        // Button actions
        buttonOptions.setOnMouseClicked(e -> {
            Scene optionsScene = menuCanvas.getOptionsScene(stage.getScene());
            stage.setScene(optionsScene);
        });

        buttonMainMenu.setOnMouseClicked(e -> {
            stage.setScene(menuCanvas.getMenuScene());
        });

        //select background and add canvas:
        //then send that scene to the next class for game logic and rendering

        Image bottle = new Image(getClass().getResource("/CowboyBitpop_Bottles_32x32/Bottles_32x32_02.png").toExternalForm());
        bottle_image= new ImageView(bottle);
        StackPane.setAlignment(bottle_image, Pos.BOTTOM_CENTER);
        bottle_image.setFitWidth(64); // or 100
        bottle_image.setPreserveRatio(true);
        GROUND_Y=bottle_image.getY();
        
   

         // Add HUD and buttons to StackPane
        gameHud.getChildren().addAll(hudHolderTop, buttonSettingsOptions, bottle_image);
        // Set scene
        Scene gameScene = new Scene(gameHud, width, height);
        // cube 
        play(gameScene, bottle_image, GROUND_Y);
    }
    //physics constants
    private  final double GRAVITY = 500.0; // Pixels/s^2
    private  final double DAMPING = 0.99; // Air resistance
    private  final double UPRIGHT_THRESHOLD = Math.toRadians(10); // 10 degrees
    

    //bottle event state
    private boolean landed = true;
    private double y = GROUND_Y; // Position (bottom of bottle)
    private double vy = 0; // Vertical velocity
    private double theta = 0; // Rotation angle (radians)
    private double omega = 0; // Angular velocity (radians/s)

    //mouse events
    // Mouse interaction
    private Point2D mouseStart;
    private long mouseStartTime;
   
    public void play(Scene gameScene, ImageView bottleImage, double groundY) {
    bottleImage.setOnMousePressed(event -> {
        mouseStart = new Point2D(event.getSceneX(), event.getSceneY());
        mouseStartTime = System.nanoTime();
        if (landed) {
            landed = false;
        }
        System.out.println("Mouse press at: " + mouseStart);
    });

    // Optionally, you can add drag or release listeners too
    bottleImage.setOnMouseReleased(event -> {
        Point2D mouseEnd = new Point2D(event.getSceneX(), event.getSceneY());
        long mouseEndTime = System.nanoTime();
        double dragDuration = (mouseEndTime - mouseStartTime) / 1e9;

        Point2D velocity = mouseStart.subtract(mouseEnd).multiply(1 / dragDuration);
        System.out.println("Velocity: " + velocity);
    });

     AnimationTimer timer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double dt = (now - lastTime) * 1e-9; // Seconds
                lastTime = now;

                if (!landed) {
                    // Update physics
                    vy += GRAVITY * dt;
                    y += vy * dt;
                    omega *= DAMPING;
                    theta += omega * dt;

                    // Check for ground collision
                    if (y >= GROUND_Y) {
                        y = GROUND_Y;
                        vy = 0;
                        if (Math.abs(theta % (2 * Math.PI)) < UPRIGHT_THRESHOLD ||
                            Math.abs(theta % (2 * Math.PI) - 2 * Math.PI) < UPRIGHT_THRESHOLD) {
                            // Successful landing
                            landed = true;
                            theta = 0;
                            omega = 0;
                        } else {
                            // Failed landing
                            omega *= 0.5; // Reduce rotation
                        }
                    }

                    // Update bottle position and rotation
                    bottleImage.setY(y - BOTTLE_HEIGHT);
                    bottleImage.setRotate(Math.toDegrees(theta));
                }
            }
        };
        timer.start();

    stage.setScene(gameScene);
    stage.show();
}
    
}