package dev.hackfight.physics2d;

import org.joml.*;

public class PointBody2D {
    private Vector2f position_;
    private Vector2f lastPosition_;
    private Vector2f acceleration_;

    public PointBody2D(Vector2f pos) {
        position_ = pos;
        lastPosition_ = pos;
        acceleration_ = new Vector2f(0f, 0f);
    }
    public PointBody2D() {
        this(new Vector2f(0f, 0f));
    }

    public Vector2f getPosition() {
        return position_;
    }
    public void setPosition(Vector2f pos, boolean conserveVel) {
        if (conserveVel) {
            Vector2f vel = getVelocity();
            position_ = pos;
            setVelocity(vel);
        } else {
            position_ = pos;
            lastPosition_ = pos;
        }
    }
    public void setPosition_(Vector2f pos) {
        setPosition(pos, false);
    }

    public Vector2f getVelocity() {
        return position_.sub(lastPosition_);
    }
    public void setVelocity(Vector2f vel) {
        lastPosition_ = position_.sub(vel);
    }

    public void accelerate(Vector2f acc) {
        acceleration_ = acceleration_.add(acc);
    }

    public void update(float dt) {
        Vector2f tempPos = position_;
        position_ = position_.mul(2);
        position_ = position_.sub(lastPosition_);
        Vector2f temp = acceleration_.mul(dt *dt);
        position_ = position_.add(temp);

        lastPosition_ = tempPos;
        acceleration_ = acceleration_.mul(0);
    }
}
