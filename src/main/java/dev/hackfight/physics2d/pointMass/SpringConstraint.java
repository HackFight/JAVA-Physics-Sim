package dev.hackfight.physics2d.pointMass;

import org.joml.Vector3f;

import java.util.ArrayList;

public class SpringConstraint extends Constraint {
    private final float restDistance, stiffness;

    public SpringConstraint(ArrayList<Particle> particles, float d, float k) {
        super(particles);
        restDistance = d;
        stiffness = k;
        if(particles.size() > 2) System.out.println("More than 2 particles provided! Extra particles in Distance Constraint will be ignored!");
    }


    @Override
    public void solve(float dt) {

        Vector3f dif = particles.get(1).getPos().sub(particles.get(0).getPos()).normalize(particles.get(1).getPos().sub(particles.get(0).getPos()));
        float d = particles.get(1).getPos().sub(particles.get(0).getPos()).length()-restDistance;
        float W = particles.get(0).w()+particles.get(1).w();

        Vector3f dx0 = new Vector3f(dif).mul(d).mul(particles.get(0).w()/W);
        Vector3f dx1 = new Vector3f(dif).mul(d).mul(-particles.get(1).w()/W);

        Vector3f a0 = dx0.mul(-stiffness).mul(particles.get(0).w());
        Vector3f a1 = dx1.mul(-stiffness).mul(particles.get(1).w());

        particles.get(0).setVel(particles.get(0).getVel().add(a0.mul(dt)));
        particles.get(1).setVel(particles.get(1).getVel().add(a1.mul(dt)));

        particles.get(0).setPos(particles.get(0).getPos().add(particles.get(0).getVel().mul(dt)));
        particles.get(1).setPos(particles.get(1).getPos().add(particles.get(1).getVel().mul(dt)));

        // particle.setVel(particle.getVel().add(getGravity().mul(dt)));
        // particle.setPos(particle.getPos().add(particle.getVel().mul(dt)));
    }
}
