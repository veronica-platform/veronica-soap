package ec.veronica.soap.client;

import recepcion.ws.sri.gob.ec.RecepcionComprobantesOffline;
import recepcion.ws.sri.gob.ec.RecepcionComprobantesOfflineService;
import recepcion.ws.sri.gob.ec.RespuestaSolicitud;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SriRecepcionClient {

    private static final String WSDL_PRUEBAS_CLASSPATH = "wsdl/pruebas/RecepcionComprobantesOffline.xml";
    private static final String WSDL_PRODUCCION_CLASSPATH = "wsdl/produccion/RecepcionComprobantesOffline.xml";

    private static final String NAMESPACE = "http://ec.gob.sri.ws.recepcion";
    private static final String SERVICE_NAME = "RecepcionComprobantesOfflineService";
    private static final QName QNAME = new QName(NAMESPACE, SERVICE_NAME);

    private static final ConcurrentHashMap<SriEnvironment, RecepcionComprobantesOfflineService> SERVICE_CACHE = new ConcurrentHashMap<SriEnvironment, RecepcionComprobantesOfflineService>();

    private final SriEnvironment env;
    private final SriRecepcionConfig config;
    private final RecepcionComprobantesOfflineService service;
    private final ThreadLocal<RecepcionComprobantesOffline> portTL;

    public SriRecepcionClient(SriEnvironment env, SriRecepcionConfig config) {
        this.env = Objects.requireNonNull(env, "env es requerido");
        this.config = Objects.requireNonNull(config, "config es requerido");

        this.service = getOrCreateCachedService(this.env);

        this.portTL = ThreadLocal.withInitial(() -> createConfiguredPort());
    }

    public static SriRecepcionClient forEnv(SriEnvironment env) {
        return new SriRecepcionClient(env, SriRecepcionDefaults.forEnv(env));
    }

    public SriEnvironment environment() {
        return env;
    }

    public RecepcionComprobantesOffline port() {
        return portTL.get();
    }

    public void resetPortForCurrentThread() {
        portTL.remove();
    }

    /**
     * Tu operación principal
     */
    public RespuestaSolicitud validarComprobante(byte[] xml) {
        return port().validarComprobante(xml);
    }

    // -----------------------
    // Internals
    // -----------------------

    private RecepcionComprobantesOffline createConfiguredPort() {
        RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();
        SriSoapPorts.configure(port, config);
        return port;
    }

    private static RecepcionComprobantesOfflineService getOrCreateCachedService(SriEnvironment env) {
        RecepcionComprobantesOfflineService existing = SERVICE_CACHE.get(env);
        if (existing != null) {
            return existing;
        }

        RecepcionComprobantesOfflineService created = createServiceForEnv(env);
        RecepcionComprobantesOfflineService raced = SERVICE_CACHE.putIfAbsent(env, created);

        return (raced != null) ? raced : created;
    }

    private static RecepcionComprobantesOfflineService createServiceForEnv(SriEnvironment env) {
        String wsdlClasspath = wsdlClasspathFor(env);
        URL wsdlUrl = loadWsdlFromClasspath(wsdlClasspath);
        return new RecepcionComprobantesOfflineService(wsdlUrl, QNAME);
    }

    private static String wsdlClasspathFor(SriEnvironment env) {
        switch (env) {
            case PRUEBAS:
                return WSDL_PRUEBAS_CLASSPATH;
            case PRODUCCION:
                return WSDL_PRODUCCION_CLASSPATH;
            default:
                throw new IllegalStateException(
                        "Unsupported SRI environment. Supported values are: PRUEBAS, PRODUCCION. Got: " + env
                );
        }
    }

    private static URL loadWsdlFromClasspath(String classpathLocation) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = (cl != null) ? cl.getResource(classpathLocation) : null;
        if (url == null) {
            url = SriRecepcionClient.class.getClassLoader().getResource(classpathLocation);
        }
        if (url == null) {
            throw new IllegalStateException("WSDL not found in classpath: " + classpathLocation);
        }
        return url;
    }
}
