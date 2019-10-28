attribute vec4 a_position;
attribute vec2 a_textureCoordinates;
varying vec2 v_texcoord;

void main() {
    v_texcoord = a_textureCoordinates;
    gl_Position= vec4 ( a_position.x, a_position.y, 1.0, 1.0 );
}
