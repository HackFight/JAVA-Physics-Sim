package dev.hackfight.physics2d.pointMass;

import org.joml.Vector3f;

import java.util.ArrayList;

public class DistanceConstraint extends Constraint {
    private final float restDistance;

    public DistanceConstraint(ArrayList<Particle> particles, float d) {
        super(particles);
        restDistance = d;
        if(particles.size() > 2) System.out.println("More than 2 particles provided! Extra particles in Distance Constraint will be ignored!");
    }


    @Override
    public void solve() {

        Vector3f dif = particles.get(1).getPos().sub(particles.get(0).getPos()).normalize(particles.get(1).getPos().sub(particles.get(0).getPos()));
        float d = particles.get(1).getPos().sub(particles.get(0).getPos()).length()-restDistance;
        float W = particles.get(0).w()+particles.get(1).w();
        Vector3f dx0 = new Vector3f(dif).mul(d).mul(particles.get(0).w()/W);
        Vector3f dx1 = new Vector3f(dif).mul(d).mul(-particles.get(1).w()/W);

        particles.get(0).setPos(particles.get(0).getPos().add(dx0));
        particles.get(1).setPos(particles.get(1).getPos().add(dx1));
    }
}
