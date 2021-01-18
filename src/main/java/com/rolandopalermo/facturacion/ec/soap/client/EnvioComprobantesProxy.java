package com.rolandopalermo.facturacion.ec.soap.client;

import recepcion.ws.sri.gob.ec.RecepcionComprobantesOffline;
import recepcion.ws.sri.gob.ec.RecepcionComprobantesOfflineService;
import recepcion.ws.sri.gob.ec.RespuestaSolicitud;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

public class EnvioComprobantesProxy {

    private RecepcionComprobantesOffline port;
    private static RecepcionComprobantesOfflineService service;

    public EnvioComprobantesProxy(String wsdlLocation) throws MalformedURLException {
        URL url = new URL(wsdlLocation);
        QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
        service = new RecepcionComprobantesOfflineService(url, qname);
        port = service.getRecepcionComprobantesOfflinePort();
    }

    public RespuestaSolicitud enviarComprobante(byte[] archivoBytes) {
        return port.validarComprobante(archivoBytes);
    }

}