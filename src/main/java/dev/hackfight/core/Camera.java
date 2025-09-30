package dev.hackfight.core;

import org.joml.Matrix4f;

public class Camera {
    private static Camera INSTANCE;
    private Matrix4f projectionMat;
    public Matrix4f getMat() {
        return projectionMat;
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

    public void setCameraSize(float left, float right, float bottom, float top, float zNear, float zFar) {
        projectionMat = new Matrix4f().ortho(left, right, bottom, top, zNear, zFar);
    }
}
