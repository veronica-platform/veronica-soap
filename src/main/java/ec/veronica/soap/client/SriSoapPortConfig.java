package ec.veronica.soap.client;

/**
 * Configuración común para un port SOAP del SRI (endpoint y timeouts).
 * Permite reutilizar la lógica de configuración entre Recepción y Autorización.
 */
public interface SriSoapPortConfig {

    String endpoint();

    int connectTimeoutMs();

    int readTimeoutMs();
}
