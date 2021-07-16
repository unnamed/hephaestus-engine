package team.unnamed.hephaestus.resourcepack;

import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.model.Model;

import javax.annotation.Nullable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
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
	    private String authorization;
	    private ResourcePackWriter writer;
	    private String fileName;

	    public HttpExporter(String url)
                throws MalformedURLException {
	        this.url = new URL(url);
        }

        /**
         * Sets the authorization token for this
         * exporter class
         */
	    public HttpExporter setAuthorization(@Nullable String authorization) {
	        this.authorization = authorization;
	        return this;
        }

        /**
         * Sets the resource pack writer for this
         * exporter
         */
        public HttpExporter setWriter(@Nullable ResourcePackWriter writer) {
	        this.writer = writer;
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
        public String export(List<Model> models) throws IOException {

            if (writer == null) {
                // use the ZipResourcePackWriter by default
                writer = new ZipResourcePackWriter();
            }

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

            if (authorization != null) {
                connection.setRequestProperty("Authorization", authorization);
            }

            // write http request body
            try (OutputStream output = connection.getOutputStream()) {
                Streams.writeUTF(
                        output,
                        "--" + BOUNDARY + LINE_FEED
                        + "Content-Disposition: form-data; name=\"" + fileName + "\"; filename=\"" + fileName + "\"" + LINE_FEED
                        + "Content-Type: application/octet-stream; charset=utf-8" + LINE_FEED + LINE_FEED
                );

                writer.write(output, models);

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
        private ResourcePackWriter writer;
        private boolean mergeZip;

        public FileExporter(File target) {
            this.target = target;
        }

        /**
         * Sets the resource pack writer for this
         * exporter
         */
        public FileExporter setWriter(@Nullable ResourcePackWriter writer) {
            this.writer = writer;
            return this;
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
        public File export(List<Model> models) throws IOException {
            if (!target.exists() && !target.createNewFile()) {
                throw new IOException("Failed to create target resource pack file");
            }
            if (writer == null) {
                // use the ZipResourcePackWriter by default
                writer = new ZipResourcePackWriter();
            }
            if (mergeZip && target.exists()) {
                try (ZipOutputStream output = new ZipOutputStream(new FileOutputStream(target))) {
                    try (ZipInputStream input = new ZipInputStream(new FileInputStream(target))) {
                        ZipEntry entry;
                        while ((entry = input.getNextEntry()) != null) {
                            ZipEntry putEntry = new ZipEntry(entry.getName());
                            putEntry.setTime(0L);
                            output.putNextEntry(putEntry);

                            Streams.pipe(input, output);
                            output.closeEntry();
                        }
                    }

                    writer.write(output, models);
                }
            } else {
                try (OutputStream output
                             = new BufferedOutputStream(new FileOutputStream(target))) {
                    writer.write(output, models);
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

}
