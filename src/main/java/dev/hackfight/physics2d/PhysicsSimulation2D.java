package dev.hackfight.physics2d;

public final class PhysicsSimulation2D {
    static PhysicsSimulation2D INSTANCE;

    private PhysicsSimulation2D() {}
    public static PhysicsSimulation2D getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PhysicsSimulation2D();
        }
        return INSTANCE;
    }

    public void update(float dt) {

    }
}
