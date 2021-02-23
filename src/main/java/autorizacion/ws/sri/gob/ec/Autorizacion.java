package autorizacion.ws.sri.gob.ec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autorizacion", propOrder = {
        "estado",
        "numeroAutorizacion",
        "fechaAutorizacion",
        "ambiente",
        "comprobante",
        "mensajes"
})
public class Autorizacion {

    protected String estado;
    protected String numeroAutorizacion;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaAutorizacion;
    protected String ambiente;

    @XmlJavaTypeAdapter(AdapterCDATA.class)
    protected String comprobante;

    protected Autorizacion.Mensajes mensajes;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String value) {
        this.estado = value;
    }

    public String getNumeroAutorizacion() {
        return numeroAutorizacion;
    }

    public void setNumeroAutorizacion(String value) {
        this.numeroAutorizacion = value;
    }

    public XMLGregorianCalendar getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(XMLGregorianCalendar value) {
        this.fechaAutorizacion = value;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String value) {
        this.ambiente = value;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String value) {
        this.comprobante = value;
    }

    public Autorizacion.Mensajes getMensajes() {
        return mensajes;
    }

    public void setMensajes(Autorizacion.Mensajes value) {
        this.mensajes = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "mensaje"
    })
    public static class Mensajes {

        protected List<Mensaje> mensaje;

        public List<Mensaje> getMensaje() {
            if (mensaje == null) {
                mensaje = new ArrayList<Mensaje>();
            }
            return this.mensaje;
        }

    }

}