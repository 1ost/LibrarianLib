#version 120

uniform sampler2D layerImage;
uniform sampler2D maskImage;
uniform vec2 displaySize;
uniform float alphaMultiply;
uniform int maskMode;
uniform int renderMode;

uniform bool outlineEnabled;
uniform vec4 outlineColor;
// Outline radius in window pixels (x/y).
uniform vec2 outlineRadius;

// luma function courtesy of https://github.com/hughsk/glsl-luma
float luma(vec4 color) {
    return dot(color.rgb, vec3(0.299, 0.587, 0.114));
}

float applyMask(float layerAlpha, vec4 maskColor) {
    if (maskMode == 1) { // Multiply by alpha
        return layerAlpha * maskColor.a;
    } else if (maskMode == 2) { // Multiply by luma on white
        return layerAlpha * ((1. - maskColor.a) + luma(maskColor) * maskColor.a);
    } else if (maskMode == 3) { // Multiply by luma on black
        return layerAlpha * (luma(maskColor) * maskColor.a);
    } else if (maskMode == 4) { // Multiply by inverse alpha
        return layerAlpha * (1. - maskColor.a);
    } else if (maskMode == 5) { // Multiply by inverse luma on white
        return layerAlpha * (1. - ((1. - maskColor.a) + luma(maskColor) * maskColor.a));
    } else if (maskMode == 6) { // Multiply by inverse luma on black
        return layerAlpha * (1. - (luma(maskColor) * maskColor.a));
    }
    return layerAlpha;
}

float layerAlphaAt(vec2 uv) {
    float a = texture2D(layerImage, uv).a;
    if (maskMode != 0) {
        vec4 maskColor = texture2D(maskImage, uv);
        a = applyMask(a, maskColor);
    }
    return a * alphaMultiply;
}

vec4 layerColorAt(vec2 uv) {
    vec4 layerColor = texture2D(layerImage, uv);
    if (maskMode != 0) {
        vec4 maskColor = texture2D(maskImage, uv);
        layerColor.a = applyMask(layerColor.a, maskColor);
    }
    layerColor.a *= alphaMultiply;
    return layerColor;
}

void main() {
    vec2 uv = gl_FragCoord.xy / displaySize;
    if (renderMode == 2) {
        uv = gl_TexCoord[0].xy;
    }

    vec4 baseColor = layerColorAt(uv);

    if (!outlineEnabled || outlineColor.a <= 0.0 || (outlineRadius.x <= 0.0 && outlineRadius.y <= 0.0)) {
        gl_FragColor = baseColor;
        return;
    }

    // The outline implementation is based on sampling neighboring alpha in window pixel space.
    // Since RenderMode.RENDER_TO_FBO uses gl_FragCoord-based UVs, we enforce that render mode when outlining.
    vec2 texel = vec2(1.0 / displaySize.x, 1.0 / displaySize.y);

    float baseAlpha = baseColor.a;
    float maxNeighborAlpha = baseAlpha;

    float rx = max(outlineRadius.x, 0.0);
    float ry = max(outlineRadius.y, 0.0);
    float maxR = max(rx, ry);

    const int MAX_R = 8;
    int r = int(ceil(maxR));
    if (r > MAX_R) r = MAX_R;

    if (r > 0) {
        // Ellipse distance check in pixel space (supports non-uniform scaling).
        float invRx2 = (rx > 0.0) ? (1.0 / (rx * rx)) : 0.0;
        float invRy2 = (ry > 0.0) ? (1.0 / (ry * ry)) : 0.0;

        for (int dx = -MAX_R; dx <= MAX_R; dx++) {
            for (int dy = -MAX_R; dy <= MAX_R; dy++) {
                if (abs(dx) > r || abs(dy) > r) continue;
                float fx = float(dx);
                float fy = float(dy);

                float dist =
                    ((rx > 0.0) ? (fx * fx * invRx2) : 0.0) +
                    ((ry > 0.0) ? (fy * fy * invRy2) : 0.0);
                if (dist > 1.0) continue;

                vec2 sampleUv = clamp(uv + vec2(fx, fy) * texel, vec2(0.0), vec2(1.0));
                float a = layerAlphaAt(sampleUv);
                maxNeighborAlpha = max(maxNeighborAlpha, a);
            }
        }
    }

    float outlineMask = max(0.0, maxNeighborAlpha - baseAlpha);
    float outlineAlpha = outlineMask * outlineColor.a;

    vec3 basePremul = baseColor.rgb * baseAlpha;
    vec3 outlinePremul = outlineColor.rgb * outlineAlpha;

    float outAlpha = baseAlpha + outlineAlpha * (1.0 - baseAlpha);
    vec3 outPremul = basePremul + outlinePremul * (1.0 - baseAlpha);
    vec3 outRgb = (outAlpha > 0.0) ? (outPremul / outAlpha) : vec3(0.0);

    gl_FragColor = vec4(outRgb, outAlpha);
}
