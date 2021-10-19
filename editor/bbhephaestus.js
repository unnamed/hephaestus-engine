/**
 * MIT License
 *
 * Copyright (c) 2021 Unnamed Team
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
(function() {

    function createFormat() {

        const codec = new Codec('hephaestus_model', {
            name: 'Hephaestus Generic Model',
            extension: 'bbmodel',
            remember: true,
            load_filter: {
                type: 'json',
                extensions: ['bbmodel']
            }
        });

        new ModelFormat({
            id: 'hephaestus_model',
            name: 'Hephaestus Generic Model',
            icon: 'icon-armor_stand',
            description: 'Extension of the Generic Model for a better integration ' +
                    'with Hephaestus-Engine',

            rotate_cubes: true,
            integer_size: true,
            rotation_limit: true, // Cube rotation, % 22.5 degrees
            display_mode: true,
            animation_mode: true,
            codec: codec
        });
    }

    // Plugin registration
    Plugin.register('bbhephaestus', {
        title: 'Hephaestus Engine',
        author: 'Unnamed Team',
        icon: 'icon-armor_stand',
        description: 'Plugin for a better integration with Hephaestus-Engine',
        version: '0.1.0',
        variant: 'both',
        onload() {
            createFormat();
        }
    });

})();