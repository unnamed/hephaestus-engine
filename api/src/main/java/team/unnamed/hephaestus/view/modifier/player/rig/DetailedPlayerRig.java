/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.hephaestus.view.modifier.player.rig;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Writable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class DetailedPlayerRig implements PlayerRig {
    public static final PlayerBoneType HEAD = PlayerBoneTypeImpl.builder("head")
            .modelData(1)
            .offset(0.0F)
            .scale(0.9375F, 0.9375F, 0.9375F)
            .translation(0.0F, 7.3F, 0.0F)
            .build();

    public static final PlayerBoneType RIGHT_ARM = PlayerBoneTypeImpl.builder("right_arm")
            .modelData(2)
            .offset(-1024.0F)
            .scale(0.46875F, 0.703125F, 0.46875F)
            .translation(0.96F, 1.5F, 0F)
            .slimModelData(13)
            .slimScale(0.3515625F, 0.703125F, 0.46875F)
            .slimTranslation(0.3F, 1.5F, 0.0F)
            .build();

    public static final PlayerBoneType LEFT_ARM = PlayerBoneTypeImpl.builder("left_arm")
            .modelData(3)
            .offset(-2048.0F)
            .scale(0.46875F, 0.703125F, 0.46875F)
            .translation(-0.96F, 1.5F, 0.0F)
            .slimModelData(14)
            .slimScale(0.3515625F, 0.703125F, 0.46875F)
            .slimTranslation(-0.3F, 1.5F, 0.0F)
            .build();

    public static final PlayerBoneType RIGHT_FOREARM = PlayerBoneTypeImpl.builder("right_forearm")
            .modelData(4)
            .offset(-1024.0F * 3)
            .scale(0.46875F, 0.703125F, 0.46875F)
            .translation(0.0F, 0.0F, 0.0F)
            .slimModelData(15)
            .slimScale(0.3515625F, 0.703125F, 0.46875F)
            .slimTranslation(-0.3F, 0.0F, 0.0F)
            .build();

    public static final PlayerBoneType LEFT_FOREARM = PlayerBoneTypeImpl.builder("left_forearm")
            .modelData(5)
            .offset(-1024.0F * 4)
            .scale(0.46875F, 0.703125F, 0.46875F)
            .translation(0.0F, 0.0F, 0.0F)
            .slimModelData(16)
            .slimScale(0.3515625F, 0.703125F, 0.46875F)
            .slimTranslation(0.3F, 0F, 0.0F)
            .build();

    public static final PlayerBoneType HIP = PlayerBoneTypeImpl.builder("hip")
            .modelData(6)
            .offset(-1024.0F * 5)
            .scale(0.9375F, 0.46875F, 0.46875F)
            .translation(0.0F, 3.75F, 0.0F)
            .build();

    public static final PlayerBoneType WAIST = PlayerBoneTypeImpl.builder("waist")
            .modelData(7)
            .offset(-1024.0F * 6)
            .scale(0.9375F, 0.46875F, 0.46875F)
            .translation(0.0F, 3.75F, 0.0F)
            .build();

    public static final PlayerBoneType CHEST = PlayerBoneTypeImpl.builder("chest")
            .modelData(8)
            .offset(-1024.0F * 7)
            .scale(0.9375F, 0.46875F, 0.46875F)
            .translation(0.0F, 3.75F, 0.0F)
            .build();

    public static final PlayerBoneType RIGHT_LEG = PlayerBoneTypeImpl.builder("right_leg")
            .modelData(9)
            .offset(-1024.0F * 8)
            .scale(0.46875F, 0.703125F, 0.46875F)
            .translation(-0.08F, 0F, 0.0F)
            .build();

    public static final PlayerBoneType LEFT_LEG = PlayerBoneTypeImpl.builder("left_leg")
            .modelData(10)
            .offset(-1024.0F * 9)
            .scale(0.46875F, 0.703125F, 0.46875F)
            .translation(0.08F, 0F, 0.0F)
            .build();

    public static final PlayerBoneType RIGHT_FORELEG = PlayerBoneTypeImpl.builder("right_foreleg")
            .modelData(11)
            .offset(-1024.0F * 10)
            .scale(0.46875F, 0.703125F, 0.46875F)
            .translation(-0.07F, 0F, 0.0F)
            .build();

    public static final PlayerBoneType LEFT_FORELEG = PlayerBoneTypeImpl.builder("left_foreleg")
            .modelData(12)
            .offset(-1024.0F * 11)
            .scale(0.46875F, 0.703125F, 0.46875F)
            .translation(0.07F, 0F, 0.0F)
            .build();

    public static final PlayerRig INSTANCE = new DetailedPlayerRig();

    private final Map<String, PlayerBoneType> boneTypes = new HashMap<>();

    private DetailedPlayerRig() {
        boneTypes.put("head", HEAD);
        boneTypes.put("right_arm", RIGHT_ARM);
        boneTypes.put("left_arm", LEFT_ARM);
        boneTypes.put("right_forearm", RIGHT_FOREARM);
        boneTypes.put("left_forearm", LEFT_FOREARM);
        boneTypes.put("hip", HIP);
        boneTypes.put("waist", WAIST);
        boneTypes.put("chest", CHEST);
        boneTypes.put("right_leg", RIGHT_LEG);
        boneTypes.put("left_leg", LEFT_LEG);
        boneTypes.put("right_foreleg", RIGHT_FORELEG);
        boneTypes.put("left_foreleg", LEFT_FORELEG);
    }

    @Override
    public @Nullable PlayerBoneType get(final @NotNull String name) {
        return boneTypes.get(name);
    }

    @Override
    public @NotNull Collection<PlayerBoneType> types() {
        return boneTypes.values();
    }

    @Override
    public @NotNull Writable vertexShader() {
        return VERTEX_SHADER;
    }

    @Override
    public @NotNull Writable fragmentShader() {
        return FRAGMENT_SHADER;
    }

    private static final Writable FRAGMENT_SHADER = Writable.stringUtf8("""
            #version 150
                        
            #moj_import <fog.glsl>
                        
            uniform sampler2D Sampler0;
                        
            uniform vec4 ColorModulator;
            uniform float FogStart;
            uniform float FogEnd;
            uniform vec4 FogColor;
                        
            in float vertexDistance;
            in vec4 vertexColor;
            in vec4 lightMapColor;
            in vec4 overlayColor;
            in vec2 texCoord0;
            in vec4 normal;
                        
            out vec4 fragColor;
                        
            // hephaestus-engine start
            uniform mat4 ModelViewMat;
            uniform mat4 ProjMat;
            uniform mat3 IViewRotMat;
                        
            in vec2 texCoord1;
            in float part;
            // hephaestus-engine end
                        
            void main() {
                vec4 color = texture(Sampler0, texCoord0);
                if (color.a < 0.1 || abs(mod(part + 0.5, 1.0) - 0.5) > 0.001) { // hephaestus-engine
                    discard;
                }
                        
                // hephaestus-engine start
                if (color.a < 1.0 && part > 0.5) {
                    vec4 color2 = texture(Sampler0, texCoord1);
                    if (color.a < 0.75 && int(gl_FragCoord.x + gl_FragCoord.y) % 2 == 0) {
                        discard;
                    }
                    else {
                        color.rgb = mix(color2.rgb, color.rgb, min(1.0, color.a * 2));
                        color.a = 1.0;
                    }
                }
                // hephaestus-engine end
                        
                color *= vertexColor * ColorModulator;
                color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
                color *= lightMapColor;
                fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
            }
            """);

    private static final Writable VERTEX_SHADER = Writable.stringUtf8("""
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
            """);
}
