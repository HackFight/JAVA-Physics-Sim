import vectors.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class RenderPanel extends JPanel implements ActionListener {
    private static RenderPanel INSTANCE;
    private Timer timer;

    private RenderPanel()
    {
        timer = new Timer(16, this);
        timer.start();
    }

    public static RenderPanel getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new RenderPanel();
        }

        return INSTANCE;
    }

    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        BallSim.render(g2);
    }

    public static void drawCircle(Graphics2D g2, int r, int x, int y)
    {
        int d = r * 2;
        g2.fillOval(x - r, y - r, d, d);
    }

    public static Vector2 worldToScreenPos(Vector2 pos)
    {
        return new Vector2(pos.x + getInstance().getWidth() / 2.0, pos.y + getInstance().getHeight() / 2.0);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        repaint();
    }
}
