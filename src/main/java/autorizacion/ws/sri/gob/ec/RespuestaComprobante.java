package autorizacion.ws.sri.gob.ec;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@XmlType(name = "respuestaComprobante", propOrder = {
    "claveAccesoConsultada",
    "numeroComprobantes",
    "autorizaciones"
})
public class RespuestaComprobante {

    protected String claveAccesoConsultada;
    protected String numeroComprobantes;
    protected RespuestaComprobante.Autorizaciones autorizaciones;

    public String getClaveAccesoConsultada() {
        return claveAccesoConsultada;
    }

    public void setClaveAccesoConsultada(String value) {
        this.claveAccesoConsultada = value;
    }

    public String getNumeroComprobantes() {
        return numeroComprobantes;
    }

    public void setNumeroComprobantes(String value) {
        this.numeroComprobantes = value;
    }

    public RespuestaComprobante.Autorizaciones getAutorizaciones() {
        return autorizaciones;
    }

    public void setAutorizaciones(RespuestaComprobante.Autorizaciones value) {
        this.autorizaciones = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "autorizacion"
    })
    public static class Autorizaciones {

        protected List<Autorizacion> autorizacion;

        public List<Autorizacion> getAutorizacion() {
            if (autorizacion == null) {
                autorizacion = new ArrayList<Autorizacion>();
            }
            return this.autorizacion;
        }

    }

}