#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform float u_time;
uniform float u_brightness;
uniform sampler2D u_tex0;

#define uTexture u_texture

void main(){
    vec2 st = gl_FragCoord.xy/u_resolution;
    vec3 color = vec3(0.0);

    // We map x (0.0 - 1.0) to the hue (0.0 - 1.0)
    // And the y (0.0 - 1.0) to the brightness
    //color = vec3(0.,1.,0.);
    color = texture2D(u_tex0,vec2(st.x*108./234.,st.y)/1.25).xyz;

    gl_FragColor = vec4(vec3(u_brightness),1.0);
}
