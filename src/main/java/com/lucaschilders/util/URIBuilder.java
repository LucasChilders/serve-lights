package com.lucaschilders.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.net.URI;
import java.util.List;

public class URIBuilder {
    private final String uri;

    private URIBuilder(final String uri) {
        this.uri = uri;
    }

    public URI getUri() {
        return URI.create(this.uri);
    }

    public static class Builder {
        private Protocol protocol;
        private String host;
        private List<String> path;

        public Builder() {
            this.protocol = Protocol.HTTP;
            this.path = Lists.newArrayList();
        }

        public Builder withProtocol(final Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder withHost(final String host) {
            this.host = host;
            return this;
        }

        public Builder withSegment(final String segment) {
            if (path.contains("/")) {
                throw new IllegalArgumentException(
                        "Segment [{}] contains '/', invoke this method multiple times to construct a full path.");
            }
            this.path.add(segment);
            return this;
        }

        public URIBuilder build() {
            Preconditions.checkNotNull(host, "Host cannot be null!");
            return new URIBuilder(String.format("%s%s/%s", this.protocol.protocol, this.host,
                    String.join("/", path)));
        }
    }

    public enum Protocol {
        HTTP("http://"),
        HTTPS("https://");

        private final String protocol;

        Protocol(final String protocol) {
            this.protocol = protocol;
        }
    }
}
