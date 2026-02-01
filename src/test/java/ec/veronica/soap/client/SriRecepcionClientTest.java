package ec.veronica.soap.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.handler.MessageContext;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SriRecepcionClientTest {

    @BeforeEach
    void resetStaticCache() throws Exception {
        Field cacheField = SriRecepcionClient.class.getDeclaredField("SERVICE_CACHE");
        cacheField.setAccessible(true);

        @SuppressWarnings("unchecked")
        ConcurrentHashMap<SriEnvironment, Object> cache =
                (ConcurrentHashMap<SriEnvironment, Object>) cacheField.get(null);

        cache.clear();
    }

    @Test
    void configurePort_setsEndpointTimeoutsAndHeaders() throws Exception {
        // Arrange
        SriRecepcionConfig cfg = SriRecepcionConfig.builder()
                .endpoint("https://example.com/RecepcionComprobantesOfflineService")
                .connectTimeoutMs(1234)
                .readTimeoutMs(5678)
                .build();

        DummyBindingProvider dummyPort = new DummyBindingProvider();

        // Act: invocar private static configurePort(Object, SriRecepcionConfig)
        Method m = SriRecepcionClient.class.getDeclaredMethod(
                "configurePort", Object.class, SriRecepcionConfig.class
        );
        m.setAccessible(true);
        m.invoke(null, dummyPort, cfg);

        // Assert
        Map<String, Object> ctx = dummyPort.getRequestContext();

        assertEquals(cfg.endpoint(), ctx.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
        assertEquals(cfg.connectTimeoutMs(), ctx.get("com.sun.xml.ws.connect.timeout"));
        assertEquals(cfg.readTimeoutMs(), ctx.get("com.sun.xml.ws.request.timeout"));

        assertTrue(ctx.containsKey(MessageContext.HTTP_REQUEST_HEADERS));

        Object headersObj = ctx.get(MessageContext.HTTP_REQUEST_HEADERS);
        assertNotNull(headersObj);
        assertTrue(headersObj instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, List<String>> headers = (Map<String, List<String>>) headersObj;
        assertNotNull(headers);
        assertTrue(headers.isEmpty(), "Por defecto se setea un Map vacío de headers.");
    }

    @Test
    void getOrCreateCachedService_returnsSameInstanceForSameEnvironment() throws Exception {
        // Act: invocar private static getOrCreateCachedService(SriEnvironment)
        Method m = SriRecepcionClient.class.getDeclaredMethod(
                "getOrCreateCachedService", SriEnvironment.class
        );
        m.setAccessible(true);

        Object s1 = m.invoke(null, SriEnvironment.PRUEBAS);
        Object s2 = m.invoke(null, SriEnvironment.PRUEBAS);
        Object s3 = m.invoke(null, SriEnvironment.PRODUCCION);

        // Assert
        assertSame(s1, s2, "Debe retornar la misma instancia para el mismo env (cache).");
        assertNotSame(s1, s3, "Ambientes distintos deben tener instancias distintas.");

        // Validar tamaño del cache = 2
        Field cacheField = SriRecepcionClient.class.getDeclaredField("SERVICE_CACHE");
        cacheField.setAccessible(true);

        @SuppressWarnings("unchecked")
        ConcurrentHashMap<SriEnvironment, Object> cache =
                (ConcurrentHashMap<SriEnvironment, Object>) cacheField.get(null);

        assertEquals(2, cache.size());
        assertTrue(cache.containsKey(SriEnvironment.PRUEBAS));
        assertTrue(cache.containsKey(SriEnvironment.PRODUCCION));
    }

    /**
     * Stub sin Mockito: suficiente para probar configurePort().
     */
    static final class DummyBindingProvider implements BindingProvider {
        private final Map<String, Object> requestContext = new HashMap<String, Object>();
        private final Map<String, Object> responseContext = new HashMap<String, Object>();

        @Override
        public Map<String, Object> getRequestContext() {
            return requestContext;
        }

        @Override
        public Map<String, Object> getResponseContext() {
            return responseContext;
        }

        @Override
        public Binding getBinding() {
            return null;
        }

        @Override
        public EndpointReference getEndpointReference() {
            return null;
        }

        @Override
        public <T extends EndpointReference> T getEndpointReference(Class<T> clazz) {
            return null;
        }
    }
}