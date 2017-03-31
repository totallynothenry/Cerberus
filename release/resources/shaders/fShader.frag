#version 150 core

uniform sampler2D sample;

in vec3 passColor;
in vec2 passSTcoord;

out vec4 fragColor;

void main() {
    fragColor = vec4(passColor, 1.0);
    fragColor = texture(sample, passSTcoord);
}