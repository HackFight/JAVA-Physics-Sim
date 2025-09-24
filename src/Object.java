import vectors.Vector2;

import java.awt.*;

public class Object
{
    public Body body;

    public Object()
    {
        body = new Body();
    }

    public void render(Graphics2D g2)
    {
        Vector2 pos = RenderPanel.worldToScreenPos(body.getPos());

        g2.setPaint(Color.RED);
        RenderPanel.drawCircle(g2, 10, (int) pos.x, (int) pos.y);
    }
}
