package dev.hackfight.core;

import org.joml.*;

public class Camera {
    private static Camera INSTANCE;
    private Matrix4f projectionMat;
    private Vector3f position;

    public void setCameraSize(float left, float right, float bottom, float top, float zNear, float zFar) {
        projectionMat = new Matrix4f().ortho(left, right, bottom, top, zNear, zFar);
    }
    public Matrix4f getMat() {
        return projectionMat;
    }

    public Vector3f getPos() {
        return new Vector3f(position);
    }

    private Camera() {
        projectionMat = new Matrix4f().ortho(-5f, 5f, -5f, 5f, 0.01f, 10f);
    }

    public static Camera getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Camera();
        }
        return INSTANCE;
    }
}
