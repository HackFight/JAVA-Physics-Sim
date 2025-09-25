package dev.hackfight.core;

import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.opengl.GL30C.*;

public class Model {

    private int vao_;
    private int vbo_;
    private int ebo_;
    private int vertexCount_;
    private int indexCount_;

    public static class Vertex {
        public Vector3f position;
        public Vector2f texCoord;
        public Vector3f color;

        public Vertex(Vector3f position, Vector2f texCoord, Vector3f color) {
            this.position = position;
            this.texCoord = texCoord;
            this.color = color;
        }
    }

    public Model(Vertex[] vertices, int[] indices) {
        this.indexCount_ = indices.length;
        createBuffer(vertices, indices);
    }

    public void bind() {
        glBindVertexArray(vao_);
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, indexCount_, GL_UNSIGNED_INT, 0);
    }

    private void createBuffer(Vertex[] vertices, int[] indices) {
        vertexCount_ = vertices.length;

        MemoryStack stack = MemoryStack.stackPush();
        FloatBuffer verticesBuffer = stack.mallocFloat(3 * 8);
        for (Vertex vertex : vertices)
        {
            verticesBuffer.put(vertex.position.x).put(vertex.position.y).put(vertex.position.z);
            verticesBuffer.put(vertex.texCoord.x).put(vertex.texCoord.y);
            verticesBuffer.put(vertex.color.x).put(vertex.color.y).put(vertex.color.z);
        }
        verticesBuffer.flip();

        vao_ = glGenVertexArrays();
        vbo_ = glGenBuffers();
        ebo_ = glGenBuffers();

        glBindVertexArray(vao_);

        glBindBuffer(GL_ARRAY_BUFFER, vbo_);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo_);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        int floatSize = 4;

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * floatSize, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8 * floatSize, 3 * floatSize);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 8 * floatSize, 5 * floatSize);
        glEnableVertexAttribArray(2);
    }
}
