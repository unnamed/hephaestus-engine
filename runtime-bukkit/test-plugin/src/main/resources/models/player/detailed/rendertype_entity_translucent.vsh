#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform int FogShape;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;
out vec4 normal;

// hephaestus-engine start
uniform sampler2D Sampler0;
out vec2 texCoord1;
out float part;

#define SPACING 1024.0
#define MAXRANGE (0.5 * SPACING)

// hephaestus-engine end

int getCube(int part, int outerLayer) {
    int cube = 0;
    switch (part) {
        case 1: cube = 8; break;
        case 2: cube = 12; break;
        case 3: cube = 10; break;
        case 4: cube = 14; break;
        case 5: cube = 2; break;
        case 6: cube = 4; break;
        case 7: cube = 6; break;
        case 8: cube = 16; break;
        case 9: cube = 20; break;
        case 10: cube = 18; break;
        case 11: cube = 22; break;
    }
    return cube + outerLayer;
}

vec3 getCubeSize(int cube, int slim) {
    if(cube >= 2 && cube <= 7)
    return vec3(8, 4, 4);

    if(cube >= 8 && cube <= 15) {
        if (slim == 1) {
            return vec3(3, 6, 4);
        } else {
            return vec3(4, 6, 4);
        }
    }

    if(cube >= 16 && cube <= 23)
    return vec3(4, 6, 4);

    return vec3(8, 8, 8);
}
// 0 Up
// 1 Down
// 2 Right
// 3 Front
// 4 Left
// 5 Back

bool shouldRender(int cube, int corner) {
    corner /= 4;
    if ((cube == 8 || cube == 9 || cube == 12 || cube == 13) && corner == 1) return false;
    if ((cube == 10 || cube == 11 || cube == 14 || cube == 15) && corner == 0) return false;
    if ((cube == 16 || cube == 17 || cube == 20 || cube == 21) && corner == 0) return false;
    if ((cube == 18 || cube == 19 || cube == 22 || cube == 23) && corner == 1) return false;
    if ((cube == 2 || cube == 3) && corner == 0) return false;
    if ((cube == 6 || cube == 7) && corner == 1) return false;
    if ((cube == 4 || cube == 5) && (corner == 1 || corner == 0)) return false;
    return true;
}

vec2 getBoxUV(int cube) {
    switch(cube) {
        case 2: // Hip
        case 4: // Waist
        case 6: // Chest
        return vec2(16, 16);
        case 3:
        case 5:
        case 7: // Jacket
        return vec2(16, 32);
        case 8:
        case 10: // Right Arm
        return vec2(40, 16);
        case 9:
        case 11: // Right Sleeve
        return vec2(40, 32);
        case 12:
        case 14: // Left Arm
        return vec2(32, 48);
        case 13:
        case 15: // Left Sleeve
        return vec2(48, 48);
        case 16:
        case 18: // Right Leg
        return vec2(0, 16);
        case 17:
        case 19: // Right Pant
        return vec2(0, 32);
        case 20:
        case 22: // Left Leg
        return vec2(16, 48);
        case 21:
        case 23: // Left Pant
        return vec2(0, 48);

    }
    return vec2(0, 0);
}

float getYOffset(int cube) {
    float r = 0;
    switch(cube) {
        case 2:
        case 3:
        r = 8;
        break;
        case 4:
        case 5:
        r = 4;
        break;
        case 10: // Right Arm
        case 11: // Right Sleeve
        case 14: // Left Arm
        case 15: // Left Sleeve
        case 18: // Right Leg
        case 19: // Right Pant
        case 22: // Left Leg
        case 23: // Left Pant
        r = 6;
        break;
    }
    return r / 64.;
}

