#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 color;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec3 vertexColor;
out vec2 fragCoord;

void main() {
    mat4 pvm = projection * view * model;
    gl_Position = pvm * vec4(position, 1.0);
    vertexColor = color;
    fragCoord = position.xy;
}