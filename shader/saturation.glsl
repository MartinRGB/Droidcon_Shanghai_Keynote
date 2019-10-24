// Author:
// Title:

#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;
uniform float u_slot1;
uniform sampler2D u_tex1;
//Shader Toy Basic Uniform
#define iTime u_time
#define iResolution u_resolution
#define iMouse u_mouse
#define mTex u_tex1

vec3 saturation(vec3 rgb, float parameter)
{
    //Green Color
    const vec3 W = vec3(0.2125, 0.7154, 0.0721);
    //Get greyscale img
    vec3 intensity = vec3(dot(rgb, W));
    //Mix the greyscale img & original img
    return mix(intensity, rgb, parameter);
}

void main() {
    // Normalize the coordinate
    vec2 st = gl_FragCoord.xy/u_resolution.xy;
    // Sample the texture
    vec3 color = texture2D(mTex,st).xyz;
    color = saturation(color,1.-u_slot1);
    gl_FragColor = vec4(color,1.0);
}