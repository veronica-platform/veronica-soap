package ec.veronica.soap.client;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import recepcion.ws.sri.gob.ec.RecepcionComprobantesOffline;
import recepcion.ws.sri.gob.ec.RespuestaSolicitud;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class SriRecepcionClientIT {

    @Test
    void shouldCreateSoapClientUsingPruebasWsdl() {
        // Arrange
        SriRecepcionConfig config = SriRecepcionConfig.builder()
                .endpoint("https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl")
                .connectTimeoutMs(15_000)
                .readTimeoutMs(30_000)
                .build();

        // Act
        SriRecepcionClient client = new SriRecepcionClient(SriEnvironment.PRUEBAS, config);

        RecepcionComprobantesOffline port = client.port();

        // Assert
        assertNotNull(client, "El client no debe ser null");
        assertNotNull(port, "El port SOAP no debe ser null");

        assertTrue(port instanceof BindingProvider, "El port debe implementar BindingProvider");

        BindingProvider bp = (BindingProvider) port;
        Map<String, Object> ctx = bp.getRequestContext();

        assertEquals(
                config.endpoint(),
                ctx.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY),
                "El endpoint debe venir de la configuración"
        );

        assertEquals(
                config.connectTimeoutMs(),
                ctx.get("com.sun.xml.ws.connect.timeout")
        );

        assertEquals(
                config.readTimeoutMs(),
                ctx.get("com.sun.xml.ws.request.timeout")
        );
    }

    @Test
    void shouldReuseServiceFromCacheForSameEnvironment() {
        // Arrange
        SriRecepcionConfig config = SriRecepcionConfig.builder()
                .endpoint("https://example.com/RecepcionComprobantesOfflineService")
                .build();

        // Act
        SriRecepcionClient client1 =
                new SriRecepcionClient(SriEnvironment.PRUEBAS, config);

        SriRecepcionClient client2 =
                new SriRecepcionClient(SriEnvironment.PRUEBAS, config);

        // Assert
        assertSame(
                getService(client1),
                getService(client2),
                "Debe reutilizar la misma instancia de Service desde el cache"
        );
    }

    @Test
    @Tag("external")
    void validarComprobante_shouldCallRealCelcerService() {
        // Para evitar que falle en CI sin red, habilítalo explícitamente:
        // -Dsri.it.enabled=true
        boolean enabled = Boolean.parseBoolean(System.getProperty("sri.it.enabled", "false"));
        Assumptions.assumeTrue(enabled, "IT externo deshabilitado. Usa -Dsri.it.enabled=true");

        String endpoint = System.getProperty(
                "sri.pruebas.endpoint",
                "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline"
        );

        SriRecepcionConfig cfg = SriRecepcionConfig.builder()
                .endpoint(endpoint)
                .connectTimeoutMs(Integer.parseInt(System.getProperty("sri.it.connectTimeoutMs", "15000")))
                .readTimeoutMs(Integer.parseInt(System.getProperty("sri.it.readTimeoutMs", "30000")))
                .build();

        SriRecepcionClient client = new SriRecepcionClient(SriEnvironment.PRUEBAS, cfg);
        RecepcionComprobantesOffline port = client.port();

        // Payload intencionalmente inválido para evitar "enviar un comprobante real"
        byte[] invalidXml = loadResourceAsBytes("xml/outdated_invoice.xml");

        try {
            RespuestaSolicitud resp = port.validarComprobante(invalidXml);

            // Si hay respuesta, hubo round-trip SOAP real ✅
            assertNotNull(resp, "El servicio debe devolver RespuestaSolicitud (aunque sea DEVUELTA).");
            assertNotNull(resp.getEstado(), "El estado no debe ser null.");
            assertNotNull(resp.getComprobantes());
            assertNotNull(resp.getComprobantes().getComprobante());
            assertNotNull(resp.getComprobantes().getComprobante().get(0));
            assertNotNull(resp.getComprobantes().getComprobante().get(0).getMensajes());

            // Normalmente será "DEVUELTA" para XML inválido; no lo fuerzo por si cambia el comportamiento.
            System.out.println("SRI estado: " + resp.getEstado());
            System.out.println("SRI # de comprobantes: " + resp.getComprobantes().getComprobante().size());
            System.out.println("SRI comprobante: " + resp.getComprobantes().getComprobante().get(0).getMensajes().getMensaje());

        } catch (SOAPFaultException fault) {
            // También cuenta como llamada exitosa (el servidor respondió con Fault) ✅
            assertNotNull(fault.getFault(), "Debe existir fault en SOAPFaultException.");
            System.out.println("SOAP Fault: " + fault.getFault().getFaultString());
        } catch (WebServiceException wse) {
            // Esto ya es problema de transporte (DNS/TLS/timeout/conexión) => fallo del IT
            fail("Falló la invocación por transporte/red. Endpoint=" + endpoint + ". Error=" + wse, wse);
        }
    }

    // -----------------------
    // Helpers
    // -----------------------

    /**
     * Acceso por reflection al field "service" para validar el cache.
     */
    private Object getService(SriRecepcionClient client) {
        try {
            java.lang.reflect.Field f =
                    SriRecepcionClient.class.getDeclaredField("service");
            f.setAccessible(true);
            return f.get(client);
        } catch (Exception e) {
            throw new AssertionError("No se pudo acceder al Service interno", e);
        }
    }

    private static byte[] loadResourceAsBytes(String classpathLocation) {
        try (java.io.InputStream is =
                     Thread.currentThread()
                             .getContextClassLoader()
                             .getResourceAsStream(classpathLocation)) {

            if (is == null) {
                throw new IllegalStateException(
                        "Resource not found in classpath: " + classpathLocation
                );
            }

            java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
            byte[] data = new byte[4096];
            int nRead;
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error reading resource: " + classpathLocation, e
            );
        }
    }
}
