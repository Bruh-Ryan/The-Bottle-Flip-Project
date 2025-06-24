import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BottleFlip extends Application {
    // Physics constants
    private static final double GRAVITY = 500.0; // Pixels/s^2
    private static final double DAMPING = 0.99; // Air resistance
    private static final double UPRIGHT_THRESHOLD = Math.toRadians(10); // 10 degrees
    private static final double BOTTLE_WIDTH = 20;
    private static final double BOTTLE_HEIGHT = 60;
    private static final double GROUND_Y = 400;

    // Bottle state
    private double y = GROUND_Y; // Position (bottom of bottle)
    private double vy = 0; // Vertical velocity
    private double theta = 0; // Rotation angle (radians)
    private double omega = 0; // Angular velocity (radians/s)
    private boolean landed = true;

    // Mouse interaction
    private Point2D mouseStart;
    private long mouseStartTime;

    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();
        Rectangle bottle = new Rectangle(200 - BOTTLE_WIDTH / 2, GROUND_Y - BOTTLE_HEIGHT,
                BOTTLE_WIDTH, BOTTLE_HEIGHT);
        bottle.setFill(Color.BLUE);
        pane.getChildren().add(bottle);

        // Mouse event handlers
        pane.setOnMousePressed(event -> {
            mouseStart = new Point2D(event.getX(), event.getY());
            mouseStartTime = System.nanoTime();
            if (landed) {
                landed = false;
            }
        });

        pane.setOnMouseReleased(event -> {
            if (mouseStart != null && !landed) {
                Point2D mouseEnd = new Point2D(event.getX(), event.getY());
                long mouseEndTime = System.nanoTime();
                double dt = (mouseEndTime - mouseStartTime) * 1e-9; // Seconds
                Point2D drag = mouseEnd.subtract(mouseStart);
                // Apply upward velocity and angular velocity
                vy = -drag.getY() * 2; // Scale drag to velocity
                omega = -drag.getX() * 0.1; // Scale drag to rotation
                mouseStart = null;
            }
        });

        // Animation loop
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
                    bottle.setY(y - BOTTLE_HEIGHT);
                    bottle.setRotate(Math.toDegrees(theta));
                }
            }
        };
        timer.start();

        Scene scene = new Scene(pane, 400, 500);
        primaryStage.setTitle("Bottle Flip Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}