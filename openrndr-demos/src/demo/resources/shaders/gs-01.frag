#version 410 core

in vec3 va_position;
in vec3 va_normal;
in vec4 v_addedProperty;

out vec4 o_color;

void main() {
    o_color = vec4(va_normal, 1.0);
}