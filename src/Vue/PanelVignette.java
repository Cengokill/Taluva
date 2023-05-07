package Vue;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import javax.swing.JPanel;

public class PanelVignette extends JPanel {

    public PanelVignette() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        // Créer un dégradé radial avec une opacité progressive (vignette)
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        Point2D center = new Point2D.Float(panelWidth / 2, panelHeight / 2);
        float[] dist = {0.4f, 1.0f};
        Color[] colors = {new Color(0, 0, 0, 0), new Color(0, 0, 0, 130)};
        RadialGradientPaint paint = new RadialGradientPaint(center, Math.max(panelWidth, panelHeight) / 2, dist, colors);

        g2d.setPaint(paint);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        // Ajouter un effet de lumière en haut à gauche (soleil)
        Point2D lightCenter = new Point2D.Float(panelWidth * 0.1f, panelHeight * 0.1f);
        float[] lightDist = {0.0f, 1.0f};
        Color[] lightColors = {new Color(255, 255, 224, 60), new Color(255, 255, 224, 0)};
        RadialGradientPaint lightPaint = new RadialGradientPaint(lightCenter, Math.max(panelWidth, panelHeight) * 0.7f, lightDist, lightColors);

        g2d.setPaint(lightPaint);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        g2d.dispose();
    }
}
