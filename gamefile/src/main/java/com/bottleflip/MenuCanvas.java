package com.bottleflip;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;



public class MenuCanvas{

    private Stage stage;
    private Double height;
    private Double width;
    protected MediaPlayer mediaPlayer;
    protected Scene MenuScene;
    public List<String> backgroundPaths = Arrays.asList(
    "file:/Users/ryan/The Bottle Flip Project/gamefile/src/main/resources/nature_4/orig.png",
    "file:/Users/ryan/The Bottle Flip Project/gamefile/src/main/resources/nature_8/orig.png");
    protected String currentBackgroundPath;

    public MenuCanvas(Stage stage, Double height, Double width) {
        this.stage = stage;
        this.height = height;
        this.width = width;
        MenuBuilder(stage, height, width);
        setMenuDisplay();
        
    }

    public void MenuBuilder(Stage stage, double height, double width){

       stage.setHeight(height);
       stage.setWidth(width); 
       stage.setResizable(false);
       stage.setTitle("Bottle Flip !");
    
    }
    private void  setupMenuScene(String currentBackgroundPath){
        this.currentBackgroundPath=currentBackgroundPath;
    }
     private String  getupMenuScene(){
     return currentBackgroundPath;
    }

    public void setMenuDisplay() {
    
        //adding file path of image to the background;
        // Image mainBackground1 = new Image("file:/Users/ryan/The Bottle Flip Project/gamefile/src/main/resources/nature_4/orig.png");
        // Image mainBackground2= new Image("file:/Users/ryan/The Bottle Flip Project/gamefile/src/main/resources/nature_8/orig.png");
        Collections.shuffle(backgroundPaths); // randomize order
        currentBackgroundPath = backgroundPaths.get(0);
        setupMenuScene(currentBackgroundPath);
        Image mainBackground = new Image(currentBackgroundPath); // pick the first one
        ImageView backgroundView = new ImageView(mainBackground);

        //adding music file for menus;

        String musicPath = "/Users/ryan/The Bottle Flip Project/gamefile/src/main/resources/Audio/Kevin MacLeod - Itty Bitty 8 Bit.mp3";
        Media media = new Media(new File(musicPath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // loop music
        mediaPlayer.play();

        //displaying image ;
        backgroundView.setFitWidth(width);
        backgroundView.setFitHeight(height);
        backgroundView.setPreserveRatio(false);

        //initiating menu for holding buttons
        StackPane menuLayout = new StackPane();
        menuLayout.setAlignment(Pos.CENTER);

        //initiating buttons
        Button buttonStart = new Button("Start Flipping");
        Button buttonExit = new Button("End Game");
        Button buttonOptions = new Button("Settings");

        //initiating Vbox to hold buttons 
        VBox buttonholds= new VBox(50);
        buttonholds.setAlignment(Pos.CENTER);
        buttonholds.getChildren().addAll(buttonStart, buttonOptions, buttonExit);
        
        //menulayout holding children nodes here button, and background view
        //remember there is a hierachy so suppose you put backgrounds after buttonholds then the scene would only render background , so there is a stack hiereachy
        menuLayout.getChildren().add(backgroundView);
        menuLayout.getChildren().add(buttonholds);
        

        MenuScene = new Scene(menuLayout, width, height);

        //event managing:
         buttonStart.setOnMouseClicked(event -> {  //handles start button
             GameCanvas game = new GameCanvas(stage, this, width,height, mediaPlayer);//game canvas Class here
             
        });
        buttonOptions.setOnMouseClicked(event -> {   //handels otpions
            stage.setScene(getOptionsScene(MenuScene));
        });
         buttonExit.setOnMouseClicked(event -> {    //handles exit button
            Platform.exit();
        });
        
        stage.setScene(MenuScene);
        
        stage.show();    
       
}

public Scene getOptionsScene(Scene returnToScene) {
    StackPane optionsLayout = new StackPane();
    optionsLayout.setAlignment(Pos.TOP_RIGHT);

    // Re-use current background
    Image mainBackground = new Image(currentBackgroundPath);
    ImageView backgroundOption = new ImageView(mainBackground);
    backgroundOption.setFitWidth(width);
    backgroundOption.setFitHeight(height);
    backgroundOption.setPreserveRatio(false);

    // Buttons
    Button buttonAudio = new Button(mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING ? "Mute" : "Unmute");
    Button buttonBack = new Button("<- Back");
    Button buttonMainMenu = new Button("Main Menu");

    // Audio toggle
    buttonAudio.setOnMouseClicked(event -> {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                buttonAudio.setText("Unmute");
            } else {
                mediaPlayer.play();
                buttonAudio.setText("Mute");
            }
        }
    });

    // Back to previous scene
    buttonBack.setOnMouseClicked(event -> stage.setScene(returnToScene));

    // Back to main menu
    buttonMainMenu.setOnMouseClicked(event -> stage.setScene(MenuScene));

    VBox optionsBox = new VBox(10, buttonAudio, buttonBack, buttonMainMenu);
    optionsBox.setAlignment(Pos.CENTER);

    optionsLayout.getChildren().addAll(backgroundOption, optionsBox);
    return new Scene(optionsLayout, width, height);
}
public Scene getMenuScene() {
    return this.MenuScene;
}
}