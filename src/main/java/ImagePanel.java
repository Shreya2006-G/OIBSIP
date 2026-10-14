
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImagePanel extends JPanel {

    private BufferedImage image;

    public ImagePanel(String resourcePath) {
        try {
            java.net.URL imageURL = getClass().getResource(resourcePath);

            if (imageURL == null) {
                System.err.println("IMAGE NOT FOUND: " + resourcePath);
            } else {
                image = ImageIO.read(imageURL);
                System.out.println("IMAGE LOADED: " + resourcePath);
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Could not load image: " + resourcePath);
        }

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image == null) {
            g.setColor(new Color(222, 209, 190));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        double scale = Math.max(
                (double) getWidth() / image.getWidth(),
                (double) getHeight() / image.getHeight()
        );

        int width = (int) (image.getWidth() * scale);
        int height = (int) (image.getHeight() * scale);

        int x = (getWidth() - width) / 2;
        int y = (getHeight() - height) / 2;

        g2.setClip(new java.awt.geom.RoundRectangle2D.Double(
                0, 0, getWidth(), getHeight(), 14, 14
        ));

        g2.drawImage(image, x, y, width, height, this);
        g2.dispose();
    }
}
