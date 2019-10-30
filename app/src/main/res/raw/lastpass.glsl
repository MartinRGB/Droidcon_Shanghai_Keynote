#ifdef GL_FRAGMENT_PRECISION_HIGH
precision highp float;
#endif

uniform vec2 u_resolution;
uniform sampler2D u_frame;

void main(void) {
    vec2 uv = gl_FragCoord.xy/u_resolution;

//    if(uv.y>0.5){
//        if(uv.x > 0.5){
//            gl_FragColor = texture2D(u_frame, gl_FragCoord.xy / u_resolution.xy).rgba;
//        }
//    }
//    else{
//        if(uv.x<0.5){
//            gl_FragColor = texture2D(u_backbuffer, gl_FragCoord.xy / u_resolution.xy).rgba;
//        }
//    }

    gl_FragColor = texture2D(u_frame, gl_FragCoord.xy / u_resolution.xy).rgba;

}