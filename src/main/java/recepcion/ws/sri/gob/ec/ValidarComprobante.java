package recepcion.ws.sri.gob.ec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validarComprobante", propOrder = {
    "xml"
})
public class ValidarComprobante {

    protected byte[] xml;

    public byte[] getXml() {
        return xml;
    }

    public void setXml(byte[] value) {
        this.xml = value;
    }

}