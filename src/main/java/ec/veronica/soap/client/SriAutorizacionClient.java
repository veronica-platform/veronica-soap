package ec.veronica.soap.client;

import autorizacion.ws.sri.gob.ec.AutorizacionComprobantesOffline;
import autorizacion.ws.sri.gob.ec.AutorizacionComprobantesOfflineService;
import autorizacion.ws.sri.gob.ec.RespuestaComprobante;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SriAutorizacionClient {

    private static final String WSDL_PRUEBAS_CLASSPATH = "wsdl/pruebas/AutorizacionComprobantesOffline.xml";
    private static final String WSDL_PRODUCCION_CLASSPATH = "wsdl/produccion/AutorizacionComprobantesOffline.xml";

    private static final String NAMESPACE = "http://ec.gob.sri.ws.autorizacion";
    private static final String SERVICE_NAME = "AutorizacionComprobantesOfflineService";
    private static final QName QNAME = new QName(NAMESPACE, SERVICE_NAME);

    private static final ConcurrentHashMap<SriEnvironment, AutorizacionComprobantesOfflineService> SERVICE_CACHE = new ConcurrentHashMap<>();

    private final SriEnvironment env;
    private final SriAutorizacionConfig config;
    private final AutorizacionComprobantesOfflineService service;
    private final ThreadLocal<AutorizacionComprobantesOffline> portTL;

    public SriAutorizacionClient(SriEnvironment env, SriAutorizacionConfig config) {
        this.env = Objects.requireNonNull(env, "env es requerido");
        this.config = Objects.requireNonNull(config, "config es requerido");

        this.service = getOrCreateCachedService(this.env);

        this.portTL = ThreadLocal.withInitial(() -> createConfiguredPort());
    }

    /**
     * Conveniencia opcional
     */
    public static SriAutorizacionClient forEnv(SriEnvironment env) {
        return new SriAutorizacionClient(env, SriAutorizacionDefaults.forEnv(env));
    }

    public SriEnvironment environment() {
        return env;
    }

    public AutorizacionComprobantesOffline port() {
        return portTL.get();
    }

    public void resetPortForCurrentThread() {
        portTL.remove();
    }

    /**
     * Tu operación principal
     */
    public RespuestaComprobante autorizacionIndividual(String accessKey) {
        return port().autorizacionComprobante(accessKey);
    }

    // -----------------------
    // Internals
    // -----------------------

    private AutorizacionComprobantesOffline createConfiguredPort() {
        AutorizacionComprobantesOffline port = service.getAutorizacionComprobantesOfflinePort();
        configurePort(port, config);
        return port;
    }

    private static AutorizacionComprobantesOfflineService getOrCreateCachedService(SriEnvironment env) {
        AutorizacionComprobantesOfflineService existing = SERVICE_CACHE.get(env);
        if (existing != null) {
            return existing;
        }

        AutorizacionComprobantesOfflineService created = createServiceForEnv(env);
        AutorizacionComprobantesOfflineService raced = SERVICE_CACHE.putIfAbsent(env, created);

        return (raced != null) ? raced : created;
    }

    private static AutorizacionComprobantesOfflineService createServiceForEnv(SriEnvironment env) {
        String wsdlClasspath = wsdlClasspathFor(env);
        URL wsdlUrl = loadWsdlFromClasspath(wsdlClasspath);
        return new AutorizacionComprobantesOfflineService(wsdlUrl, QNAME);
    }

    private static void configurePort(Object port, SriAutorizacionConfig config) {
        BindingProvider bp = (BindingProvider) port;

        // Endpoint real (sin ?wsdl)
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.endpoint());

        // Timeouts: seteo ambos (internal y non-internal) para máxima compatibilidad en Java 8
        bp.getRequestContext().put("com.sun.xml.ws.connect.timeout", config.connectTimeoutMs());
        bp.getRequestContext().put("com.sun.xml.ws.request.timeout", config.readTimeoutMs());
        bp.getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", config.connectTimeoutMs());
        bp.getRequestContext().put("com.sun.xml.internal.ws.request.timeout", config.readTimeoutMs());

        // Headers (por consistencia con tu RecepcionClient)
        Map<String, List<String>> headers = new HashMap<>();
        bp.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, headers);
    }

    private static String wsdlClasspathFor(SriEnvironment env) {
        switch (env) {
            case PRUEBAS:
                return WSDL_PRUEBAS_CLASSPATH;
            case PRODUCCION:
                return WSDL_PRODUCCION_CLASSPATH;
            default:
                throw new IllegalStateException("Unsupported SRI environment. Supported values are: PRUEBAS, PRODUCCION. Got: " + env);
        }
    }

    private static URL loadWsdlFromClasspath(String classpathLocation) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = (cl != null) ? cl.getResource(classpathLocation) : null;

        if (url == null) {
            url = SriAutorizacionClient.class.getClassLoader().getResource(classpathLocation);
        }

        if (url == null) {
            throw new IllegalStateException("WSDL not found in classpath: " + classpathLocation);
        }

        return url;
    }
}