import vectors.Vector2;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BallSim
{
    private static JFrame frame;

    public static ArrayList<Object> objects = new ArrayList<Object>();

    public BallSim()
    {
        frameInit();

        long previousTime = System.nanoTime();
        long deltaTime = 0; //Last cycle time in nanoseconds

        Object ball = new Object();
        objects.add(ball);

        boolean running = true;
        while (running)
        {
            deltaTime = System.nanoTime() - previousTime;
            previousTime = System.nanoTime();

            ball.body.accelerate(new Vector2(0.0,  0.0981));
            ball.body.update(deltaTime);
        }
    }

    public static void render(Graphics2D g2)
    {
        for (Object object : objects)
        {
            object.render(g2);
        }
    }

    public static void frameInit()
    {
        // Creating instance of JFrame
        frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        // Panel to display render results
        pane.add(RenderPanel.getInstance(), BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args)
    {
        BallSim sim = new BallSim();
    }
}