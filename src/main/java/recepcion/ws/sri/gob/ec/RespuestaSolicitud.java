package recepcion.ws.sri.gob.ec;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuestaSolicitud", propOrder = {
    "estado",
    "comprobantes"
})
public class RespuestaSolicitud {

    protected String estado;
    protected RespuestaSolicitud.Comprobantes comprobantes;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String value) {
        this.estado = value;
    }

    public RespuestaSolicitud.Comprobantes getComprobantes() {
        return comprobantes;
    }

    public void setComprobantes(RespuestaSolicitud.Comprobantes value) {
        this.comprobantes = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "comprobante"
    })
    public static class Comprobantes {

        protected List<Comprobante> comprobante;

        public List<Comprobante> getComprobante() {
            if (comprobante == null) {
                comprobante = new ArrayList<Comprobante>();
            }
            return this.comprobante;
        }

    }

}