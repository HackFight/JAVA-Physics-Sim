package dev.hackfight.core;

import dev.hackfight.physics2d.*;
import org.joml.*;

import java.util.ArrayList;

public class SimObject {
    private PointBody2D body_;
    private Model model_;
    private Shader shader_;
    private ArrayList<Vector2f> forces_;

    public float mass = 1f;

    public SimObject(Model model, Shader shader, Vector2f pos) {
        body_ = new PointBody2D(pos);
        model_ = model;
        shader_ = shader;
        forces_ = new ArrayList<Vector2f>();
    }
    public SimObject(Model model, Shader shader) {
        this(model, shader, new Vector2f(0f, 0f));
    }

    public Vector2f getPos() {
        return body_.getPosition();
    }

    public void addForce(Vector2f force) {
        forces_.add(force);
    }

    public void update(float dt) {
        Vector2f totalForces = new Vector2f(0f, 0f);
        for (Vector2f force : forces_) {
            totalForces = totalForces.add(force);
        }

        body_.accelerate(totalForces.div(mass));
        body_.update(dt);
    }

    public void render() {
        Matrix4f model = new Matrix4f().translate(body_.getPosition().x, body_.getPosition().y, 0f);;
        Matrix4f view = new Matrix4f().translate(0f, 0f, -1f);
        Matrix4f projection = new Matrix4f().ortho(-25f, 25f, -25f, 25f, 0.01f, 10f);

        shader_.bind();
        shader_.setMat4("model", model);
        shader_.setMat4("view", view);
        shader_.setMat4("projection", projection);

        model_.bind();
        model_.draw();
    }
}
