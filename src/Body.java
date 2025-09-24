import vectors.Vector2;

import java.time.Duration;

public class Body
{
    private Vector2 pos = new Vector2(0.0, 0.0);
    public Vector2 getPos(){return pos;}
    private Vector2 oldPos = new Vector2(0.0, 0.0);
    private Vector2 acc = new Vector2(0.0, 0.0);

    public void accelerate(Vector2 v)
    {
        acc = acc.add(v);
    }

    public void update(long dt)
    {
        double dts = dt/ 1000000000.0; //Convert nanoseconds to seconds

        Vector2 tempPos = pos;
        pos = pos.multiply(2).add(oldPos.multiply(-1)).add(acc.multiply(dts*dts));
        oldPos = tempPos;
        acc = acc.multiply(0);
    }
}