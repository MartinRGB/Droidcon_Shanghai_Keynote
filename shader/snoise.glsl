// Author:
// Title:

#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;
uniform sampler2D u_tex4;
uniform vec2 u_tex0Resolution;
uniform float u_slot1;
uniform float u_slot2;
//Shader Toy Basic Uniform
#define iTime u_time
#define iResolution u_resolution
#define iMouse u_mouse

float lineJitter = 0.5;
float lineWidth = 1.5;
float gridWidth = 1.2;
float scale = 0.0013;
float zoom = 4.;
vec2 offset = vec2(0.5);


vec3 mod289(vec3 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
  return mod289(((x*34.0)+1.0)*x);
}

float snoise(vec2 v)
  {
  const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
                      0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
                     -0.577350269189626,  // -1.0 + 2.0 * C.x
                      0.024390243902439); // 1.0 / 41.0
// First corner
  vec2 i  = floor(v + dot(v, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);

// Other corners
  vec2 i1;
  //i1.x = step( x0.y, x0.x ); // x0.x > x0.y ? 1.0 : 0.0
  //i1.y = 1.0 - i1.x;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  // x0 = x0 - 0.0 + 0.0 * C.xx ;
  // x1 = x0 - i1 + 1.0 * C.xx ;
  // x2 = x0 - 1.0 + 2.0 * C.xx ;
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;

// Permutations
  i = mod289(i); // Avoid truncation effects in permutation
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
        + i.x + vec3(0.0, i1.x, 1.0 ));

  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;

// Gradients: 41 points uniformly over a line, mapped onto a diamond.
// The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)

  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;

// Normalise gradients implicitly by scaling m
// Approximation of: m *= inversesqrt( a0*a0 + h*h );
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

// Compute final noise value at P
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}

float rand (in float _x) {
    return fract(sin(_x)*1e4);
}

float rand (in vec2 co) {
    return fract(sin(dot(co.xy,vec2(12.9898,78.233)))*43758.5453);
}

float noise (in float _x) {
    float i = floor(_x);
    float f = fract(_x);
    float u = f * f * (3.0 - 2.0 * f);
    return mix(rand(i), rand(i + 1.0), u);
}


float noise (in vec2 _st) {
    vec2 i = floor(_st);
    vec2 f = fract(_st);
    // Four corners in 2D of a tile
    float a = rand(i);
    float b = rand(i + vec2(1.0, 0.0));
    float c = rand(i + vec2(0.0, 1.0));
    float d = rand(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + 
            (c - a)* u.y * (1.0 - u.x) + 
            (d - b) * u.x * u.y;
}

float noiseFactor = 0.1;

vec2 sNoiseFunction(){
  // # normalized coordinate influenced by simplex noise;
    vec2 uv = gl_FragCoord.xy/u_resolution.xy;
    vec2 st = gl_FragCoord.xy/u_resolution.xy;

    float s = snoise(vec2(st.x,st.y*1.5+u_time/4.));
    //float s = snoise(vec2(st.x,st.y));
    st *= vec2( 1.0 + s * (noiseFactor*u_slot1) ); //// multiply the uv coord for 1 + the noise

    //uv.y *= sin(uv.x*10.)*0.000 + 1
    return vec2(st.x,st.y);
}

float function(in float x) {
 
    //float y = x;
    float y = sNoiseFunction().y;

    return y;
}

vec3 plot2D(in vec2 _st, in float _width,float givenFunc) {
    const float samples = 12.0;

    vec2 steping = _width*vec2(scale)/samples;

    float count = 0.0;
    float mySamples = 0.0;
    for (float i = 0.0; i < samples; i++) {
        for (float j = 0.0;j < samples; j++) {
            if (i*i+j*j>samples*samples) 
                continue;
            mySamples++;
            float ii = i + lineJitter*rand(vec2(_st.x+ i*steping.x,_st.y+ j*steping.y));
            float jj = j + lineJitter*rand(vec2(_st.y + i*steping.x,_st.x+ j*steping.y));
            //float f = function(_st.x+ ii*steping.x)-(_st.y+ jj*steping.y);
            float f = givenFunc -(_st.y+ jj*steping.y);
            count += (f>0.) ? 1.0 : -1.0;
        }
    }
    vec3 color = vec3(1.);
    if (abs(count)!=mySamples)
        color = vec3(abs(float(count))/float(mySamples));
    return color;
}

vec3 grid2D( in vec2 _st, in float _width ) {
    float axisDetail = _width*scale;
    if (abs(_st.x)<axisDetail || abs(_st.y)<axisDetail) 
        return 1.0-vec3(0.65,0.65,1.0);
    if (abs(mod(_st.x,1.0))<axisDetail || abs(mod(_st.y,1.0))<axisDetail) 
        return 1.0-vec3(0.80,0.80,1.0);
    if (abs(mod(_st.x,0.25))<axisDetail || abs(mod(_st.y,0.25))<axisDetail) 
        return 1.0-vec3(0.95,0.95,1.0);
    return vec3(0.0);
}

float plot(vec2 st, float pct){
  return  smoothstep( pct-0.009, pct, st.y) -
          smoothstep( pct, pct+0.009, st.y);
}

vec2 rotateUV(vec2 uv, vec2 pivot, float rotation) {
    float cosa = cos(rotation);
    float sina = sin(rotation);
    uv -= pivot;
    return vec2(
        cosa * uv.x - sina * uv.y,
        cosa * uv.y + sina * uv.x 
    ) + pivot;
}


void main() {
    // #static normalized coordinate;
    vec2 uv = gl_FragCoord.xy/u_resolution.xy;
    
  
    vec2 st = sNoiseFunction();
    
    
    vec3 color = vec3(0.);

    if(uv.y>0.5){
        // Use texel to sample texture;
        color = texture2D(u_tex4,vec2(st.x,st.y*2.)).xyz;
    }
    else{
        vec2 graphUV = (gl_FragCoord.xy/u_resolution.xy)-offset;
        graphUV.x *= u_resolution.x/u_resolution.y;
        
        scale *= zoom;
        graphUV *= zoom;
        // color = plot2D(vec2(graphUV.x,graphUV.y+1.5),lineWidth,st.y*2.);
        
        vec2 rotatedUV = rotateUV(vec2(graphUV.x,graphUV.y+1.78),vec2(0.5),0.528);
        color = plot2D(vec2(graphUV.x,graphUV.y+1.5),lineWidth,st.y*2.);
        color *= plot2D(rotatedUV,lineWidth+1.9,st.x);
    	color -= grid2D(graphUV,gridWidth);
    }
    
    //color = vec3(1.0,1.,1.)*s;
    
    gl_FragColor = vec4(color ,1.0);
}