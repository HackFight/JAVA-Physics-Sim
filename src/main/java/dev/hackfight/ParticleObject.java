package dev.hackfight;

import dev.hackfight.core.*;
import dev.hackfight.physics2d.pointMass.Particle;
import org.joml.*;

public class ParticleObject {

    public Particle particle;

    private Model model;
    private Shader shader;

    public ParticleObject(Model model, Shader shader, Vector3f pos, float mass) {
        this.model = model;
        this.shader = shader;

        particle = new Particle(pos, mass);
    }
    public ParticleObject(Model model, Shader shader, Vector3f pos)  {
        this.model = model;
        this.shader = shader;

        particle = new Particle(pos);
    }
    public ParticleObject(Model model, Shader shader) {
        this.model = model;
        this.shader = shader;

        particle = new Particle();
    }

    public void render() {
        shader.bind();
        shader.setMat4("model", new Matrix4f().translate(particle.getPos()));
        shader.setMat4("view", Camera.getInstance().getView());
        shader.setMat4("projection", Camera.getInstance().getProj());

        model.bind();
        model.draw();
    }
}
