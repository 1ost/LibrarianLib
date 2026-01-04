#version 150

uniform sampler2D LayerImage;
uniform sampler2D MaskImage;
uniform vec2 DisplaySize;
uniform float AlphaMultiply;
uniform int MaskMode;
uniform int RenderMode;
uniform bool OutlineEnabled;
uniform vec4 OutlineColor;
// Outline radius in window pixels (x/y).
uniform vec2 OutlineRadius;

in vec2 texelCoord;

out vec4 fragColor;

// luma function courtesy of https://github.com/hughsk/glsl-luma
float luma(vec4 color) {
    return dot(color.rgb, vec3(0.299, 0.587, 0.114));
}

float applyMask(float layerAlpha, vec4 maskColor) {
    if (MaskMode == 1) { // Multiply by alpha
        return layerAlpha * maskColor.a;
    } else if (MaskMode == 2) { // Multiply by luma on white
        return layerAlpha * ((1. - maskColor.a) + luma(maskColor) * maskColor.a);
    } else if (MaskMode == 3) { // Multiply by luma on black
        return layerAlpha * (luma(maskColor) * maskColor.a);
    } else if (MaskMode == 4) { // Multiply by inverse alpha
        return layerAlpha * (1. - maskColor.a);
    } else if (MaskMode == 5) { // Multiply by inverse luma on white
        return layerAlpha * (1. - ((1. - maskColor.a) + luma(maskColor) * maskColor.a));
    } else if (MaskMode == 6) { // Multiply by inverse luma on black
        return layerAlpha * (1. - (luma(maskColor) * maskColor.a));
    }
    return layerAlpha;
}

float layerAlphaAt(ivec2 texel) {
    float a = texelFetch(LayerImage, texel, 0).a;
    if (MaskMode != 0) {
        vec4 maskColor = texelFetch(MaskImage, texel, 0);
        a = applyMask(a, maskColor);
    }
    return a * AlphaMultiply;
}

vec4 layerColorAt(ivec2 texel) {
    vec4 layerColor = texelFetch(LayerImage, texel, 0);
    if (MaskMode != 0) {
        vec4 maskColor = texelFetch(MaskImage, texel, 0);
        layerColor.a = applyMask(layerColor.a, maskColor);
    }
    layerColor.a *= AlphaMultiply;
    return layerColor;
}

void main() {
    ivec2 baseTexel = ivec2(gl_FragCoord.xy);
    if (RenderMode == 2) {
        baseTexel = ivec2(texelCoord);
    }

    vec4 baseColor = layerColorAt(baseTexel);

    if (!OutlineEnabled || OutlineColor.a <= 0.0 || (OutlineRadius.x <= 0.0 && OutlineRadius.y <= 0.0)) {
        fragColor = baseColor;
        return;
    }

    ivec2 maxCoord = ivec2(DisplaySize) - ivec2(1);

    float baseAlpha = baseColor.a;
    float maxNeighborAlpha = baseAlpha;

    float rx = max(OutlineRadius.x, 0.0);
    float ry = max(OutlineRadius.y, 0.0);
    float maxR = max(rx, ry);

    const int MAX_R = 8;
    int r = int(ceil(maxR));
    if (r > MAX_R) r = MAX_R;

    if (r > 0) {
        float invRx2 = (rx > 0.0) ? (1.0 / (rx * rx)) : 0.0;
        float invRy2 = (ry > 0.0) ? (1.0 / (ry * ry)) : 0.0;

        for (int dx = -MAX_R; dx <= MAX_R; dx++) {
            for (int dy = -MAX_R; dy <= MAX_R; dy++) {
                if (abs(dx) > r || abs(dy) > r) continue;
                float fx = float(dx);
                float fy = float(dy);

                float distSample = sqrt(fx * fx + fy * fy);
                if (distSample == 0.0) continue;

                float dirX = fx / distSample;
                float dirY = fy / distSample;
                float denom =
                    ((rx > 0.0) ? (dirX * dirX * invRx2) : 0.0) +
                    ((ry > 0.0) ? (dirY * dirY * invRy2) : 0.0);
                float boundaryDist = (denom > 0.0) ? (1.0 / sqrt(denom)) : 0.0;
                float weight = clamp(boundaryDist + 1.0 - distSample, 0.0, 1.0);
                if (weight <= 0.0) continue;

                ivec2 sampleCoord = baseTexel + ivec2(dx, dy);
                sampleCoord = clamp(sampleCoord, ivec2(0), maxCoord);
                float a = layerAlphaAt(sampleCoord);
                maxNeighborAlpha = max(maxNeighborAlpha, a * weight);
            }
        }
    }

    float outlineMask = max(0.0, maxNeighborAlpha - baseAlpha);
    float outlineAlpha = outlineMask * OutlineColor.a;

    vec3 basePremul = baseColor.rgb * baseAlpha;
    vec3 outlinePremul = OutlineColor.rgb * outlineAlpha;

    float outAlpha = baseAlpha + outlineAlpha * (1.0 - baseAlpha);
    vec3 outPremul = basePremul + outlinePremul * (1.0 - baseAlpha);
    vec3 outRgb = (outAlpha > 0.0) ? (outPremul / outAlpha) : vec3(0.0);

    fragColor = vec4(outRgb, outAlpha);
}
