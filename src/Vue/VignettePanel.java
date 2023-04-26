package Vue;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import javax.swing.JPanel;

public class VignettePanel extends JPanel {

    public VignettePanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        // Créer un dégradé radial avec une opacité progressive
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        Point2D center = new Point2D.Float(panelWidth / 2, panelHeight / 2);
        float[] dist = {0.4f, 1.0f}; // Augmentez la distance initiale pour rendre le centre plus transparent
        Color[] colors = {new Color(0, 0, 0, 0), new Color(0, 0, 0, 130)}; // Réduisez l'opacité du noir dans les coins (77/255 ~ 30%)
        RadialGradientPaint paint = new RadialGradientPaint(center, Math.max(panelWidth, panelHeight) / 2, dist, colors);

        g2d.setPaint(paint);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        g2d.dispose();
    }
}