vec2 getUVOffset(int corner, vec3 cubeSize, float yOffset) {
    vec2 offset, uv;
    switch(corner / 4) {
        case 0: // Up
        offset = vec2(cubeSize.z, 0);
        uv = vec2(cubeSize.x, cubeSize.z);
        break;
        case 1: // Down
        offset = vec2(cubeSize.z + cubeSize.x, 0);
        uv = vec2(cubeSize.x, cubeSize.z);
        break;
        case 2: // Right
        offset = vec2(0, cubeSize.z);
        offset.y += yOffset;
        uv = vec2(cubeSize.z, cubeSize.y);
        break;
        case 3: // Front
        offset = vec2(cubeSize.z, cubeSize.z);
        offset.y += yOffset;
        uv = vec2(cubeSize.x, cubeSize.y);
        break;
        case 4: // Left
        offset = vec2(cubeSize.z + cubeSize.x, cubeSize.z);
        offset.y += yOffset;
        uv = vec2(cubeSize.z, cubeSize.y);
        break;
        case 5: // Back
        offset = vec2(2 * cubeSize.z + cubeSize.x, cubeSize.z);
        offset.y += yOffset;
        uv = vec2(cubeSize.x, cubeSize.y);
        break;
    }

    switch(corner % 4) {
        case 0:
        offset += vec2(uv.x, 0);
        break;
        case 2:
        offset += vec2(0, uv.y);
        break;
        case 3:
        offset += vec2(uv.x, uv.y);
        break;
    }

    return offset;
}

void main() {
    // gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0); // hephaestus-engine

    // vertexDistance = fog_distance(ModelViewMat, IViewRotMat * Position, FogShape); // hephaestus-engine
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, normalize(Normal), Color);// hephaestus-engine - normalize(Normal)
    lightMapColor = texelFetch(Sampler2, UV2 / 16, 0);
    overlayColor = texelFetch(Sampler1, UV1, 0);
    // texCoord0 = UV0; // hephaestus-engine
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);

    // hephaestus-engine start
    ivec2 dim = textureSize(Sampler0, 0);

    if (ProjMat[2][3] == 0.0 || dim.x != 64 || dim.y != 64) { // short circuit if cannot be player
        part = 0.0;
        texCoord0 = UV0;
        texCoord1 = vec2(0.0);
        vertexDistance = fog_distance(ModelViewMat, IViewRotMat * Position, FogShape);
        gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    } else {
        vec3 wpos = IViewRotMat * Position;
        vec2 UVout = UV0;
        vec2 UVout2 = vec2(0.0);
        int partId = -int((wpos.y - MAXRANGE) / SPACING);

        part = float(partId);

        if (partId == 0) { // higher precision position if no translation is needed
            gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
        } else {
            int outerLayer = (gl_VertexID / 24) % 2;

            wpos.y += SPACING * partId;
            gl_Position = ProjMat * ModelViewMat * vec4(inverse(IViewRotMat) * wpos, 1.0);

            int slim = 0;
            if (partId == 1 || partId == 2 || partId == 3 || partId == 4) {
                vec4 samp1 = texture(Sampler0, vec2(54.0 / 64.0, 20.0 / 64.0));
                vec4 samp2 = texture(Sampler0, vec2(55.0 / 64.0, 20.0 / 64.0));
                if (samp1.a == 0.0 || (((samp1.r + samp1.g + samp1.b) == 0.0) && ((samp2.r + samp2.g + samp2.b) == 0.0) && samp1.a == 1.0 && samp2.a == 1.0)) {
                    slim = 1;
                }
            }

            int corner = gl_VertexID % 24;
            int cube = getCube(partId, outerLayer);
            vec3 cubeSize = getCubeSize(cube, slim) / 64;
            vec2 boxUV = getBoxUV(cube) / 64;
            vec2 uvOffset = getUVOffset(corner, cubeSize, getYOffset(cube));
            UVout = boxUV + uvOffset;

        }

        vertexDistance = fog_distance(ModelViewMat, wpos, FogShape);
        texCoord0 = UVout;
    }
    // hephaestus-engine end
}