package ec.veronica.soap.client;

import autorizacion.ws.sri.gob.ec.AutorizacionComprobantesOffline;
import autorizacion.ws.sri.gob.ec.AutorizacionComprobantesOfflineService;
import autorizacion.ws.sri.gob.ec.RespuestaComprobante;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;

public class AutorizacionComprobanteProxy {

    private AutorizacionComprobantesOfflineService service;
    private AutorizacionComprobantesOffline port;

    public AutorizacionComprobanteProxy(String wsdlLocation) throws MalformedURLException {
        service = new AutorizacionComprobantesOfflineService(new URL(wsdlLocation),
                new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService"));
        port = service.getAutorizacionComprobantesOfflinePort();
        ((BindingProvider) port).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", 10000);
        ((BindingProvider) port).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", 10000);
    }

    public RespuestaComprobante autorizacionIndividual(String accessKey) {
        return port.autorizacionComprobante(accessKey);
    }

}