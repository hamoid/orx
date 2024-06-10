#version 410 core

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in Vertex {
    vec3 va_position;
    vec3 va_normal;
    vec4 v_addedProperty;
} vertex[];

out vec3 va_position;
out vec3 va_normal;
out vec4 v_addedProperty;

uniform vec3 offset;

void main() {
    int i;
    for(i = 0; i < vertex.length(); i++) {
        gl_Position = vec4(vertex[i].va_position, 1.0); //vertex[i].va_normal * offset.x;
        va_normal = vertex[i].va_normal;
        EmitVertex();
    }
    EndPrimitive();
}