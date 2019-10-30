#ifdef GL_ES
precision highp float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;
uniform sampler2D u_tex0;
uniform sampler2D u_tex1;
uniform sampler2D u_tex2;
uniform sampler2D u_backbuffer;
varying vec2 v_texcoord;

#define M_PI (3.14159265358979)
#define GRAVITY (9.80665)
#define EPS (1e-3)
#define RAYMARCH_CLOUD_ITER (8)
#define WAVENUM (16)

const float kSensorWidth = 36e-3;
const float kFocalLength = 18e-3;

const vec2 kWind = vec2(0.0, 1.0);
const float kCloudHeight = 100.0;
const float kOceanScale = 10.0;

const float kCameraSpeed = 0.0;
const float kCameraHeight = 1.0;
const float kCameraShakeAmp = 0.001;
const float kCameraRollNoiseAmp = 0.1;


struct Ray
{
	vec3 o;
    vec3 dir;
};

struct HitInfo
{
	vec3 pos;
    vec3 normal;
    float dist;
    Ray ray;
};

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

vec3 saturation(vec3 rgb, float parameter)
{
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
	vec3 intensity = vec3(dot(rgb, W));
	return mix(intensity, rgb, parameter);
}

float rand(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float rand(vec3 n)
{
    return fract(sin(dot(n, vec3(12.9898, 4.1414, 5.87924))) * 43758.5453);
}

float Noise2D(vec2 p)
{
    vec2 e = vec2(0.0, 1.0);
    vec2 mn = floor(p);
    vec2 xy = fract(p);

    float val = mix(
        mix(rand(mn + e.xx), rand(mn + e.yx), xy.x),
        mix(rand(mn + e.xy), rand(mn + e.yy), xy.x),
        xy.y
    );

    val = val * val * (3.0 - 2.0 * val);
    return val;
}

float Noise3D(vec3 p)
{
    vec2 e = vec2(0.0, 1.0);
    vec3 i = floor(p);
    vec3 f = fract(p);

    float x0 = mix(rand(i + e.xxx), rand(i + e.yxx), f.x);
    float x1 = mix(rand(i + e.xyx), rand(i + e.yyx), f.x);
    float x2 = mix(rand(i + e.xxy), rand(i + e.yxy), f.x);
    float x3 = mix(rand(i + e.xyy), rand(i + e.yyy), f.x);

    float y0 = mix(x0, x1, f.y);
    float y1 = mix(x2, x3, f.y);

    float val = mix(y0, y1, f.z);

    val = val * val * (3.0 - 2.0 * val);
    return val;
}

float SmoothNoise(vec3 p)
{
    float amp = 1.0;
    float freq = 1.0;
    float val = 0.0;

    for (int i = 0; i < 4; i++)
    {
        amp *= 0.5;
        val += amp * Noise3D(freq * p - float(i) * 11.7179);
        freq *= 2.0;
    }

    return val;
}

float Pow5(float x)
{
    return (x * x) * (x * x) * x;
}

// Schlick approx
// Ref: https://en.wikipedia.org/wiki/Schlick's_approximation
float FTerm(float LDotH, float f0)
{
    return f0 + (1.0 - f0) * Pow5(1.0 - LDotH);
}

float OceanHeight(vec2 p)
{
    float height = 0.0;
    vec2 grad = vec2(0.0, 0.0);
    float t = u_time;

    float windNorm = length(kWind);
    float windDir = atan(kWind.y, kWind.x);

    for (int i = 1; i < WAVENUM; i++)
    {
        float rndPhi = windDir + asin(2.0 * rand(vec2(0.141 * float(i), 0.1981)) - 1.0);
        float kNorm = 2.0 * M_PI * float(i) / kOceanScale;
        vec2 kDir = vec2(cos(rndPhi), sin(rndPhi));
        vec2 k = kNorm * kDir;
        float l = (windNorm * windNorm) / GRAVITY;
        float amp = exp(-0.5 / (kNorm * kNorm * l * l)) / (kNorm * kNorm);
        float omega = sqrt(GRAVITY * kNorm + 0.01 * sin(p.x));
        float phase = 2.0 * M_PI * rand(vec2(0.6814 * float(i), 0.7315));

        vec2 p2 = p;
        p2 -= amp * k * cos(dot(k, p2) - omega * t + phase);
        height += amp * sin(dot(k, p2) - omega * t + phase);
    }
    return height;
}

vec3 OceanNormal(vec2 p, vec3 camPos)
{
    vec2 e = vec2(0, 1.0 * EPS);
    float l = 20.0 * distance(vec3(p.x, 0.0, p.y), camPos);
    e.y *= l;

    float hx = OceanHeight(p + e.yx) - OceanHeight(p - e.yx);
    float hz = OceanHeight(p + e.xy) - OceanHeight(p - e.xy);
    return normalize(vec3(-hx, 2.0 * e.y, -hz));
}

HitInfo IntersectOcean(Ray ray) {
    HitInfo hit;
    vec3 rayPos = ray.o;
    float dl = rayPos.y / abs(ray.dir.y);
    rayPos += ray.dir * dl;
    hit.pos = rayPos;
    hit.normal = OceanNormal(rayPos.xz, ray.o);
    hit.dist = length(rayPos - ray.o);
    return hit;
}

vec3 RayMarchCloud(Ray ray, vec3 sunDir, vec3 bgColor)
{
    vec3 rayPos = ray.o;
    rayPos += ray.dir * (kCloudHeight - rayPos.y) / ray.dir.y;

    float dl = 1.0;
    float scatter = 0.0;
    vec3 t = bgColor;
    for(int i = 0; i < RAYMARCH_CLOUD_ITER; i++) {
        rayPos += dl * ray.dir;
        float dens = SmoothNoise(vec3(0.05, 0.001 - 0.001 * u_time, 0.1) * rayPos - vec3(0,0, 0.2 * u_time)) *
            SmoothNoise(vec3(0.01, 0.01, 0.01) * rayPos);
        t -= 0.01 * t * dens * dl;
        t += 0.02 * dens * dl;
	}
    return t;
}

// Environment map
vec3 BGColor(vec3 dir, vec3 sunDir) {
    vec3 color = vec3(0);

    color += mix(
        vec3(0.094, 0.2266, 0.3711),
        vec3(0.988, 0.6953, 0.3805),
       	clamp(0.0, 1.0, dot(sunDir, dir) * dot(sunDir, dir)) * smoothstep(-0.1, 0.1, sunDir.y)
    );

    dir.x += 0.01 * sin(312.47 * dir.y + u_time) * exp(-40.0 * dir.y);
    dir = normalize(dir);

    color += smoothstep(0.995, 1.0, dot(sunDir, dir));
	return color;
}

vec3 mainImage( vec2 fragCoord )
{
	vec2 uv = ( gl_FragCoord.xy / u_resolution.xy ) * 2.0 - 1.0;
	float aspect = u_resolution.y / u_resolution.x;

    // Camera settings
	vec3 camPos = vec3(0, kCameraHeight, -kCameraSpeed * u_time);
    vec3 camDir = vec3(kCameraShakeAmp * (rand(vec2(u_time, 0.0)) - 0.5), kCameraShakeAmp * (rand(vec2(u_time, 0.1)) - 0.5), -1);

    vec3 up = vec3(kCameraRollNoiseAmp * (SmoothNoise(vec3(0.2 * u_time, 0.0, 0.0)) - 0.5), 1.0, 0.0);

	vec3 camForward = normalize(camDir);
	vec3 camRight = cross(camForward, up);
	vec3 camUp = cross(camRight, camForward);

    // Ray
    Ray ray;
    ray.o = camPos;
    ray.dir = normalize(
        kFocalLength * camForward +
        kSensorWidth * 0.5 * uv.x * camRight +
        kSensorWidth * 0.5 * aspect * uv.y * camUp
    );

    // Controll the height of the sun
    float mouseY = u_mouse.y;
    if (mouseY <= 0.0) mouseY = 0.5 * u_resolution.y;
    vec3 sunDir = normalize(vec3(0, -0.1 + 0.3 * mouseY / u_resolution.y, -1));

    vec3 color = vec3(0);
	HitInfo hit;
    float l = 0.0;
    if (ray.dir.y < 0.0)
    {
        // Render an ocean
        HitInfo hit = IntersectOcean(ray);

        vec3 oceanColor = vec3(0.0, 0.2648, 0.4421) * dot(-ray.dir, vec3(0, 1, 0));
        vec3 refDir = reflect(ray.dir, hit.normal);
        refDir.y = abs(refDir.y);
        l = -camPos.y / ray.dir.y;
        color = oceanColor + BGColor(refDir, sunDir) * FTerm(dot(refDir, hit.normal), 0.5);
    }
    else
    {
        // Render clouds
        vec3 bgColor = BGColor(ray.dir, sunDir);
        color += RayMarchCloud(ray, sunDir, bgColor);
        l = (kCloudHeight - camPos.y) / ray.dir.y;
    }

    // Fog
    color = mix(color, BGColor(ray.dir, sunDir), 1.0 - exp(-0.0001 * l));

    // Color grading
    color = smoothstep(0.3, 0.8, color);
	//fragColor = vec4(color, 1.0);

    return color;
}

void main(){

    vec2 uv = gl_FragCoord.xy/u_resolution;

    gl_FragColor = vec4(mainImage(gl_FragCoord.xy),1.0);

}
