package team.unnamed.hephaestus.resourcepack;

import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.io.Streamable;

import java.util.Objects;

/**
 * Resource pack information, contains extra information
 * that can be omitted by a resource pack writer
 */
public class ResourcePackInfo {

    private final int format;
    private final String description;
    @Nullable
    private final Streamable icon;

    /**
     * Constructs a new resource pack info object
     * @param format The pack format, depends on minecraft version
     * @param description The resource-pack description
     * @param icon The resource-pack icon, it won't be written if
     *             it's null
     */
    public ResourcePackInfo(
            int format,
            String description,
            @Nullable Streamable icon
    ) {
        this.format = format;
        this.description = description;
        this.icon = icon;
    }

    public int getFormat() {
        return format;
    }

    public String getDescription() {
        return description;
    }

    @Nullable
    public Streamable getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "ResourcePackInfo{" +
                "format=" + format +
                ", description='" + description + '\'' +
                ", icon=" + icon +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourcePackInfo that = (ResourcePackInfo) o;
        return format == that.format
                && description.equals(that.description)
                && Objects.equals(icon, that.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(format, description, icon);
    }

    /**
     * Creates a {@link ResourcePackWriter} instance
     * for writing information from this instance into
     * a resource pack
     */
    public ResourcePackWriter toWriter() {
        return new ResourcePackInfoWriter(this);
    }

    /**
     * Converts this {@link ResourcePackInfo} into a
     * fluent {@link ResourcePackInfo.Builder}, note that
     * modifications to the builder do not affect this
     * instance.
     */
    public ResourcePackInfo.Builder toBuilder() {
        return builder()
                .setFormat(format)
                .setDescription(description)
                .setIcon(icon);
    }

    /**
     * Returns a new and fresh fluent builder for
     * {@link ResourcePackInfo}
     * @see ResourcePackInfo.Builder
     */
    public static ResourcePackInfo.Builder builder() {
        return new ResourcePackInfo.Builder();
    }

    /**
     * Mutable {@link ResourcePackInfo} fluent
     * builder
     */
    public static class Builder {

        private int format = 7;
        private String description = "";
        @Nullable private Streamable icon;

        public Builder setFormat(int format) {
            this.format = format;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setIcon(@Nullable Streamable icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Builds the immutable instance of {@link ResourcePackInfo}
         * with the previously provided information
         */
        public ResourcePackInfo build() {
            return new ResourcePackInfo(format, description, icon);
        }

    }

}
