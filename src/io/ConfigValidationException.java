package io;

/**
 * Excepción lanzada cuando el archivo de configuración JSON es sintácticamente válido
 * pero viola alguna regla del modelo de LogísTEC (por ejemplo: una arista referencia un
 * vértice inexistente, un paquete tiene prioridad fuera de {1,2,3}, o el depósito no existe).
 *
 * <p>Es una excepción verificada (checked) para forzar a quien carga el caso a manejar
 * explícitamente los errores de configuración y reportarlos al usuario.</p>
 */
public class ConfigValidationException extends Exception {

    public ConfigValidationException(String message) {
        super(message);
    }

    public ConfigValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
