package com.bottleflip;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
      try {
        MenuCanvas startGame = new MenuCanvas(primaryStage, 720.0, 580.0);
        startGame.setMenuDisplay();
          
      } catch (Exception e) {
      }
    }

    public static void main(String[] args) {
        launch(args);
    }
}