#ifdef GL_ES
precision highp float;
#endif

uniform float u_time;
uniform vec2 u_resolution;
uniform vec2 u_offset;
uniform sampler2D iChannel0;
//uniform sampler2D u_backbuffer;

void main()
{
	float mx = max( u_resolution.x, u_resolution.y );
	vec2 uv = (gl_FragCoord.xy - u_resolution.xy*0.5)/mx;

	uv += u_offset*0.3;

	float r = 0.7;
	uv *= mat2(
		r, -r,
		r, r );

	float y = uv.y*mx*0.05 + u_time;
	float f = fract( y );
	f = (max( 0.4, min( f, 1.0 - f ) ) - 0.4)*10.0;

	vec3 color =
		vec3(
			mod( y, 6.0 )*f,
			mod( y, 2.0 )*f,
			mod( y, 0.9 )*f )*
		abs( sin( mod(
			30.0 + uv.x,
			uv.y + 1.0 ) ) );
//
//	color = mix(
//		texture2D(
//			u_backbuffer,
//			gl_FragCoord.xy/mx ).rgb,
//		color,
//		0.5 );

	gl_FragColor = vec4(color, 1.0 );
}
