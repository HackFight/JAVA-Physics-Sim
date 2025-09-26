package dev.hackfight.physics2d;

import org.joml.*;

public class PointBody2D {
    private Vector2f position_;
    private Vector2f lastPosition_;
    private Vector2f acceleration_;

    public PointBody2D(Vector2f pos) {
        position_ = new Vector2f(pos);
        lastPosition_ = new Vector2f(pos);
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
            Vector2f vel = new Vector2f(getVelocity());
            position_ = new Vector2f(pos);
            setVelocity(vel);
        } else {
            position_ = new Vector2f(pos);
            lastPosition_ = new Vector2f(pos);
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
        // solve verlet integration
        Vector2f tempPos = new Vector2f(position_);
        position_ = position_.mul(2).sub(lastPosition_).add(acceleration_.mul(dt * dt));
        lastPosition_ = tempPos;
        acceleration_ = acceleration_.mul(0);

        // solve constraints
        if (position_.y < -25f) {
            Vector2f tempVel = new Vector2f(getVelocity().x, -getVelocity().y);
            position_.y = -25f;
            setVelocity(tempVel);
        }
    }
}