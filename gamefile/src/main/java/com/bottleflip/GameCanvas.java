package com.bottleflip;

import java.util.Arrays;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameCanvas extends MenuCanvas {

    private Stage stage;
    private double height;
    private double width;
    private int score;
    ImageView bottle_image;
    private MenuCanvas menuCanvas;
    private double x_bottle_loc_new=0.0;//moving mouse event in start canvas
    private double y_bottle_loc_new=0.0;
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
        Pane gameHud = new Pane();
        gameHud.setPrefSize(width, height);

        Text gameScore = new Text("Game Score:" + score);
        VBox scoreHudVBox = new VBox(gameScore);
        scoreHudVBox.setLayoutX(width-100);

        // Buttons for game menu
        Button buttonOptions = new Button("<-");
        Button buttonMainMenu = new Button("Main Menu");
        VBox buttonSettingsOptions = new VBox(buttonOptions, buttonMainMenu);
        buttonSettingsOptions.setLayoutX(10);
        buttonSettingsOptions.setLayoutX(10);

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

        /// remember there is a glitch with button presses so its because of the offset, use an array to solve that!!

        Image bottle = new Image(getClass().getResource("/CowboyBitpop_Bottles_32x32/Bottles_32x32_02.png").toExternalForm());
        bottle_image= new ImageView(bottle);
        bottle_image.setFitWidth(64); // or 100
        bottle_image.setPreserveRatio(true);
        bottle_image.setLayoutX(width/2-(bottle_image.getImage().getWidth()/2));//initial X position
        bottle_image.setLayoutY(height-(bottle_image.getImage().getHeight())*4);//inital height
                // Offset variables
        final double[] offsetX = new double[1];
        final double[] offsetY = new double[1];
        final double[] initialX =new double[1];
        final double[] initialY =new double[1];
         // Add HUD and buttons to StackPane
        gameHud.getChildren().addAll(scoreHudVBox,buttonSettingsOptions, bottle_image);
        // Set scene
        Scene gameScene = new Scene(gameHud, width, height);
        // cube 
        //on mouse click;reset
        initialX[0]=bottle_image.getLayoutX();
        initialY[0]=bottle_image.getLayoutY();
    
        boolean left= false;
        boolean right=false;
        final long[] pressTime = new long[1]; // stores press time

        //press
        bottle_image.setOnMousePressed(event -> {
            pressTime[0] = System.currentTimeMillis(); // start timer
            
        });

        //on drag; move
        bottle_image.setOnMouseDragged((mouseEvent) -> {
            bottle_image.setLayoutX(mouseEvent.getSceneX() - offsetX[0]);
            bottle_image.setLayoutY(mouseEvent.getSceneY() - offsetY[0]);
            // long releaseTime = System.currentTimeMillis();
            // double elapsedSeconds = (releaseTime - pressTime[0]) / 1000.0;

            // //how to flip logic!
            // //width/2 is the middle if movement is > middle then bottle flips +90deg clock wise
            // double distance;
            // double vel;
           
            // distance=Math.pow(mouseEvent.getSceneX()- offsetX[0],2)+Math.pow(mouseEvent.getSceneY() - offsetY[0],2);
            // vel=distance/elapsedSeconds;

            System.out.println("X position of bottle :"+bottle_image.getLayoutX()+" Y position of bottle :"+bottle_image.getLayoutY());
            // System.out.println("Velocity:"+vel);
            // vel=0.0;
        });

        bottle_image.setOnMouseReleased((mouseEvent)->{
            long releaseTime = System.currentTimeMillis();
            double dt = (releaseTime - pressTime[0]) / 1000.0;

            double dx = mouseEvent.getSceneX() - initialX[0];
            double dy = mouseEvent.getSceneY() - initialY[0];
            double distance = Math.sqrt(dx * dx + dy * dy);
            double velocity = distance / dt;
            double direction = dx >= 0 ? 1 : -1;

            System.out.println("Velocity: " + velocity);

            if (velocity > 300) {
                animateBottleFlip(direction, velocity);
            }
        });
        //reset variables
        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) {
                bottle_image.setLayoutX(initialX[0]);
                bottle_image.setLayoutY(initialY[0]);
                 bottle_image.setRotate(theta[0]);

                System.out.println("RESET: X position of bottle :"+bottle_image.getLayoutX()+" Y position of bottle :"+bottle_image.getLayoutY());
            }
        });

        stage.setScene(gameScene);
        stage.show();  
    }
     final double[] theta=new double[1];
     private void animateBottleFlip(double direction, double velocity) {
        final double GRAVITY = 980;
        final double DAMPING = 0.985;
        final double[] vy = { -velocity };
        final double[] y = { bottle_image.getLayoutY() };
        final double[] omega = { 720 * direction };
        final double[] theta = { bottle_image.getRotate() };

        new AnimationTimer() {
            long lastTime = -1;
            @Override
            public void handle(long now) {
                if (lastTime < 0) {
                    lastTime = now;
                    return;
                }

                double elapsed = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                vy[0] += GRAVITY * elapsed; // 1. Update vertical velocity due to gravity
                y[0] += vy[0] * elapsed;    // 2. Update position based on velocity
                omega[0] *= DAMPING;        // 3. Apply damping to slow down rotation
                theta[0] += omega[0] * elapsed; // 4. Update rotation angle

                bottle_image.setLayoutY(y[0]);
                bottle_image.setRotate(theta[0]);

                if (y[0] >= height - bottle_image.getFitHeight()) {
                    bottle_image.setLayoutY(height - bottle_image.getFitHeight());
                    stop();
                }
               
            }
        }.start();
        
    }

    }
 
