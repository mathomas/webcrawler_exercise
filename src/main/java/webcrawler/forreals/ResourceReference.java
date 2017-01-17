package webcrawler.forreals;

import static webcrawler.forreals.ResourceType.*;

/**
 * Lightweight representation of a reference to a given resource.  Useful to hold
 * discrimination logic for use in predicates.
 */
public class ResourceReference {
    private final String uri;
    private final ResourceType type;

    private ResourceReference(String uri, ResourceType type) {
        this.uri = uri;
        this.type = type;
    }

    static ResourceReference page(String uri) {
        return new ResourceReference(uri, PAGE);
    }

    static ResourceReference image(String src) {
        return new ResourceReference(src, IMAGE);
    }

    String getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceReference resourceReference = (ResourceReference) o;

        if (uri != null ? !uri.equals(resourceReference.uri) : resourceReference.uri != null) return false;
        return type == resourceReference.type;
    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResourceReference{" +
                "uri='" + uri + '\'' +
                ", type=" + type +
                '}';
    }

    boolean isPage() {
        return type == ResourceType.PAGE;
    }

    boolean isLocal(String localDomain) {
        return uri.startsWith(localDomain) || uri.startsWith("/");
    }

    boolean isReportable() {
        return !(getUri().isEmpty() ||
                getUri().startsWith("mailto:") ||
                getUri().startsWith("tel:") ||
                getUri().startsWith("#")
        );
    }
}
