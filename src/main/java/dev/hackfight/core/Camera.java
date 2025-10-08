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

    public void setPos(float x, float y, float z) {
        position.x = x; position.y = y; position.z = z;
    }
    public Vector3f getPos() {
        return new Vector3f(position);
    }

    private Camera(Vector3f pos) {
        projectionMat = new Matrix4f().ortho(-5f, 5f, -5f, 5f, 0.01f, 10f);
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
