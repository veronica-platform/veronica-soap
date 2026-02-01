package ec.veronica.soap.client;

public enum SriEnvironment {
    PRUEBAS(1),
    PRODUCCION(2);

    private final int code;

    SriEnvironment(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SriEnvironment fromCode(int code) {
        for (SriEnvironment env : values()) {
            if (env.code == code) {
                return env;
            }
        }
        throw new IllegalArgumentException(
                "Unsupported SRI environment code: " + code +
                        ". Supported codes are: 1 (PRUEBAS), 2 (PRODUCCION)"
        );
    }
}
