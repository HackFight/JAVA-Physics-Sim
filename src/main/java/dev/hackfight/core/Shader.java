package dev.hackfight.core;

import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30C.*;

public class Shader {
    public int ID;

    public Shader(Path vertexPath, Path fragmentPath) throws IOException {

        // retrieve source code
        String vertexSrc = Files.readString(vertexPath, StandardCharsets.UTF_8);
        String fragSrc = Files.readString(fragmentPath, StandardCharsets.UTF_8);

        // compile shaders
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexSrc);
        glCompileShader(vertexShader);
        // check
        int status = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(vertexShader));
        }

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragSrc);
        glCompileShader(fragmentShader);
        //check
        status = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(fragmentShader));
        }

        // shader program
        ID = glCreateProgram();
        glAttachShader(ID, vertexShader);
        glAttachShader(ID, fragmentShader);
        glLinkProgram(ID);
        // check
        status = glGetProgrami(ID, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(ID));
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void bind() {
        glUseProgram(ID);
    }

    public void set2f(String name, float v1, float v2) {
        glUniform2f(glGetUniformLocation(ID, name), v1, v2);
    }
    public void set3f(String name, float v1, float v2, float v3) {
        glUniform3f(glGetUniformLocation(ID, name), v1, v2, v3);
    }

    public void setMat4(String name, Matrix4f mat4) {
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matrixBuffer);

        glUniformMatrix4fv(glGetUniformLocation(ID, name), false, matrixBuffer);
    }
}
