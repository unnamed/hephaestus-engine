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
package team.unnamed.hephaestus.playermodel;

import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;

import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ResourcePlayerModelWriter implements PlayerModelWriter<ResourcePack> {

    private final static String SHADERS_PATH = "assets/minecraft/shaders";
    private final static String RESOURCES_SHADERS_PATH = "playermodel/shaders";

    private final static String MODELS_PATH = "assets/minecraft/models";
    private final static String RESOURCES_MODELS_PATH = "playermodel/models";

    private final static ClassLoader CLASS_LOADER = ResourcePlayerModelWriter.class.getClassLoader();

    @Override
    public void write(ResourcePack resourcePack) {
        visit(RESOURCES_SHADERS_PATH, SHADERS_PATH, resourcePack);
        visit(RESOURCES_MODELS_PATH, MODELS_PATH, resourcePack);
    }

    private void visit(String resourcesPath, String writePath, ResourcePack resourcePack) {
        try {
            URI uri = this.getClass().getResource("/" + resourcesPath).toURI();
            Path path;

            try {
                path = Paths.get(uri);
            } catch (FileSystemNotFoundException exception) {
                path = FileSystems.newFileSystem(uri, new HashMap<>()).getPath("/" + resourcesPath);
            }

            Path finalPath = path;

            Files.walk(path).forEach(filePath -> {
                if (!Files.isDirectory(filePath)) {
                    String resourcePath = filePath.toString().substring(1);

                    resourcePack.unknownFile(
                            writePath + "/" + finalPath.relativize(filePath),
                            Writable.resource(CLASS_LOADER, resourcePath)
                    );
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}