package dev.hackfight.core;

import org.joml.*;

import java.lang.Math;

public class Camera {
    private static Camera INSTANCE;
    private Matrix4f viewMat = new Matrix4f();
    private Matrix4f projectionMat = new Matrix4f();
    private Vector3f position;
    private Vector3f forward = new Vector3f(0f, 0f, 1f);
    private Vector3f up = new Vector3f(0f, 1f, 0f);

    private void updateView() {
        viewMat = new Matrix4f().lookAt(getPos(), getPos().add(forward), up);
    }
    public Matrix4f getView() {
        return new Matrix4f(viewMat);
    }
    public void setProj(float angle, float width, float height) {
        projectionMat = new Matrix4f().perspective((float) (angle/180f * Math.PI), width/height, 0.01f, 100f);
    }
    public Matrix4f getProj() {
        return new Matrix4f(projectionMat);
    }

    public Vector3f getForward() {return new Vector3f(forward);}
    public void setForward(float x, float y, float z) {
        forward.x = x; forward.y = y; forward.z = z;
        updateView();
    }
    public void setForward(Vector3f forward) {
        setForward(forward.x, forward.y, forward.z);
    }

    public Vector3f getUp() {return new Vector3f(up);}
    public void setUp(float x, float y, float z) {
        up.x = x; up.y = y; up.z = z;
        updateView();
    }
    public void setUp(Vector3f up) {
        setPos(up.x, up.y, up.z);
    }

    public Vector3f getPos() {return new Vector3f(position);}
    public void setPos(float x, float y, float z) {
        position.x = x; position.y = y; position.z = z;
        updateView();
    }
    public void setPos(Vector3f pos) {
        setPos(pos.x, pos.y, pos.z);
    }

    private Camera(Vector3f pos) {
        position = new Vector3f(pos);
    }

    public static void create(Vector3f pos) {
        if(INSTANCE == null) {
            INSTANCE = new Camera(pos);
        }
    }
    public static Camera getInstance() {
        create(new Vector3f(0f));
        return INSTANCE;
    }
}
