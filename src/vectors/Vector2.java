package vectors;

public class Vector2
{
    public double x;
    public double y;

    public Vector2(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 v)
    {
        return Vector2.sum(this, v);
    }
    public static Vector2 sum(Vector2 v1, Vector2 v2)
    {
        return new Vector2( v1.x + v2.x, v1.y + v2.y );
    }

    public Vector2 multiply(double scalar)
    {
        return Vector2.product(this, scalar);
    }
    public static Vector2 product(Vector2 v, double scalar)
    {
        return new Vector2(v.x * scalar, v.y * scalar);
    }

    public double dot(Vector2 v)
    {
        return dotProduct(this ,v);
    }
    public static double dotProduct(Vector2 v1, Vector2 v2)
    {
        return v1.x * v2.x + v1.y * v2.y;
    }

    public double magnitude()
    {
        return magnitude(this);
    }
    public static double magnitude(Vector2 v)
    {
        return Math.sqrt(Math.pow(Math.abs(v.x), 2) + Math.pow(Math.abs(v.y), 2));
    }

    public static boolean checkZero(Vector2 v)
    {
        return v.magnitude() == 0;
    }
    public boolean isZero()
    {
        return checkZero(this);
    }

    public static Vector2 normalize(Vector2 v)
    {
        if (v.isZero())
        {
            throw new IllegalArgumentException();
        }
        else
        {
            return v.multiply(1.0/v.magnitude());
        }
    }
    public Vector2 normalize()
    {
        return normalize(this);
    }
}