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
package team.unnamed.hephaestus.minestom;

import com.sun.net.httpserver.HttpExchange;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.file.FileTreeWriter;
import team.unnamed.creative.server.ResourcePackRequest;
import team.unnamed.creative.server.ResourcePackRequestHandler;

import java.io.IOException;

/**
 * {@link ResourcePackRequestHandler} implementation that re-builds
 * the resource-pack after every request
 */
public class ResourcePackProvider implements ResourcePackRequestHandler {

    private final FileTreeWriter writer;
    private ResourcePack pack;

    public ResourcePackProvider(FileTreeWriter writer) {
        this.writer = writer;
        this.pack = ResourcePack.build(writer);
    }

    @Override
    public void onRequest(ResourcePackRequest request, HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/zip");
        exchange.sendResponseHeaders(200, pack.bytes().length);
        exchange.getResponseBody().write(pack.bytes());

        // rebuild is done *after* the resource-pack is created,
        // to ensure stored hash is correct
        pack = ResourcePack.build(writer);
    }

    public ResourcePack pack() {
        return pack;
    }

}
