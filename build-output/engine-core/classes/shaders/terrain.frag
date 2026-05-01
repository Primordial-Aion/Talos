#version 330 core
out vec4 fragColor;

in vec3 fragPos;
in vec3 normal_out;
in vec2 texCoords_out;
in vec2 blendCoords_out;

uniform vec3 lightDir;
uniform vec3 lightColor;
uniform vec3 ambientColor;
uniform sampler2D tex0;
uniform sampler2D tex1;
uniform sampler2D tex2;
uniform sampler2D blendMap;

void main() {
    vec3 normal = normalize(normal_out);
    vec3 lightDirection = normalize(lightDir);
    
    float diff = max(dot(normal, lightDirection), 0.0);
    vec3 diffuse = diff * lightColor;
    
    vec4 t0 = texture(tex0, texCoords_out * 10.0);
    vec4 t1 = texture(tex1, texCoords_out * 10.0);
    vec4 t2 = texture(tex2, texCoords_out * 10.0);
    vec4 blend = texture(blendMap, blendCoords_out);
    
    vec4 baseColor = t0 * blend.r + t1 * blend.g + t2 * blend.b;
    
    vec3 result = (ambientColor + diffuse);
    fragColor = vec4(result, 1.0) * baseColor;
}