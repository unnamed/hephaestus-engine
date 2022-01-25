package team.unnamed.hephaestus.writer;

import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.io.TreeOutputStream;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Utility class containing some default resource
 * pack exporting methods
 */
public final class ResourceExports {

    private ResourceExports() {
    }

    /**
     * Fluent-style class for exporting resource
     * packs and upload it using HTTP
     */
    public static class HttpExporter
            implements ResourceExporter<String> {

        private static final String BOUNDARY = "HephaestusBoundary";
        private static final String LINE_FEED = "\r\n";

        private final URL url;
        private final Map<String, String> headers;
        private String fileName;

        public HttpExporter(String url)
                throws MalformedURLException {
            this.url = new URL(url);
            this.headers = new HashMap<>();;
        }

        /**
         * Sets the authorization token for this
         * exporter class
         */
        public HttpExporter setAuthorization(@Nullable String authorization) {
            return setProperty("Authorization", authorization);
        }

        /**
         * Sets a request property for the export
         * @param name The property name
         * @param value The property value
         */
        public HttpExporter setProperty(String name, String value) {
            headers.put(name, value);
            return this;
        }

        /**
         * Sets the filename passed to the HTTP server
         * when uploading the data
         */
        public HttpExporter setFileName(String fileName) {
            this.fileName = Objects.requireNonNull(fileName, "fileName");
            return this;
        }

        @Override
        public String export(ResourcePackWriter writer) throws IOException {

            if (fileName == null) {
                // use 'resourcepack' as default name
                fileName = "resourcepack";
            }

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(10000);

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            connection.setRequestProperty("User-Agent", "Hephaestus-Engine");
            connection.setRequestProperty("Charset", "utf-8");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            headers.forEach(connection::setRequestProperty);

            // write http request body
            try (OutputStream output = connection.getOutputStream()) {
                Streams.writeUTF(
                        output,
                        "--" + BOUNDARY + LINE_FEED
                                + "Content-Disposition: form-data; name=\"" + fileName + "\"; filename=\""
                                + fileName + "\"" + LINE_FEED + "Content-Type: application/octet-stream;" +
                                " charset=utf-8" + LINE_FEED + LINE_FEED
                );

                TreeOutputStream treeOutput = TreeOutputStream.forZip(new ZipOutputStream(output));
                try {
                    writer.write(treeOutput);
                } finally {
                    treeOutput.finish();
                }

                Streams.writeUTF(
                        output,
                        LINE_FEED + "--" + BOUNDARY + "--" + LINE_FEED
                );
            }

            // execute and read the response
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            )) {
                return reader.lines().collect(Collectors.joining());
            }
        }

    }

    /**
     * Creates a new http export fluent builder
     * targeting the given {@code url}
     * @throws MalformedURLException If the given
     * {@code url} isn't a valid URL
     */
    public static HttpExporter newHttpExporter(String url)
            throws MalformedURLException {
        return new HttpExporter(url);
    }

    /**
     * Fluent-style class for exporting resource
     * packs to {@link File}s
     */
    public static class FileExporter
            implements ResourceExporter<File> {

        private final File target;
        private boolean mergeZip;

        public FileExporter(File target) {
            this.target = target;
        }

        /**
         * Set to true if the export must open a
         * {@link ZipOutputStream} if the {@code target}
         * file exists. If it exists, it will read its
         * entries and put them in the output
         */
        public FileExporter setMergeZip(boolean mergeZip) {
            this.mergeZip = mergeZip;
            return this;
        }

        @Override
        public File export(ResourcePackWriter writer) throws IOException {
            if (!target.exists() && !target.createNewFile()) {
                throw new IOException("Failed to create target resource pack file");
            }
            if (mergeZip && target.exists()) {

                File tmpTarget = new File(
                        target.getParentFile(),
                        Long.toHexString(System.nanoTime()) + ".tmp"
                );

                if (!tmpTarget.createNewFile()) {
                    throw new IOException(
                            "Cannot generate temporary file to write the merged output"
                    );
                }

                try (TreeOutputStream output = TreeOutputStream.forZip(
                        new ZipOutputStream(new FileOutputStream(tmpTarget))
                )) {
                    try (ZipInputStream input = new ZipInputStream(new FileInputStream(target))) {
                        ZipEntry entry;
                        while ((entry = input.getNextEntry()) != null) {
                            output.useEntry(entry.getName());
                            Streams.pipe(input, output);
                            output.closeEntry();
                        }
                    }

                    writer.write(output);
                }

                // delete old file
                if (!target.delete()) {
                    throw new IOException("Cannot delete original ZIP file");
                }

                if (!tmpTarget.renameTo(target)) {
                    throw new IOException("Cannot move temporary file to original ZIP file");
                }
            } else {
                try (TreeOutputStream output = TreeOutputStream.forZip(
                        new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(target))))
                ) {
                    writer.write(output);
                }
            }

            return target;
        }
    }

    /**
     * Creates a new file export fluent builder
     * targeting the given {@code file}
     */
    public static FileExporter newFileExporter(File file) {
        return new FileExporter(file);
    }

    /**
     * Creates a new file-tree exporter, the resources
     * will be exported into the given {@code root} folder
     */
    public static ResourceExporter<Void> newTreeExporter(File root) {
        return data -> {
            try (TreeOutputStream output = TreeOutputStream.forFolder(root)) {
                data.write(output);
            }
            return null;
        };
    }

}