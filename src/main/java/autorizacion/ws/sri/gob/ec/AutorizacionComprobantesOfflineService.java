package autorizacion.ws.sri.gob.ec;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "AutorizacionComprobantesOfflineService", targetNamespace = "http://ec.gob.sri.ws.autorizacion", wsdlLocation = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl")
public class AutorizacionComprobantesOfflineService
    extends Service
{

    private final static URL AUTORIZACIONCOMPROBANTESOFFLINESERVICE_WSDL_LOCATION;
    private final static WebServiceException AUTORIZACIONCOMPROBANTESOFFLINESERVICE_EXCEPTION;
    private final static QName AUTORIZACIONCOMPROBANTESOFFLINESERVICE_QNAME = new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        AUTORIZACIONCOMPROBANTESOFFLINESERVICE_WSDL_LOCATION = url;
        AUTORIZACIONCOMPROBANTESOFFLINESERVICE_EXCEPTION = e;
    }

    public AutorizacionComprobantesOfflineService() {
        super(__getWsdlLocation(), AUTORIZACIONCOMPROBANTESOFFLINESERVICE_QNAME);
    }

    public AutorizacionComprobantesOfflineService(URL wsdlLocation) {
        super(wsdlLocation, AUTORIZACIONCOMPROBANTESOFFLINESERVICE_QNAME);
    }

    public AutorizacionComprobantesOfflineService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    @WebEndpoint(name = "AutorizacionComprobantesOfflinePort")
    public AutorizacionComprobantesOffline getAutorizacionComprobantesOfflinePort() {
        return super.getPort(new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflinePort"), AutorizacionComprobantesOffline.class);
    }

    @WebEndpoint(name = "AutorizacionComprobantesOfflinePort")
    public AutorizacionComprobantesOffline getAutorizacionComprobantesOfflinePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflinePort"), AutorizacionComprobantesOffline.class, features);
    }

    private static URL __getWsdlLocation() {
        if (AUTORIZACIONCOMPROBANTESOFFLINESERVICE_EXCEPTION!= null) {
            throw AUTORIZACIONCOMPROBANTESOFFLINESERVICE_EXCEPTION;
        }
        return AUTORIZACIONCOMPROBANTESOFFLINESERVICE_WSDL_LOCATION;
    }

}