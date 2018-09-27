void get_polar_texcoord(const float cr, const vec2 scrpos, const vec4 rect, out vec2 texcoord, out float radius) {
    float theta = -(scrpos.x * TWOPI + PI / 2. - cr);
    float interpolated = polarRadii[0] + scrpos.y * (polarRadii[1] - polarRadii[0]);

    if (cutOffValue >= 0.) {
        vec3 dpos = vec3(sin(theta), cos(theta), 0.) * interpolated;
        vec3 cutOffDirectionAlt = vec3(-cutOffDirection.y, cutOffDirection.x, 0.);
        float geometryFlatDist = abs(dot(dpos, cutOffDirection));
        float geometryFlatDistAlt = abs(dot(dpos, cutOffDirectionAlt));
        if (geometryFlatDist > cutOffValue || geometryFlatDistAlt > cutOffValue)
            discard;
    }

    if (interpolated > radii[1] || interpolated < radii[0])
        discard;

    texcoord = rect.zw * (-rect.xy + vec2(cos(theta), sin(theta)) * interpolated);
    clamp_texture(texcoord);

    radius = 1.;
    if (interpolated > 1.) {
        radius = interpolated;
    }
}

void main(void) {
    vec4 color;
    vec2 texcoord;
    float radius;

    vec2 scrpos = getScrPos();
    get_polar_texcoord(crota[0], scrpos, rect, texcoord, radius);
    if (isdifference == NODIFFERENCE) {
        color = getColor(texcoord, texcoord, radius);
    } else {
        vec2 difftexcoord;
        float diffradius;
        get_polar_texcoord(crotaDiff[0], scrpos, differencerect, difftexcoord, diffradius);
        color = getColor(texcoord, difftexcoord, radius);
    }
    outColor = color;
}
