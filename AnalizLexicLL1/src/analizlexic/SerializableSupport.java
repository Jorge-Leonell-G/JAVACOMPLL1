package analizlexic;

import java.io.Serializable;

/**
 * Interfaz de marcador para permitir la serialización de clases.
 * Al implementar esta interfaz, las clases pueden ser guardadas y cargadas desde archivos binarios.
 */
public interface SerializableSupport extends Serializable {
    // No contiene métodos. Sirve solo para marcar las clases serializables.
}
