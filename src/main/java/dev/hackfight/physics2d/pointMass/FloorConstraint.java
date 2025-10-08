package dev.hackfight.physics2d.pointMass;

import org.joml.Vector3f;

import java.util.ArrayList;

public class FloorConstraint extends Constraint {

    private final float height;
    public FloorConstraint(ArrayList<Particle> particles, float h) {
        super(particles);
        height = h;
    }

    @Override
    public void solve() {
        for (Particle particle : particles) {
            Vector3f pos = particle.getPos();
            if(pos.y < height) {
                particle.setPos(pos.x, height, pos.z);
            }
        }
    }
}
