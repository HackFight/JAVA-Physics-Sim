package dev.hackfight.physics2d.pointMass;

import java.util.ArrayList;

public abstract class Constraint {
    public ArrayList<Particle> particles;

    public Constraint(ArrayList<Particle> particles) {
        this.particles = particles;
    }

    public abstract void solve(float dt);
}

