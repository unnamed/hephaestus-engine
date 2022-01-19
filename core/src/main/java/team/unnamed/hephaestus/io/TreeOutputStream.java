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
package team.unnamed.hephaestus.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A tree output stream, represents a data tree
 * where data can be written using names as keys for
 * the entries, it can represent a virtual entry tree
 * like when using a {@link ZipOutputStream} or a real
 * file tree where data will be written as real files.
 *
 * <strong>Note that this class is not thread-safe</strong>
 *
 * @author yusshu (Andre Roldan)
 */
public abstract class TreeOutputStream
        extends OutputStream {

    /**
     * Start using the given {@code name} to
     * write the data
     * @param name The entry name
     */
    public abstract void useEntry(String name) throws IOException;

    /**
     * Closes the currently used entry, the method
     * {@link TreeOutputStream#useEntry} must be
     * called first to close it.
     *
     * <strong>Invoking this method is required to avoid
     * having unused open output streams</strong>
     */
    public abstract void closeEntry() throws IOException;

    /**
     * Finishes the writing of the tree without closing the
     * underlying stream, similar to {@link ZipOutputStream#finish()}
     */
    public abstract void finish() throws IOException;

    /**
     * Implementation of {@link TreeOutputStream} for a
     * virtual file tree using the ZIP file format. This
     * class just wraps a {@link ZipOutputStream}
     */
    private static class ZipTreeOutputStream
            extends TreeOutputStream {

        private final ZipOutputStream delegate;

        private ZipTreeOutputStream(ZipOutputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public void useEntry(String name) throws IOException {
            ZipEntry entry = new ZipEntry(name);
            // TODO: Maybe the invoker method should have the control of this
            entry.setTime(0L);
            delegate.putNextEntry(entry);
        }

        //#region Just delegated behavior
        @Override
        public void closeEntry() throws IOException {
            delegate.closeEntry();
        }

        @Override
        public void write(byte@NotNull[] bytes, int off, int len)
                throws IOException {
            delegate.write(bytes, off, len);
        }

        @Override
        public void write(byte@NotNull[] bytes) throws IOException {
            delegate.write(bytes);
        }

        @Override
        public void write(int i) throws IOException {
            delegate.write(i);
        }

        @Override
        public void finish() throws IOException {
            delegate.finish();
        }

        @Override
        public void flush() throws IOException {
            delegate.flush();
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }
        //#endregion

    }

    /**
     * Creates a virtual {@link TreeOutputStream} instance
     * representing the given {@code output}
     */
    public static TreeOutputStream forZip(ZipOutputStream output) {
        return new ZipTreeOutputStream(output);
    }

    /**
     * Implementation of {@link TreeOutputStream} for a
     * real file tree
     */
    private static class FileTreeOutputStream
            extends TreeOutputStream {

        private final File root;
        @Nullable private OutputStream entry;

        private FileTreeOutputStream(File root) {
            this.root = root;
        }

        @Override
        public void useEntry(String name) throws IOException {
            this.closeEntry(); // close the previous entry
            File file = new File(root, name);
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (!parent.exists() && !parent.mkdirs()) {
                    throw new IOException("Cannot create " +
                            "entry folder");
                }
                if (!file.createNewFile()) {
                    // this should never happen since we've already
                    // checked for the file existence, if something
                    // fails, it should throw an exception, not return
                    // false
                    throw new IOException("Cannot create" +
                            " entry file");
                }
            }
            this.entry = new FileOutputStream(file);
        }

        @Override
        public void closeEntry() throws IOException {
            if (entry != null) {
                entry.close();
                entry = null;
            }
        }

        @Override
        public void finish() throws IOException {
            this.closeEntry();
        }

        @Override
        public void close() throws IOException {
            this.closeEntry();
        }

        @NotNull
        private OutputStream checkOpenEntry() throws IOException {
            if (entry == null) {
                throw new IOException("No current open entry!" +
                        " You must invoke useEntry(...) first!");
            }
            return entry;
        }

        //#region write delegations
        @Override
        public void write(byte@NotNull[] bytes, int off, int len)
                throws IOException {
            checkOpenEntry().write(bytes, off, len);
        }

        @Override
        public void write(byte@NotNull[] bytes) throws IOException {
            checkOpenEntry().write(bytes);
        }

        @Override
        public void write(int i) throws IOException {
            checkOpenEntry().write(i);
        }
        //#endregion

    }

    /**
     * Creates a {@link TreeOutputStream} instance
     * representing the file tree inside the given {@code folder}
     */
    public static TreeOutputStream forFolder(File folder) {
        return new FileTreeOutputStream(folder);
    }

}
