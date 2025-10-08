package dev.hackfight.physics2d;

import org.joml.Vector3f;

import java.util.ArrayList;

public class ParticlePhysicsWorld {

    private final float FLOOR_HEIGHT = 0f;

    private ArrayList<Particle> particles = new ArrayList<>();
    private final Vector3f GRAVITY = new Vector3f(0f, -9.81f, 0f);
    public Vector3f getGravity()
    {
        return new Vector3f(GRAVITY);
    }

    public void addParticle(Particle particle) {
        particles.add(particle);
    }
    public void removeParticle(Particle particle) {
        particles.remove(particle);
    }

    public void step(float dt) {
        for (Particle particle : particles) {
            particle.setVel(particle.getVel().add(getGravity().mul(dt)));
            Vector3f lastPos = particle.getPos();
            particle.setPos(particle.getPos().add(particle.getVel().mul(dt)));

            solve(particles, dt);

            particle.setVel(particle.getPos().sub(lastPos).mul(1f/dt));
        }
    }

    private void solve(ArrayList<Particle> particles, float dt) {
        for (Particle particle : particles) {
            Vector3f pos = particle.getPos();
            if(pos.y < -25f) {
                particle.setPos(pos.x, -25f, pos.z);
            }
        }
    }
}