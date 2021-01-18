package recepcion.ws.sri.gob.ec;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name = "RecepcionComprobantesOffline", targetNamespace = "http://ec.gob.sri.ws.recepcion")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface RecepcionComprobantesOffline {

    @WebMethod
    @WebResult(name = "RespuestaRecepcionComprobante", targetNamespace = "")
    @RequestWrapper(localName = "validarComprobante", targetNamespace = "http://ec.gob.sri.ws.recepcion", className = "recepcion.ws.sri.gob.ec.ValidarComprobante")
    @ResponseWrapper(localName = "validarComprobanteResponse", targetNamespace = "http://ec.gob.sri.ws.recepcion", className = "recepcion.ws.sri.gob.ec.ValidarComprobanteResponse")
    public RespuestaSolicitud validarComprobante(
        @WebParam(name = "xml", targetNamespace = "")
        byte[] xml);

}