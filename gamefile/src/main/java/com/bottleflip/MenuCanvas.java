package com.bottleflip;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class MenuCanvas{

    private Stage stage;
    private Double height;
    private Double width;
    private MediaPlayer mediaPlayer;
    private List<String> backgroundPaths = Arrays.asList(
    "file:/Users/ryan/The Bottle Flip Project/gamefile/src/main/resources/nature_4/orig.png",
    "file:/Users/ryan/The Bottle Flip Project/gamefile/src/main/resources/nature_8/orig.png");
    private String currentBackgroundPath;

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
        //member there is a hierachy so suppose you put backgrounds after buttonholds then the scene would only render background , so there is a stack hiereachy
        menuLayout.getChildren().add(backgroundView);
        menuLayout.getChildren().add(buttonholds);
        

        Scene MenuScene = new Scene(menuLayout, width, height);

        //event managing:
        buttonOptions.setOnMouseClicked(event -> {
            setOptionsDiplay(MenuScene, mediaPlayer,backgroundView);
        });
         buttonExit.setOnMouseClicked(event -> {
            Platform.exit();
        });
        
        stage.setScene(MenuScene);
        stage.show();    
       
}

public void setOptionsDiplay(Scene scene, MediaPlayer musicPlaying, ImageView backgroundView){

    
    StackPane optionsLayout = new StackPane();
    optionsLayout.setAlignment(Pos.TOP_RIGHT);

    Button buttonAudio = new Button("Toggle Audio");
    Button buttonBack = new Button("<-Back<-");

    VBox optionsBox= new VBox(10);
    optionsBox.setAlignment(Pos.CENTER);
    optionsBox.getChildren().addAll(buttonAudio,buttonBack);

    //buttons and background in options
    Image mainBackground = new Image(currentBackgroundPath); // pick the first one
    ImageView backgroundOption = new ImageView(mainBackground);
            //displaying image ;
                backgroundOption.setFitWidth(width);
                backgroundOption.setFitHeight(height);
                backgroundOption.setPreserveRatio(false);
    optionsLayout.getChildren().add(backgroundOption);
    optionsLayout.getChildren().add(optionsBox);
    

    Scene optionScene= new Scene(optionsLayout);

    stage.setScene(optionScene);
   

    //button action, handeling in options menu;
    buttonBack.setOnMouseClicked((event) -> {
        stage.setScene(scene);    
    });
    buttonAudio.setOnMouseClicked((event) -> {
        if(musicPlaying!=null){
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        }
    });

}
    
}