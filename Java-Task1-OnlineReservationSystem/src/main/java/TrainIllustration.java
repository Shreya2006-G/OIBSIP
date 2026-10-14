
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class TrainIllustration extends JPanel {

    private final Color cream = new Color(245, 239, 228);
    private final Color moss = new Color(126, 144, 115);
    private final Color deepMoss = new Color(91, 108, 79);
    private final Color cocoa = new Color(73, 56, 45);
    private final Color gold = new Color(216, 183, 121);
    private final Color hill = new Color(199, 205, 183);

    public TrainIllustration() {
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(340, 200));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int w = getWidth();
        int h = getHeight();

        // Soft countryside background
        g2.setColor(cream);
        g2.fillRoundRect(0, 0, w, h, 24, 24);

        // Golden-hour sun
        g2.setColor(gold);
        g2.fill(new Ellipse2D.Double(
                w * 0.73, h * 0.10,
                h * 0.24, h * 0.24
        ));

        // Distant rolling hills
        Path2D hill1 = new Path2D.Double();
        hill1.moveTo(0, h * 0.68);
        hill1.curveTo(
                w * 0.22, h * 0.42,
                w * 0.42, h * 0.78,
                w * 0.62, h * 0.60
        );
        hill1.curveTo(
                w * 0.79, h * 0.45,
                w * 0.91, h * 0.65,
                w, h * 0.55
        );
        hill1.lineTo(w, h);
        hill1.lineTo(0, h);
        hill1.closePath();

        g2.setColor(hill);
        g2.fill(hill1);

        // Foreground hill
        Path2D hill2 = new Path2D.Double();
        hill2.moveTo(0, h * 0.80);
        hill2.curveTo(
                w * 0.28, h * 0.62,
                w * 0.53, h * 0.93,
                w * 0.76, h * 0.73
        );
        hill2.curveTo(
                w * 0.87, h * 0.67,
                w * 0.95, h * 0.74,
                w, h * 0.72
        );
        hill2.lineTo(w, h);
        hill2.lineTo(0, h);
        hill2.closePath();

        g2.setColor(moss);
        g2.fill(hill2);

        // Railway track
        g2.setColor(cocoa);
        g2.setStroke(new BasicStroke(
                2f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        g2.drawLine(
                (int) (w * 0.08), (int) (h * 0.91),
                (int) (w * 0.93), (int) (h * 0.91)
        );

        for (int i = 12; i < 96; i += 12) {
            int x = w * i / 100;
            g2.drawLine(
                    x, (int) (h * 0.885),
                    x, (int) (h * 0.94)
            );
        }

        // Train body
        int tx = (int) (w * 0.20);
        int ty = (int) (h * 0.45);
        int tw = (int) (w * 0.48);
        int th = (int) (h * 0.32);

        g2.setColor(cocoa);
        g2.fill(new RoundRectangle2D.Double(
                tx, ty, tw, th, 12, 12
        ));

        g2.setColor(moss);
        g2.fill(new RoundRectangle2D.Double(
                tx + 3, ty + 3, tw - 6, th - 6, 10, 10
        ));

        // Train front
        g2.setColor(cocoa);
        g2.fillRoundRect(
                tx + tw - 12, ty + 12,
                (int) (tw * 0.28), th - 12, 10, 10
        );

        g2.setColor(gold);
        g2.fillRoundRect(
                tx + tw - 8, ty + 17,
                (int) (tw * 0.22), th - 22, 7, 7
        );

        // Windows
        g2.setColor(cream);
        g2.fillRoundRect(
                tx + 12, ty + 10, 25, 22, 5, 5
        );
        g2.fillRoundRect(
                tx + 45, ty + 10, 25, 22, 5, 5
        );

        // Chimney
        g2.setColor(cocoa);
        g2.fillRoundRect(
                tx + 18, ty - 17, 12, 20, 4, 4
        );

        g2.fillOval(tx + 16, ty - 20, 16, 7);

        // Wheels
        int wheelY = ty + th - 3;

        g2.setColor(cocoa);
        g2.fillOval(tx + 12, wheelY, 25, 25);
        g2.fillOval(tx + 55, wheelY, 25, 25);
        g2.fillOval(tx + tw - 2, wheelY, 25, 25);

        g2.setColor(cream);
        g2.fillOval(tx + 20, wheelY + 8, 9, 9);
        g2.fillOval(tx + 63, wheelY + 8, 9, 9);
        g2.fillOval(tx + tw + 6, wheelY + 8, 9, 9);

        // Little steam clouds
        g2.setColor(new Color(255, 253, 248, 225));
        g2.fillOval(tx + 7, ty - 37, 17, 13);
        g2.fillOval(tx + 17, ty - 44, 21, 17);
        g2.fillOval(tx + 32, ty - 37, 17, 13);

        // Small flying birds
        g2.setColor(cocoa);
        g2.setStroke(new BasicStroke(1.5f));

        g2.drawArc(
                (int) (w * 0.68), (int) (h * 0.25),
                12, 9, 15, 140
        );

        g2.drawArc(
                (int) (w * 0.72), (int) (h * 0.25),
                12, 9, 25, 140
        );

        g2.dispose();
    }
}
