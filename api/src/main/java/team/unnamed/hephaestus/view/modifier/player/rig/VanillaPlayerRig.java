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

final class VanillaPlayerRig implements PlayerRig {
    public static final PlayerBoneType HEAD = PlayerBoneTypeImpl.builder("head")
            .modelData(1)
            .offset(0)
            .scale(0.9375F, 0.9375F, 0.9375F)
            .translation(0, 7.3F, 0)
            .build();

    public static final PlayerBoneType RIGHT_ARM = PlayerBoneTypeImpl.builder("right_arm")
            .modelData(2)
            .offset(-1024.0F)
            .scale(0.46875F, 1.40625F, 0.46875F)
            .translation(0.96F, 1.6F, 0)
            .slimModelData(7)
            .slimScale(0.3515625F, 1.40625F, 0.46875F)
            .slimTranslation(0.47F, 1.6F, 0)
            .build();

    public static final PlayerBoneType LEFT_ARM = PlayerBoneTypeImpl.builder("left_arm")
            .modelData(3)
            .offset(-2048.0F)
            .scale(0.46875F, 1.40625F, 0.46875F)
            .translation(-0.96F, 1.6F, 0)
            .slimModelData(8)
            .slimScale(0.3515625F, 1.40625F, 0.46875F)
            .slimTranslation(-0.47F, 1.6F, 0)
            .build();

    public static final PlayerBoneType TORSO = PlayerBoneTypeImpl.builder("torso")
            .modelData(4)
            .offset(-3072.0F)
            .scale(0.9375F, 1.40625F, 0.46875F)
            .translation(0, 11, 0)
            .build();

    public static final PlayerBoneType RIGHT_LEG = PlayerBoneTypeImpl.builder("right_leg")
            .modelData(5)
            .offset(-4096.0F)
            .scale(0.46875F, 1.40625F, 0.46875F)
            .translation(-0.08F, -0.2F, 0)
            .build();

    public static final PlayerBoneType LEFT_LEG = PlayerBoneTypeImpl.builder("left_leg")
            .modelData(6)
            .offset(-5120.0F)
            .scale(0.46875F, 1.40625F, 0.46875F)
            .translation(-0.08F, -0.2F, 0)
            .build();

    static final PlayerRig INSTANCE = new VanillaPlayerRig();

    private final Map<String, PlayerBoneType> boneTypes = new HashMap<>();

    private VanillaPlayerRig() {
        boneTypes.put(HEAD.boneName(), HEAD);
        boneTypes.put(RIGHT_ARM.boneName(), RIGHT_ARM);
        boneTypes.put(LEFT_ARM.boneName(), LEFT_ARM);
        boneTypes.put(TORSO.boneName(), TORSO);
        boneTypes.put(RIGHT_LEG.boneName(), RIGHT_LEG);
        boneTypes.put(LEFT_LEG.boneName(), LEFT_LEG);
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
                        
            const vec4[] subuvs = vec4[](
                vec4(4.0,  0.0,  8.0,  4.0 ), // 4x4x12
                vec4(8.0,  0.0,  12.0, 4.0 ),
                vec4(0.0,  4.0,  4.0,  16.0),
                vec4(4.0,  4.0,  8.0,  16.0),
                vec4(8.0,  4.0,  12.0, 16.0),\s
                vec4(12.0, 4.0,  16.0, 16.0),\s
                vec4(4.0,  0.0,  7.0,  4.0 ), // 4x3x12
                vec4(7.0,  0.0,  10.0, 4.0 ),
                vec4(0.0,  4.0,  4.0,  16.0),
                vec4(4.0,  4.0,  7.0,  16.0),
                vec4(7.0,  4.0,  11.0, 16.0),\s
                vec4(11.0, 4.0,  14.0, 16.0),
                vec4(4.0,  0.0,  12.0, 4.0 ), // 4x8x12
                vec4(12.0,  0.0, 20.0, 4.0 ),
                vec4(0.0,  4.0,  4.0,  16.0),
                vec4(4.0,  4.0,  12.0, 16.0),
                vec4(12.0, 4.0,  16.0, 16.0),
                vec4(16.0, 4.0,  24.0, 16.0)
            );
                        
            const vec2[] origins = vec2[](
                vec2(40.0, 16.0), // right arm
                vec2(40.0, 32.0),
                vec2(32.0, 48.0), // left arm
                vec2(48.0, 48.0),
                vec2(16.0, 16.0), // torso
                vec2(16.0, 32.0),
                vec2(0.0,  16.0), // right leg
                vec2(0.0,  32.0),
                vec2(16.0, 48.0), // left leg
                vec2(0.0,  48.0)
            );
            // hephaestus-engine end
                        
            void main() {
                // gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0); // hephaestus-engine
                        
                // vertexDistance = fog_distance(ModelViewMat, IViewRotMat * Position, FogShape); // hephaestus-engine
                vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, normalize(Normal), Color); // hephaestus-engine - normalize(Normal)
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
                        vec4 samp1 = texture(Sampler0, vec2(54.0 / 64.0, 20.0 / 64.0));
                        vec4 samp2 = texture(Sampler0, vec2(55.0 / 64.0, 20.0 / 64.0));
                        bool slim = samp1.a == 0.0 || (((samp1.r + samp1.g + samp1.b) == 0.0) && ((samp2.r + samp2.g + samp2.b) == 0.0) && samp1.a == 1.0 && samp2.a == 1.0);
                        int outerLayer = (gl_VertexID / 24) % 2;\s
                        int faceId = (gl_VertexID % 24) / 4;
                        int vertexId = gl_VertexID % 4;
                        int subuvIndex = faceId;
                        
                        wpos.y += SPACING * partId;
                        gl_Position = ProjMat * ModelViewMat * vec4(inverse(IViewRotMat) * wpos, 1.0);
                       \s
                        UVout = origins[2 * (partId - 1) + outerLayer];
                        UVout2 = origins[2 * (partId - 1)];
                        
                        if (slim && (partId == 1 || partId == 2)) {
                            subuvIndex += 6;
                        } else if (partId == 3) {
                            subuvIndex += 12;
                        }
                        
                        vec4 subuv = subuvs[subuvIndex];
                        
                        vec2 offset = vec2(0.0);
                        if (faceId == 1) {
                            if (vertexId == 0) {
                                offset += subuv.zw;
                            } else if (vertexId == 1) {
                                offset += subuv.xw;
                            } else if (vertexId == 2) {
                                offset += subuv.xy;
                            } else {
                                offset += subuv.zy;
                            }
                        } else {
                            if (vertexId == 0) {
                                offset += subuv.zy;
                            } else if (vertexId == 1) {
                                offset += subuv.xy;
                            } else if (vertexId == 2) {
                                offset += subuv.xw;
                            } else {
                                offset += subuv.zw;
                            }
                        }
                        
                        UVout += offset;
                        UVout2 += offset;
                        UVout /= 64.0;
                        UVout2 /= 64.0;
                    }
                        
                    vertexDistance = fog_distance(ModelViewMat, wpos, FogShape);
                    texCoord0 = UVout;
                    texCoord1 = UVout2;
                }
                // hephaestus-engine end
            }
            """);
}
