package dev.hackfight;

import dev.hackfight.core.Camera;
import dev.hackfight.core.Model;
import dev.hackfight.core.Shader;
import dev.hackfight.physics2d.Particle;
import org.joml.Matrix4f;

public class ParticleObject {

    public Particle particle;

    private Model model;
    private Shader shader;

    public ParticleObject(Model model, Shader shader) {
        this.model = model;
        this.shader = shader;

        particle = new Particle();
    }

    public void render() {
        Matrix4f modelMat = new Matrix4f().translate(particle.getPos());
        Matrix4f viewMat = new Matrix4f().translate(Camera.getInstance().getPos());

        shader.bind();
        shader.setMat4("model", modelMat);
        shader.setMat4("view", viewMat);
        shader.setMat4("projection", Camera.getInstance().getMat());

        model.bind();
        model.draw();
    }
}
