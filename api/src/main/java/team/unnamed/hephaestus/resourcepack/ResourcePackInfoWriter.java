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
package team.unnamed.hephaestus.resourcepack;

import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.io.TreeOutputStream;

import java.io.IOException;

/**
 * Implementation of {@link ResourcePackWriter} that
 * writes the resource pack information.
 * @see ResourcePackInfo
 */
public class ResourcePackInfoWriter
        implements ResourcePackWriter {

    private final ResourcePackInfo info;

    public ResourcePackInfoWriter(ResourcePackInfo info) {
        this.info = info;
    }

    @Override
    public void write(TreeOutputStream output) throws IOException {
        // write the pack data
        output.useEntry("pack.mcmeta");
        Streams.writeUTF(
                output,
                "{ " +
                        "\"pack\":{" +
                        "\"pack_format\":" + info.getFormat() + "," +
                        "\"description\":\"" + info.getDescription() + "\"" +
                        "}" +
                        "}"
        );
        output.closeEntry();

        // write the pack icon if not null
        Streamable icon = info.getIcon();
        if (icon != null) {
            output.useEntry("pack.png");
            icon.transfer(output);
            output.closeEntry();
        }
    }

}
