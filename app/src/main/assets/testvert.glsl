attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;
varying vec2 texCoord;

void main() {
    texCoord = a_TextureCoordinates;
    gl_Position= vec4 ( a_Position.x, a_Position.y, 1.0, 1.0 );
}
