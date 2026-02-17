package ec.veronica.soap.client;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilidades para configurar ports SOAP del SRI (endpoint, timeouts, headers).
 */
public final class SriSoapPorts {

    private SriSoapPorts() {
    }

    /**
     * Aplica endpoint, timeouts y headers por defecto a un port JAX-WS.
     *
     * @param port   el port (debe ser un {@link BindingProvider})
     * @param config configuración con endpoint y timeouts
     */
    public static void configure(Object port, SriSoapPortConfig config) {
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.endpoint());
        bp.getRequestContext().put("com.sun.xml.ws.connect.timeout", config.connectTimeoutMs());
        bp.getRequestContext().put("com.sun.xml.ws.request.timeout", config.readTimeoutMs());
        bp.getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", config.connectTimeoutMs());
        bp.getRequestContext().put("com.sun.xml.internal.ws.request.timeout", config.readTimeoutMs());
        Map<String, List<String>> headers = new HashMap<>();
        bp.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, headers);
    }
}
