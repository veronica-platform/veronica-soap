package ec.veronica.soap.client;

import java.util.Objects;

public class SriRecepcionConfig {

    private final String endpoint;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;

    private SriRecepcionConfig(Builder b) {
        this.endpoint = Objects.requireNonNull(b.endpoint, "Endpoint field is required");
        this.connectTimeoutMs = b.connectTimeoutMs;
        this.readTimeoutMs = b.readTimeoutMs;
    }

    public String endpoint() {
        return endpoint;
    }

    public int connectTimeoutMs() {
        return connectTimeoutMs;
    }

    public int readTimeoutMs() {
        return readTimeoutMs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String endpoint;
        private int connectTimeoutMs = 15_000;
        private int readTimeoutMs = 30_000;

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder connectTimeoutMs(int ms) {
            this.connectTimeoutMs = ms;
            return this;
        }

        public Builder readTimeoutMs(int ms) {
            this.readTimeoutMs = ms;
            return this;
        }

        public SriRecepcionConfig build() {
            return new SriRecepcionConfig(this);
        }
    }
}
