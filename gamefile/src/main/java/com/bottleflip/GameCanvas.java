package com.bottleflip;
import javafx.scene.control.Button; 
import java.awt.Menu;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//Dev note :fix audio double audio in the options
public class GameCanvas extends MenuCanvas{

    private Stage stage;
    private double height;
    private double width;
    private int score;

    public GameCanvas(Stage stage, double width, double height, MediaPlayer mediaPlayer) {
        super(stage, height, width);
        this.stage = stage;
        this.height = height;
        this.width = width;
        this.mediaPlayer=mediaPlayer;
        this.score=0;
        startGame(stage,width,height);
    }

    public void startGame(Stage stage, double width, double height){
        
        stage.setHeight(height);
        stage.setWidth(width); 
        stage.setResizable(false);
        stage.setTitle("Bottle Flip !");   
        
        //hud game:
        StackPane gameHud = new StackPane();
        gameHud.setPrefSize(width, height);

        Text gameScore = new Text("Game Score:"+score);//score updates here remember!!!!!

        // VBox to hold score HUD
        VBox hudHolderTop = new VBox(gameScore);
        hudHolderTop.setAlignment(Pos.TOP_RIGHT);
        hudHolderTop.setPadding(new Insets(10));

        //backbutton for game menu canvas
        //back
        Button buttonOptions = new Button("<-");
        VBox buttonSettingsOptions = new VBox(buttonOptions);
        buttonSettingsOptions.setAlignment(Pos.TOP_LEFT);
        //back action
        buttonOptions.setOnMouseClicked(e -> {
                Scene options = getOptionsScene(stage.getScene());
                stage.setScene(options);
            });

        // Add score overlay to StackPane
        gameHud.getChildren().add(hudHolderTop);
        gameHud.getChildren().add(buttonSettingsOptions);

        //scene companent
        Scene gameScene= new Scene(gameHud,width,height);
        stage.setScene(gameScene);
        stage.show();    
    }



}