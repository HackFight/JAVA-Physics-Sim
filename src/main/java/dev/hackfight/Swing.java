package dev.hackfight;

import dev.hackfight.core.*;
import dev.hackfight.physics2d.pointMass.*;
import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.IOException;
import java.nio.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Timer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Swing {

    // The window handle
    private long window;

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
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a frame buffer resize callback
        glfwSetFramebufferSizeCallback(window, this::framebuffer_size_callback);

        // Setup a key callback
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
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

        // Instantiate the physic world
        physWorld = new ParticlePhysicsWorld();

        // Create particles
        ArrayList<ParticleObject> objects = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ParticleObject object = new ParticleObject(triangle, billboardShader, new Vector3f(i*3f, 45f, i*3f));
            objects.add(object);
            physWorld.addParticle(object.particle);
        }
        objects.getFirst().particle.setStatic();

        // Create constraints
        for (int i = 0; i < objects.size() - 1; i++) {
            ArrayList<Particle> pair = new ArrayList<>();
            pair.add(objects.get(i).particle);
            pair.add(objects.get(i+1).particle);
            physWorld.addConstraint(new DistanceConstraint(pair, 3f));
        }

        ArrayList<Particle> all = new ArrayList<>();
        for(ParticleObject object : objects) {
            all.add(object.particle);
        }
        physWorld.addConstraint(new FloorConstraint(all, 0f));

        //Only these model and shader  will be used so we can bind them here instead of each frame.
        billboardShader.bind();
        triangle.bind();

        Camera.create(new Vector3f(0f, 25f, -10f));
        Camera.getInstance().setProj(45f, 1f, 1f);

        //Variables for delta time
        double lastLoopTime = glfwGetTime();
        double delta = 0.0;
        double timeAccumulator = 0.0;
        float fixedStepTime = 1f/1000f;
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

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
        }
    }

    private void loadAssets() throws IOException {
        // shaders
        defaultShader = new Shader(Path.of("src/main/resources/shaders/default.vert"), Path.of("src/main/resources/shaders/circle.frag"));
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

        //Camera.getInstance().setProj(45f, width, height);
    }

    public static void main(String[] args) {
        try {
            new Swing().run();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

}