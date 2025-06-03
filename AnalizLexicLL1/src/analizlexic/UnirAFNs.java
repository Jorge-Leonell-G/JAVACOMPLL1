package analizlexic;

import java.util.List;

public class UnirAFNs {

    /**
     * Une dos AFNs utilizando el operador OR de Thompson.
     *
     * @param afn1 El primer AFN.
     * @param afn2 El segundo AFN.
     * @param nuevoId El ID del AFN resultante.
     * @param tokens Los tokens correspondientes a los AFNs.
     * @return El AFN resultante de la unión.
     */
    public static AFN unirAFNs(AFN afn1, AFN afn2, int nuevoId, List<Integer> tokens) {
        // Verificamos que los dos AFNs no sean null
        if (afn1 == null || afn2 == null) {
            throw new IllegalArgumentException("Ambos AFNs deben ser no nulos.");
        }

        // Creamos un nuevo AFN para el resultado de la unión
        AFN afnResultante = new AFN();
        afnResultante.setIdAFN(nuevoId);

        // Creamos el nuevo estado inicial para el AFN resultante
        Estado estadoInicial = new Estado();
        afnResultante.setEdoInicial(estadoInicial);

        // Creamos el estado de aceptación final
        Estado estadoFinal = new Estado(true, 0);  // El token será 0 para el estado final de aceptación
        afnResultante.setSimbolos('\0'); // Usamos transiciones epsilon '\0'

        // Conectamos el nuevo estado inicial con los estados iniciales de ambos AFNs
        estadoInicial.agregarTransicion(new Transicion('\0', '\0', afn1.getEdoInicial())); // Conexión a afn1
        estadoInicial.agregarTransicion(new Transicion('\0', '\0', afn2.getEdoInicial())); // Conexión a afn2

        // Conectamos los estados de aceptación de ambos AFNs al nuevo estado de aceptación
        for (Estado e : afn1.getEdosAceptacion()) {
            e.agregarTransicion(new Transicion('\0', '\0', estadoFinal)); // Conexión de afn1
            e.setEdoAceptacion(false);  // Desmarcamos como estado de aceptación
        }

        for (Estado e : afn2.getEdosAceptacion()) {
            e.agregarTransicion(new Transicion('\0', '\0', estadoFinal)); // Conexión de afn2
            e.setEdoAceptacion(false);  // Desmarcamos como estado de aceptación
        }

        // Añadimos los estados aceptados a la lista de estados aceptacion
        afnResultante.setEdosAcept(estadoFinal);
        afnResultante.setEdoAFN(estadoInicial);
        afnResultante.setEdoAFN(estadoFinal);

        // Añadimos todos los estados de ambos AFNs al nuevo AFN
        afnResultante.getEdosAFN().addAll(afn1.getEdosAFN());
        afnResultante.getEdosAFN().addAll(afn2.getEdosAFN());

        // Si se proporcionan tokens, asignamos los tokens a los estados de aceptación de los AFNs
        if (tokens != null && tokens.size() == 2) {
            int token1 = tokens.get(0);
            int token2 = tokens.get(1);

            for (Estado e : afn1.getEdosAceptacion()) {
                e.setToken(token1);  // Asignamos el token del primer AFN
            }

            for (Estado e : afn2.getEdosAceptacion()) {
                e.setToken(token2);  // Asignamos el token del segundo AFN
            }
        }

        return afnResultante;  // Retornamos el nuevo AFN resultante
    }
}
