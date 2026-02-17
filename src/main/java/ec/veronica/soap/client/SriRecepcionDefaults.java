package ec.veronica.soap.client;

public final class SriRecepcionDefaults {
    private SriRecepcionDefaults() {
    }

    public static SriRecepcionConfig forEnv(SriEnvironment env) {
        if (env == null) {
            throw new IllegalArgumentException("SriEnvironment no puede ser null");
        }

        switch (env) {
            case PRUEBAS:
                return SriRecepcionConfig.builder()
                        .endpoint("https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline")
                        .connectTimeoutMs(15_000)
                        .readTimeoutMs(30_000)
                        .build();

            case PRODUCCION:
                return SriRecepcionConfig.builder()
                        .endpoint("https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline")
                        .connectTimeoutMs(15_000)
                        .readTimeoutMs(30_000)
                        .build();

            default:
                throw new IllegalStateException("Unsupported environment configuration: " + env);
        }
    }
}