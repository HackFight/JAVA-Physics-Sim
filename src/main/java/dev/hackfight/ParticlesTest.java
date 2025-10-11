package dev.hackfight;

import dev.hackfight.core.*;
import dev.hackfight.physics2d.pointMass.*;
import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.IOException;
import java.lang.Math;
import java.nio.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Timer;

import static java.lang.Math.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class ParticlesTest {

    // The window handle
    private long window;

    // Mouse stuff
    private boolean cursorLocked = true;
    private float cursorPosX = 400, cursorPosY = 400;
    private float lastX = 400, lastY = 400;
    float xoffset, yoffset;
    private float sensitivity = 0.1f;
    private boolean firstMouse = true;

    //Camera stuff
    float yaw = -90f, pitch = 0f;

    private Timer timer;

    private Shader defaultShader;
    private Shader billboardShader;
    private Model triangle;

    private ParticlePhysicsWorld physWorld;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        try {
            loadAssets();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(800, 800, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a frame buffer resize callback
        glfwSetFramebufferSizeCallback(window, this::framebuffer_size_callback);

        // Setup a key callback
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                if (cursorLocked) {
                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                    cursorLocked = false;
                } else {
                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                    cursorLocked = true;
                }
        });

        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {

            if (firstMouse) // initially set to true
            {
                lastX = (float) xpos;
                lastY = (float) ypos;
                firstMouse = false;
            }

            xoffset = (float) (xpos - lastX);
            yoffset = (float) (lastY - ypos); // reversed since y-coordinates range from bottom to top
            lastX = (float) xpos;
            lastY = (float) ypos;

            xoffset *= sensitivity;
            yoffset *= sensitivity;

            yaw   += xoffset;
            pitch += yoffset;

            //Constraint input
            if(pitch > 89.0f)
                pitch =  89.0f;
            if(pitch < -89.0f)
                pitch = -89.0f;

            Vector3f direction = new Vector3f();
            direction.x = (float) (cos(Math.toRadians(yaw)) * cos(Math.toRadians(pitch)));
            direction.y = (float) sin(Math.toRadians(pitch));
            direction.z = (float) (sin(Math.toRadians(yaw)) * cos(Math.toRadians(pitch)));
            Camera.getInstance().setForward(direction.normalize());
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            if (OsValidator.isWindows()) {
                // Center the window
                glfwSetWindowPos(
                        window,
                        (vidmode.width() - pWidth.get(0)) / 2,
                        (vidmode.height() - pHeight.get(0)) / 2
                );
            }
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        GL.createCapabilities();
        Callback debugProc = GLUtil.setupDebugMessageCallback();

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        //Create capabilities, important for some java stuff
        GL.createCapabilities();
        //Set buffer clear color cus why not
        glClearColor(0.270588235f, 0.2823529411764706f, 0.4235294117647059f, 0.0f); //#45486C
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetInputMode(window, GLFW_STICKY_MOUSE_BUTTONS, GLFW_TRUE);

        // Instantiate the physic world
        physWorld = new ParticlePhysicsWorld();

        // Create lattice
        ArrayList<ParticleObject> objects = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                ParticleObject object = new ParticleObject(triangle, billboardShader, new Vector3f(j*3f, -i*3f + 30f, 0f));
                objects.add(object);
                physWorld.addParticle(object.particle);

                if(i==0) {
                    object.particle.setStatic();
                }
            }
        }
        objects.getLast().particle.setVel(100f, 0f, 10f);
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 9; i++) {
                ArrayList<Particle> hpair = new ArrayList<>();
                hpair.add(objects.get(j*10 + i).particle);
                hpair.add(objects.get(j*10 + i + 1).particle);
                physWorld.addConstraint(new DistanceConstraint(hpair, 3f));
            }
        }
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 10; i++) {
                ArrayList<Particle> vpair = new ArrayList<>();
                vpair.add(objects.get(j * 10 + i).particle);
                vpair.add(objects.get(j * 10 + i + 10).particle);
                physWorld.addConstraint(new DistanceConstraint(vpair, 3f));
            }
        }

        // Create pyramid
        {
            ParticleObject object = new ParticleObject(triangle, billboardShader, new Vector3f(5f, 0f, 5f));
            objects.add(object);
            physWorld.addParticle(object.particle);
            object = new ParticleObject(triangle, billboardShader, new Vector3f(8f, 0f, 5f));
            objects.add(object);
            physWorld.addParticle(object.particle);
            object = new ParticleObject(triangle, billboardShader, new Vector3f(5f, 0f, 8f));
            objects.add(object);
            physWorld.addParticle(object.particle);
            object = new ParticleObject(triangle, billboardShader, new Vector3f(8f, 0f, 8f));
            objects.add(object);
            physWorld.addParticle(object.particle);
            object = new ParticleObject(triangle, billboardShader, new Vector3f(6.5f, 3f, 6.5f));
            objects.add(object);
            physWorld.addParticle(object.particle);

            ArrayList<Particle> pair0 = new ArrayList<>();
            pair0.add(objects.getLast().particle);
            pair0.add(objects.get(objects.size()-2).particle);

            ArrayList<Particle> pair1 = new ArrayList<>();
            pair1.add(objects.getLast().particle);
            pair1.add(objects.get(objects.size()-3).particle);

            ArrayList<Particle> pair2 = new ArrayList<>();
            pair2.add(objects.getLast().particle);
            pair2.add(objects.get(objects.size()-4).particle);

            ArrayList<Particle> pair3 = new ArrayList<>();
            pair3.add(objects.getLast().particle);
            pair3.add(objects.get(objects.size()-5).particle);

            ArrayList<Particle> pair4 = new ArrayList<>();
            pair4.add(objects.get(objects.size()-2).particle);
            pair4.add(objects.get(objects.size()-3).particle);

            ArrayList<Particle> pair5 = new ArrayList<>();
            pair5.add(objects.get(objects.size()-3).particle);
            pair5.add(objects.get(objects.size()-4).particle);

            ArrayList<Particle> pair6 = new ArrayList<>();
            pair6.add(objects.get(objects.size()-4).particle);
            pair6.add(objects.get(objects.size()-5).particle);

            ArrayList<Particle> pair7 = new ArrayList<>();
            pair7.add(objects.get(objects.size()-2).particle);
            pair7.add(objects.get(objects.size()-4).particle);

            ArrayList<Particle> pair8 = new ArrayList<>();
            pair8.add(objects.get(objects.size()-3).particle);
            pair8.add(objects.get(objects.size()-5).particle);

            physWorld.addConstraint(new DistanceConstraint(pair0, pair0.get(0).getPos().sub(pair0.get(1).getPos()).length()));
            physWorld.addConstraint(new DistanceConstraint(pair1, pair1.get(0).getPos().sub(pair1.get(1).getPos()).length()));
            physWorld.addConstraint(new DistanceConstraint(pair2, pair2.get(0).getPos().sub(pair2.get(1).getPos()).length()));
            physWorld.addConstraint(new DistanceConstraint(pair3, pair3.get(0).getPos().sub(pair3.get(1).getPos()).length()));
            physWorld.addConstraint(new DistanceConstraint(pair4, pair4.get(0).getPos().sub(pair4.get(1).getPos()).length()));
            physWorld.addConstraint(new DistanceConstraint(pair5, pair5.get(0).getPos().sub(pair5.get(1).getPos()).length()));
            physWorld.addConstraint(new DistanceConstraint(pair6, pair6.get(0).getPos().sub(pair6.get(1).getPos()).length()));
            physWorld.addConstraint(new DistanceConstraint(pair7, pair7.get(0).getPos().sub(pair7.get(1).getPos()).length()));
            physWorld.addConstraint(new DistanceConstraint(pair8, pair8.get(0).getPos().sub(pair8.get(1).getPos()).length()));
        }

        //Floor constraint
        ArrayList<Particle> all = new ArrayList<>();
        for(ParticleObject object : objects)
        {
            all.add(object.particle);
        }
        physWorld.addConstraint(new FloorConstraint(all, 0f));

        //Only these model and shader  will be used so we can bind them here instead of each frame.
        billboardShader.bind();
        triangle.bind();

        Camera.getInstance().setPos(0f, 0f, 50f);
        Camera.getInstance().setProj(90f, 1f, 1f);

        //Variables for delta time
        double lastLoopTime = glfwGetTime();
        double delta = 0.0;
        double timeAccumulator = 0.0;
        double fpsAccu = 0.0;
        float fixedStepTime = 1f/1000f;
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            processInput();

            // Step Physics World
            timeAccumulator += delta;
            while(timeAccumulator >= fixedStepTime) {
                physWorld.step(fixedStepTime); // Step the simulation
                timeAccumulator -= fixedStepTime;
            }

            //Render particles
            //These are the same for each particle
            billboardShader.setMat4("view", Camera.getInstance().getView());
            billboardShader.setMat4("projection", Camera.getInstance().getProj());
            //This though needs to be set per particle
            for (ParticleObject object : objects) {
                Matrix4f modelMat = new Matrix4f().translate(object.particle.getPos());

                billboardShader.setMat4("model", modelMat);

                triangle.draw();
            }

            glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents();

            // Calculate delta time
            double time = glfwGetTime();
            delta = time - lastLoopTime;
            lastLoopTime = time;

            //Print FPS every second
            fpsAccu += delta;
            if(fpsAccu >= 1.0) {
                System.out.println(1f/delta);
                fpsAccu = 0.0;
            }
        }
    }

    private void processInput() {
        //Keyboard Inputs
        int state;
        state = glfwGetKey(window, GLFW_KEY_W);
        if (state == GLFW_PRESS) {
            Camera.getInstance().setPos(Camera.getInstance().getPos().add(Camera.getInstance().getForward().mul(1f)));
        }
        state = glfwGetKey(window, GLFW_KEY_S);
        if (state == GLFW_PRESS) {
            Camera.getInstance().setPos(Camera.getInstance().getPos().add(Camera.getInstance().getForward().mul(-1f)));
        }
        state = glfwGetKey(window, GLFW_KEY_A);
        if (state == GLFW_PRESS) {
            Camera.getInstance().setPos(Camera.getInstance().getPos().add(Camera.getInstance().getUp().cross(Camera.getInstance().getForward()).normalize().mul(1f)));
        }
        state = glfwGetKey(window, GLFW_KEY_D);
        if (state == GLFW_PRESS) {
            Camera.getInstance().setPos(Camera.getInstance().getPos().add(Camera.getInstance().getUp().cross(Camera.getInstance().getForward()).normalize().mul(-1f)));
        }
        state = glfwGetKey(window, GLFW_KEY_SPACE);
        if (state == GLFW_PRESS) {
            Camera.getInstance().setPos(Camera.getInstance().getPos().add(Camera.getInstance().getUp().mul(1f)));
        }
        state = glfwGetKey(window, GLFW_KEY_LEFT_SHIFT);
        if (state == GLFW_PRESS) {
            Camera.getInstance().setPos(Camera.getInstance().getPos().add(Camera.getInstance().getUp().mul(-1f)));
        }

        //Mouse inputs are in a callback higher in the code
    }

    private void loadAssets() throws IOException {
        // shaders
        defaultShader = new Shader(Path.of("src/main/resources/shaders/default.vert"), Path.of("src/main/resources/shaders/default.frag"));
        billboardShader = new Shader(Path.of("src/main/resources/shaders/billboard.vert"), Path.of("src/main/resources/shaders/circle.frag"));

        // models
        Model.Vertex[] vertices = {
                new Model.Vertex(new Vector3f(-1.73205f, -1f, 0f), new Vector2f(0f, 0f), new Vector3f(1f, 0f, 0f)),
                new Model.Vertex(new Vector3f(1.73205f, -1f, 0f), new Vector2f(0.5f, 1f), new Vector3f(0f, 1f, 0f)),
                new Model.Vertex(new Vector3f(0f, 2f, 0f), new Vector2f(1f, 0f), new Vector3f(0f, 0f, 1f))
        };

        int[] indices = {
                0, 1, 2
        };

        triangle = new Model(vertices, indices);
    }

    private void framebuffer_size_callback(long window, int width, int height)
    {
        // make sure the viewport matches the new window dimensions; note that width and
        // height will be significantly larger than specified on retina displays.
        glViewport(0, 0, width, height);
        float ratio = (float) width/ (float) height;

        Camera.getInstance().setProj(90f, width, height);
    }

    public static void main(String[] args) {
        try {
            new ParticlesTest().run();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

}