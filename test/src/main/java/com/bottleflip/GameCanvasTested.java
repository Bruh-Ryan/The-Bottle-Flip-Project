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

public class GameCanvasTested extends MenuCanvas {

    private double GROUND_Y = 0.0;
    private final double BOTTLE_HEIGHT = 64;
    private Stage stage;
    private double height;
    private double width;
    private int score;
    private ImageView bottle_image;
    private MenuCanvas menuCanvas;
    private MediaPlayer mediaPlayer;
    private Text gameScore; // Added field to store Text node
    public List<String> select_a_game_background = Arrays.asList(
        "file:gamefile/src/main/resources/GameBackGround/city 1/10.png",
        "file:gamefile/src/main/resources/GameBackGround/city 2/10.png"
    );

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

        gameScore = new Text("Game Score: " + score); // Initialize Text node
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

        // Bottle setup
        Image bottle = new Image(getClass().getResource("/CowboyBitpop_Bottles_32x32/Bottles_32x32_02.png").toExternalForm());
        bottle_image = new ImageView(bottle);
        StackPane.setAlignment(bottle_image, Pos.BOTTOM_CENTER);
        bottle_image.setFitWidth(64);
        bottle_image.setPreserveRatio(true);
        GROUND_Y = height - BOTTLE_HEIGHT;

        // Add HUD and buttons to StackPane
        gameHud.getChildren().addAll(hudHolderTop, buttonSettingsOptions, bottle_image);
        Scene gameScene = new Scene(gameHud, width, height);
        
        // Start game logic
        play(gameScene, bottle_image, GROUND_Y);
    }

    // Physics constants
    private final double GRAVITY = 500.0;
    private final double DAMPING = 0.99;
    private final double UPRIGHT_THRESHOLD = Math.toRadians(10);
    private final double THROW_SCALE = 2.0;

    // Bottle state
    private boolean landed = true;
    private boolean isDragging = false;
    private double x = 0;
    private double y = 0;
    private double vx = 0;
    private double vy = 0;
    private double theta = 0;
    private double omega = 0;

    // Mouse interaction
    private Point2D mouseStart;
    private long mouseStartTime;
    private double initialX;
    private double initialY;

    public void play(Scene gameScene, ImageView bottleImage, double groundY) {
        GROUND_Y = groundY;
        x = width / 2;
        y = GROUND_Y;

        // Mouse press - start drag or reset
        bottleImage.setOnMousePressed(event -> {
            if (landed) {
                mouseStart = new Point2D(event.getSceneX(), event.getSceneY());
                mouseStartTime = System.nanoTime();
                initialX = x;
                initialY = y;
                isDragging = true;
                landed = false;
            } else {
                resetBottle();
            }
            event.consume();
        });

        // Mouse drag - update bottle position
        bottleImage.setOnMouseDragged(event -> {
            if (isDragging) {
                double mouseX = event.getSceneX();
                double mouseY = event.getSceneY();
                x = mouseX;
                y = mouseY;
                bottleImage.setTranslateX(x - width / 2);
                bottleImage.setTranslateY(y - GROUND_Y);
            }
            event.consume();
        });

        // Mouse release - throw bottle
        bottleImage.setOnMouseReleased(event -> {
            if (isDragging) {
                isDragging = false;
                Point2D mouseEnd = new Point2D(event.getSceneX(), event.getSceneY());
                long mouseEndTime = System.nanoTime();
                double dragDuration = Math.max((mouseEndTime - mouseStartTime) / 1e9, 0.1);

                // Calculate throw velocity
                Point2D velocity = mouseStart.subtract(mouseEnd).multiply(THROW_SCALE / dragDuration);
                vx = velocity.getX();
                vy = velocity.getY();
                omega = velocity.magnitude() * 0.05;
            }
            event.consume();
        });

        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double dt = (now - lastTime) * 1e-9;
                lastTime = now;

                if (!landed && !isDragging) {
                    // Update physics
                    vy += GRAVITY * dt;
                    x += vx * dt;
                    y += vy * dt;
                    omega *= DAMPING;
                    theta += omega * dt;

                    // Check for ground collision
                    if (y >= GROUND_Y) {
                        y = GROUND_Y;
                        vy = -vy * 0.6;
                        vx *= 0.8;
                        if (Math.abs(vy) < 10 && Math.abs(vx) < 10) {
                            if (Math.abs(theta % (2 * Math.PI)) < UPRIGHT_THRESHOLD ||
                                Math.abs(theta % (2 * Math.PI) - 2 * Math.PI) < UPRIGHT_THRESHOLD) {
                                landed = true;
                                score++;
                                gameScore.setText("Game Score: " + score); // Use stored Text reference
                                theta = 0;
                                omega = 0;
                                vx = 0;
                                vy = 0;
                            } else {
                                omega *= 0.5;
                            }
                        }
                    }

                    // Update bottle position and rotation
                    bottleImage.setTranslateX(x - width / 2);
                    bottleImage.setTranslateY(y - GROUND_Y);
                    bottleImage.setRotate(Math.toDegrees(theta));
                }
            }
        };
        timer.start();

        stage.setScene(gameScene);
        stage.show();
    }

    private void resetBottle() {
        landed = true;
        isDragging = false;
        x = width / 2;
        y = GROUND_Y;
        vx = 0;
        vy = 0;
        theta = 0;
        omega = 0;
        bottle_image.setTranslateX(0);
        bottle_image.setTranslateY(0);
        bottle_image.setRotate(0);
    }
}