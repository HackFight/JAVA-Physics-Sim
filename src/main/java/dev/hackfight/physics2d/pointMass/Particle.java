package dev.hackfight.physics2d.pointMass;

import org.joml.*;

import java.lang.Math;

public class Particle {
    private Vector3f position = new Vector3f();
    private Vector3f velocity = new Vector3f();
    private Vector3f force = new Vector3f();
    private float w = 1f;

    //Setters & getters
    public void setPos(float x, float y, float z) {
        position.x = x; position.y = y; position.z = z;
    }
    public void setPos(Vector3f pos) {
        setPos(pos.x, pos.y, pos.z);
    }
    public Vector3f getPos() {
        return new Vector3f(position);
    }

    public void setVel(float x, float y, float z) {
        velocity.x = x; velocity.y = y; velocity.z = z;
    }
    public void setVel(Vector3f vel) {
        setVel(vel.x, vel.y, vel.z);
    }
    public Vector3f getVel() {
        return new Vector3f(velocity);
    }

    public void setForce(float x, float y, float z) {
        force.x = x; force.y = y; force.z = z;
    }
    public void setForce(Vector3f force) {
        setForce(force.x, force.y, force.z);
    }
    public void addForce(float x, float y, float z) {
        force.add(x, y, z);
    }
    public void addForce(Vector3f force) {
        this.force.add(force);
    }

    public void setMass(float mass) {
        if(mass <= 0f) {
            w = 1f;
        } else {
            w = 1f / mass;
        }
    }
    public void setW(float invMass) {
        w = Math.clamp(invMass, 0f, 1f);
    }
    public float w() {
        return w;
    }

    public Particle(Vector3f pos, Vector3f vel, Vector3f force, float mass) {
        setPos(pos);
        setVel(vel);
        setForce(force);
        setMass(mass);
    }
    public Particle(Vector3f pos, float mass) {
        setPos(pos);
        setMass(mass);
    }
    public Particle(Vector3f pos) {
        setPos(pos);
    }
    public Particle() {}
}