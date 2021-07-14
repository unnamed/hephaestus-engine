package team.unnamed.hephaestus.resourcepack;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Factory for creating exporting methods
 * from {@link String} (from configuration)
 */
public final class ResourceExportMethodFactory {

    private ResourceExportMethodFactory() {
    }

    public static ResourceExporter<?> createExporter(File folder, String format)
            throws IOException {
        String[] args = format.split(":");
        String method = args[0].toLowerCase();

        switch (method) {
            case "file": {
                if (args.length < 2) {
                    throw new IllegalArgumentException(
                            "Invalid format for file export: '" + format
                                    + "'. Use: 'file:filename'"
                    );
                }

                String filename = String.join(":", Arrays.copyOfRange(args, 1, args.length));
                return ResourceExports.newFileExporter(new File(folder, filename));
            }
            case "upload": {
                if (args.length < 3) {
                    throw new IllegalArgumentException(
                            "Invalid format for upload export: '" + format
                                    + "'. Use: 'upload:authorization:url'"
                    );
                }
                String authorization = args[1];
                String url = String.join(":", Arrays.copyOfRange(args, 2, args.length));

                if (authorization.equalsIgnoreCase("none")) {
                    authorization = null;
                }

                return ResourceExports.newHttpExporter(url)
                        .setAuthorization(authorization);
            }
            default: {
                throw new IllegalArgumentException(
                        "Invalid format: '" + format + "', unknown export"
                        + "method: '" + method + "'"
                );
            }
        }
    }

}
