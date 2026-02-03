package ec.veronica.soap.client;

public final class SriAutorizacionDefaults {

    private SriAutorizacionDefaults() {
    }

    public static SriAutorizacionConfig forEnv(SriEnvironment env) {
        if (env == null) {
            throw new IllegalArgumentException("SriEnvironment can't be null");
        }

        switch (env) {
            case PRUEBAS:
                return SriAutorizacionConfig.builder()
                        .endpoint("https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline")
                        .connectTimeoutMs(15_000)
                        .readTimeoutMs(30_000)
                        .build();

            case PRODUCCION:
                return SriAutorizacionConfig.builder()
                        .endpoint("https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline")
                        .connectTimeoutMs(15_000)
                        .readTimeoutMs(30_000)
                        .build();

            default:
                throw new IllegalStateException(
                        "Unsupported SRI environment. Supported values are: PRUEBAS, PRODUCCION. Got: " + env
                );
        }
    }
}