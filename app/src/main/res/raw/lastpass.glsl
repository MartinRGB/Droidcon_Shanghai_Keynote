#ifdef GL_FRAGMENT_PRECISION_HIGH
precision highp float;
#endif

uniform vec2 u_resolution;
uniform sampler2D frame;
void main(void) {
    gl_FragColor = texture2D(frame,
    gl_FragCoord.xy / u_resolution.xy).rgba;
}