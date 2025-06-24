package com.bottleflip;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameCanvas extends MenuCanvas {

    private Rectangle block;
    private Stage stage;
    private double height;
    private double width;
    private int score;
    private MenuCanvas menuCanvas;
    private AnimationTimer timer;
    private double blockX = 400; // Block position (center)
    private double blockY = 300;
    private double velocityY = 0; // Physics: vertical velocity
    private double blockWidth = 50; // Block dimensions
    private double blockHeight = 100;

   

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

        // Add HUD and buttons to StackPane
        gameHud.getChildren().addAll(hudHolderTop, buttonSettingsOptions);


         // Block setup (acting as the flipped bottle)
        block = new Rectangle(blockWidth, blockHeight);
        block.setFill(Color.RED);
        block.setLayoutX(blockX - blockWidth / 2);
        block.setLayoutY(blockY - blockHeight / 2);

        // Add block to game HUD
        gameHud.getChildren().add(block);

        //select background and add canvas:
        //then send that scene to the next class for game logic and rendering

        // Set scene
        Scene gameScene = new Scene(gameHud, width, height);
        gameScene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case SPACE:
                    velocityY = -10;
                    break;
            }
        });
        stage.setScene(gameScene);
        start();
        
    }

    public void start() {
        if (timer == null) {
            timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    update();
                 
                }
            };
            timer.start();
        }

    }

    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

   private void update() {
    // Apply gravity
    velocityY += 0.5;
    blockY += velocityY;

    // Bounce logic
    if (blockY > height - blockHeight) {
        blockY = height - blockHeight;
        velocityY = -velocityY * 0.8;
    }

    // Move the rectangle (JavaFX Node)
    block.setLayoutY(blockY - blockHeight / 2);
}

    
}