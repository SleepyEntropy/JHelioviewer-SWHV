void main(void) {
    vec2 normalizedScreenpos = 2. * ((gl_FragCoord.xy - viewportOffset) / viewport.xy - 0.5);
    vec4 up1 = cameraTransformationInverse * vec4(normalizedScreenpos.x, normalizedScreenpos.y, -1., 1.);

    float radius2 = dot(up1.xy, up1.xy);
    vec3 hitPoint = vec3(up1.x, up1.y, sqrt(1. - radius2));
    vec3 rotatedHitPoint = rotate_vector_inverse(cameraDifferenceRotationQuat, hitPoint);

    if (radius2 >= 1. || rotatedHitPoint.z <= 0.) {
        hitPoint = vec3(up1.x, up1.y, intersectPlane(cameraDifferenceRotationQuat, up1));
        rotatedHitPoint = rotate_vector_inverse(cameraDifferenceRotationQuat, hitPoint);
    }

    float factor = 1.;
    if (radius2 >= 1.) {
        factor = sqrt(radius2);
    }
    vec2 texcoord = vec2((rotatedHitPoint.x - rect.x) * rect.z, (-rotatedHitPoint.y - rect.y) * rect.w);
    clamp_texcoord(texcoord);

    float geometryFlatDist = abs(dot(rotatedHitPoint, cutOffDirection));
    vec3 cutOffDirectionAlt = vec3(-cutOffDirection.y, cutOffDirection.x, 0.);
    float geometryFlatDistAlt = abs(dot(rotatedHitPoint, cutOffDirectionAlt));

    float rotatedHitPointRad = length(rotatedHitPoint.xy);
    if (rotatedHitPointRad > cutOffRadius.y || rotatedHitPointRad < cutOffRadius.x ||
        (cutOffValue >= 0. && (geometryFlatDist > cutOffValue || geometryFlatDistAlt > cutOffValue))) {
        discard;
    }

    vec2 difftexcoord;
    if (isdifference != NODIFFERENCE) {
        vec3 diffrotatedHitPoint = rotate_vector_inverse(diffcameraDifferenceRotationQuat, hitPoint);
        if (radius2 >= 1. && diffrotatedHitPoint.z <= 0.) {
            hitPoint = vec3(up1.x, up1.y, intersectPlane(diffcameraDifferenceRotationQuat, up1));
            diffrotatedHitPoint = rotate_vector_inverse(diffcameraDifferenceRotationQuat, hitPoint);
        }

        difftexcoord = vec2((diffrotatedHitPoint.x - differencerect.x) * differencerect.z, (-diffrotatedHitPoint.y - differencerect.y) * differencerect.w);
        clamp_texcoord(difftexcoord);

        float diffrotatedHitPointRad = length(diffrotatedHitPoint.xy);
        if (diffrotatedHitPointRad > cutOffRadius.y || diffrotatedHitPointRad < cutOffRadius.x) {
            discard;
        }
    }
    gl_FragColor = getColor(texcoord, difftexcoord, factor);
}
