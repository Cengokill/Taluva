package Vue;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Camera {
    private static final int SHAKE_DURATION = 200; // Durée de l'effet en millisecondes
    private static final int SHAKE_INTERVAL = 25; // Intervalle entre les mouvements en millisecondes
    private static final int SHAKE_DISTANCE = 10; // Distance maximale de déplacement en pixels

    public static final int tile_size = 148;
    public static double zoomFactor = 0.2;
    public static final double zoomIncrement = 0.1;
    public static Point hoverTilePosition = new Point(-tile_size, -tile_size);
    public static final Point cameraOffset = new Point(-4400/2, -3600/2);
    public static Point lastMousePosition;

    public static void shake() {
        Random random = new Random();
        long startTime = System.currentTimeMillis();
        long endTime = startTime + SHAKE_DURATION;

        // Créez un Timer pour exécuter l'animation en arrière-plan
        Timer timer = new Timer(SHAKE_INTERVAL, null);
        timer.addActionListener(e -> {
            if (System.currentTimeMillis() >= endTime) {
                // Arrêtez le Timer et réinitialisez les décalages de la caméra
                timer.stop();

            } else {
                int deltaX = random.nextInt(SHAKE_DISTANCE * 2) - SHAKE_DISTANCE;
                int deltaY = random.nextInt(SHAKE_DISTANCE * 2) - SHAKE_DISTANCE;

                // Mettre à jour les décalages de la caméra
                cameraOffset.x += deltaX;
                cameraOffset.y += deltaY;
            }
        });

        // Démarrez le Timer
        timer.start();
    }
}
