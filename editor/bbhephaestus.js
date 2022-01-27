/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2022 Unnamed Team
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
(function() {
    'use strict';

    let codec;

    // Plugin registration
    Plugin.register('bbhephaestus', {
        title: 'Hephaestus Model Plugin',
        author: 'Unnamed Team',
        icon: 'icon-armor_stand',
        description: 'Plugin for exporting Hephaestus models, for Hephaestus-Engine',
        version: '0.1.0',
        variant: 'both',
        onload() {

            const FORMAT_VERSION = 1;

            codec = new Codec('hephaestus_model', {
                name: 'Hephaestus Model',
                extension: 'hepmodel',
                remember: true,
                load_filter: {
                    type: 'json',
                    extensions: ['bbmodel']
                },

                export() {
                    Blockbench.export({
                        resource_id: 'model',
                        type: this.name,
                        extensions: [ this.extension ],
                        name: this.fileName(),
                        startpath: this.startPath(),
                        content: Blockbench.isApp ? null : this.compile(),
                        custom_writer: Blockbench.isApp ? (content, path) => {
                            Project.save_path = path;
                            content = this.compile();
                            this.write(content, path);
                        } : null
                    }, path => this.afterDownload(path));
                },

                compile() {

                    if (Project.box_uv) {
                        Blockbench.showMessageBox({
                            title: 'Hephaestus Model Exporter',
                            message: 'Models must not use "Box UV" UV mode'
                        });
                    }

                    const model = {
                        name: Project.geometry_name || Project.name || 'unnamed_model',
                        meta: {
                            format_version: FORMAT_VERSION,
                            creation_time: Math.round(Date.now() / 1000),
                            model_format: Format.id
                        },
                        resolution: {
                            width: Project.texture_width || 16,
                            height: Project.texture_height || 16
                        },
                        outliner: (function () {
                            const result = [];
                            function iterate(array, target) {
                                for (const element of array) {
                                    if (element.type === 'group') {
                                        // compile bone
                                        const object = element.compile(true);
                                        if (element.children.length > 0) {
                                            // recursively save children
                                            iterate(element.children, object.children);
                                        }
                                        target.push(object);
                                    } else {
                                        target.push({
                                            from: element.from,
                                            to: element.to,
                                            origin: element.origin,
                                            rotation: element.rotation.allEqual(0)
                                                    ? undefined
                                                    : element.rotation,
                                            faces: (function () {
                                                const faces = {};
                                                for (const key in element.faces) {
                                                    const face = element.faces[key];
                                                    const copy = { uv: face.uv };
                                                    if (face.texture) copy.texture = Texture.all
                                                            .findIndex(t => t.uuid === face.texture);
                                                    if (face.tint !== -1) copy.tint = face.tint;
                                                    faces[key] = copy;
                                                }
                                                return faces;
                                            })()
                                        });
                                    }
                                }
                                console.log(result);
                            }
                            iterate(Outliner.root, result);
                            return result;
                        })(),
                        textures: Texture.all.map(texture => ({
                            name: texture.name,
                            source: texture.getBase64()
                        })),
                        animations: Animator.animations.map(animation => ({
                            name: animation.name,
                            loop: animation.loop,
                            override: animation.override,
                            length: animation.length,
                            animators: (function () {
                                const animators = {};
                                // bone animations
                                for (const uuid in animation.animators) {
                                    const animator = animation.animators[uuid];
                                    const keyframes = animator.keyframes;
                                    if (keyframes && keyframes.length) {
                                        animators[uuid] = {
                                            name: animator.name,
                                            keyframes: keyframes.map(keyframe => ({
                                                channel: keyframe.channel,
                                                time: keyframe.time,
                                                value: (function() {
                                                    const datapoint = keyframe.data_points[0];
                                                    return [ datapoint.x, datapoint.y, data.point.z ];
                                                })()
                                            }))
                                        };
                                    }
                                }
                                return animators;
                            })()
                        }))
                    };

                    return JSON.stringify(model, null, 2);
                }
            });

            const format = new ModelFormat({
                id: 'hephaestus_model',
                name: 'Hephaestus Model',
                icon: 'icon-armor_stand',
                description: 'Hephaestus Model format',
                show_on_start_screen: true,
                rotate_cubes: true,
                integer_size: true,
                rotation_limit: true, // Cube rotation, % 22.5 degrees
                display_mode: true,
                animation_mode: true,
                codec: codec
            });

            codec.export_action = new Action({
                name: 'Export Hephaestus Model',
                id: 'export_hephaestus',
                icon: format.icon,
                category: 'file',
                condition: () => Format == format,
                click: () => codec.export()
            });

            MenuBar.addAction(codec.export_action, 'file.export');
        },
        onunload() {
            codec.export_action.delete();
            codec.delete();
        }
    });

})();