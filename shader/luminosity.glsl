// Author:
// Title:

#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;
uniform sampler2D u_tex2;
uniform vec2 u_tex0Resolution;
uniform float u_slot1;
varying vec2 v_texcoord;

//Shader Toy Basic Uniform
#define iTime u_time
#define iResolution u_resolution
#define iMouse u_mouse

vec3 rgb2hsb( in vec3 c ){
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), 
                 vec4(c.gb, K.xy), 
                 step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), 
                 vec4(c.r, p.yzx), 
                 step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), 
                d / (q.x + e), 
                q.x);
}

vec3 hsb2rgb( in vec3 c ){
    vec3 rgb = clamp(abs(mod(c.x*6.0+vec3(0.0,4.0,2.0),
                             6.0)-3.0)-1.0, 
                     0.0, 
                     1.0 );
    rgb = rgb*rgb*(3.0-2.0*rgb);
    return c.z * mix( vec3(1.0), rgb, c.y);
}

vec3 brightnessContrast(vec3 value, float brightness, float contrast)
{
    return (value - 0.5) * contrast + 0.5 + brightness;
}



void main() {
    
    vec2 st = vec2(v_texcoord.x,v_texcoord.y);
    vec3 color = texture2D(u_tex2,st).xyz;
    color = rgb2hsb(color);
    // Revert the luminosity,equal to brightness? color.z:1. - color.z
    color.z = color.z - (2.*color.z - 1.)*u_slot1;
    color = hsb2rgb(color);
    gl_FragColor = vec4(color,1.0);
    
}