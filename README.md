# veronica-soap

Cliente SOAP para los servicios del SRI (Servicio de Rentas Internas del Ecuador): **Recepción** y **Autorización** de comprobantes electrónicos.

El WSDL se carga desde el classpath (archivos embebidos en la librería), no desde la red. Así la aplicación **arranca aunque el SRI esté caído**; la conexión al SRI solo ocurre cuando se invoca una operación.

## Requisitos

- Java 8+
- JAX-WS (incluido en el JDK)

## Uso

### Recepción de comprobantes

Validar un comprobante (envío al SRI):

```java
import ec.veronica.soap.client.SriEnvironment;
import ec.veronica.soap.client.SriRecepcionClient;
import recepcion.ws.sri.gob.ec.RespuestaSolicitud;

// Con configuración por defecto del ambiente
SriRecepcionClient client = SriRecepcionClient.forEnv(SriEnvironment.PRUEBAS);

byte[] xmlComprobante = ...; // XML del comprobante
RespuestaSolicitud respuesta = client.validarComprobante(xmlComprobante);
```

Con configuración personalizada (endpoint, timeouts):

```java
SriRecepcionConfig config = SriRecepcionConfig.builder()
    .endpoint("https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline")
    .connectTimeoutMs(15_000)
    .readTimeoutMs(30_000)
    .build();

SriRecepcionClient client = new SriRecepcionClient(SriEnvironment.PRUEBAS, config);
RespuestaSolicitud respuesta = client.validarComprobante(xmlComprobante);
```

### Autorización de comprobantes

Consultar autorización por clave de acceso:

```java
import ec.veronica.soap.client.SriAutorizacionClient;
import autorizacion.ws.sri.gob.ec.RespuestaComprobante;

SriAutorizacionClient client = SriAutorizacionClient.forEnv(SriEnvironment.PRODUCCION);

String claveAcceso = "...";
RespuestaComprobante respuesta = client.autorizacionIndividual(claveAcceso);
```

Acceso al port para otras operaciones (p. ej. autorización por lote):

```java
AutorizacionComprobantesOffline port = client.port();
// port.autorizacionComprobanteLote(...)
```

### Spring

Ejemplo de beans:

```java
@Configuration
public class SriConfig {

    @Bean
    public SriRecepcionClient sriRecepcionClient(@Value("${sri.env:PRUEBAS}") SriEnvironment env) {
        return SriRecepcionClient.forEnv(env);
    }

    @Bean
    public SriAutorizacionClient sriAutorizacionClient(@Value("${sri.env:PRUEBAS}") SriEnvironment env) {
        return SriAutorizacionClient.forEnv(env);
    }
}
```

## Ambientes

- `SriEnvironment.PRUEBAS` — certificación (celcer.sri.gob.ec)
- `SriEnvironment.PRODUCCION` — producción (cel.sri.gob.ec)

Los defaults de endpoint y timeouts por ambiente están en `SriRecepcionDefaults` y `SriAutorizacionDefaults`. Puedes usar los builders de `SriRecepcionConfig` y `SriAutorizacionConfig` para sobrescribirlos.

## Comportamiento cuando el SRI está caído

- **Arranque:** la aplicación inicia con normalidad. No se hace ninguna petición HTTP al SRI al crear los clientes (el WSDL se lee desde recursos).
- **En la llamada:** al ejecutar `validarComprobante(...)` o `autorizacionIndividual(...)` se realiza la conexión. Si el SRI no responde, obtendrás excepciones de red/timeout (p. ej. `javax.xml.ws.WebServiceException`, `SocketTimeoutException`). Los timeouts son configurables en la config para no bloquear indefinidamente.

Para mayor resiliencia (reintentos, circuit breaker), implementa esa lógica en la aplicación que usa esta librería.

## Migración desde un cliente que usaba el WSDL remoto

Si antes tenías algo así:

- `new RecepcionComprobantesOfflineService()` (constructor por defecto), o
- `new RecepcionComprobantesOfflineService(remoteWsdlUrl)`,

el arranque fallaba cuando el SRI estaba caído porque JAX-WS descargaba el WSDL por red.

**Solución:** usar solo los clientes de esta librería (`SriRecepcionClient`, `SriAutorizacionClient`) y no instanciar directamente `RecepcionComprobantesOfflineService` ni `AutorizacionComprobantesOfflineService` con URL remota o sin argumentos. Los clientes de veronica-soap cargan el WSDL desde el classpath y configuran el endpoint en tiempo de ejecución.

## Actualizar los WSDL

Si el SRI publica nuevas versiones de los WSDL:

1. Descarga los WSDL desde el SRI (recepción y/o autorización, pruebas y/o producción).
2. Reemplaza los archivos en `src/main/resources/wsdl/pruebas/` y `src/main/resources/wsdl/produccion/` (p. ej. `RecepcionComprobantesOffline.xml`, `AutorizacionComprobantesOffline.xml`).
3. Ajusta si hace falta la URL del servicio en el `<soap:address location="..."/>` dentro del WSDL, o deja que la aplicación la sobrescriba vía config (esta librería usa `BindingProvider.ENDPOINT_ADDRESS_PROPERTY`).
4. Si cambian operaciones o tipos, regenera las clases JAX-WS y actualiza la librería.

## Tests

### Unitarios

```bash
mvn test
```

### Integración (llamadas reales al SRI)

```bash
mvn -Dsri.it.enabled=true -Dtest=SriRecepcionClientIT test
```

## Licencia

Ver repositorio / proyecto.
