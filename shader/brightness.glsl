// Author:
// Title:

#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;
uniform float u_slot1;
uniform sampler2D u_tex0;
//Shader Toy Basic Uniform
#define iTime u_time
#define iResolution u_resolution
#define iMouse u_mouse
#define mTex u_tex0

vec3 brightness(vec3 value, float brightness)
{
    return value + brightness;
}

void main() {
    // Normalize the coordinate
    vec2 st = gl_FragCoord.xy/u_resolution.xy;
    // Sample the texture
    vec3 color = texture2D(mTex,st).xyz;
    color = brightness(color,-u_slot1);
    gl_FragColor = vec4(color,1.0);
}