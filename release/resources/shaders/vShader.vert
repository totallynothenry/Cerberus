#version 150 core

in vec2 position;
in vec3 inColor;
in vec2 inSTcoord;

out vec3 passColor;
out vec2 passSTcoord;

void main() {
    gl_Position = vec4(position, 0, 1.0);
    passColor = inColor;
    passSTcoord = inSTcoord;
}