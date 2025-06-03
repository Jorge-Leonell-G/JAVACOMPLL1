package analizlexic;

import java.util.List;

/**
 * Clase encargada de unir múltiples AFNs en uno solo,
 * utilizando el método de Thompson (operador OR global).
 */
public class UnionLexico {

    /**
     * Une múltiples AFNs en uno solo mediante transiciones lambda desde un nuevo estado inicial.
     *
     * @param afnsSeleccionados Lista de AFNs a unir.
     * @param nuevoIdAFN        ID que se asignará al AFN resultante.
     * @param tokens            Lista de tokens asociados a cada AFN.
     * @return AFN resultado de la unión.
     * @throws IllegalArgumentException si hay menos de 2 AFNs o si los datos son inconsistentes.
     */
    public static AFN unirAFNs(List<AFN> afnsSeleccionados, int nuevoIdAFN, List<Integer> tokens) {
        if (afnsSeleccionados == null || afnsSeleccionados.size() < 2) {
            throw new IllegalArgumentException("⚠️ Se requieren al menos 2 AFNs para unir.");
        }

        if (tokens != null && tokens.size() != afnsSeleccionados.size()) {
            throw new IllegalArgumentException("⚠️ La cantidad de tokens no coincide con los AFNs.");
        }

        // Asignar tokens únicos a cada estado de aceptación de su respectivo AFN
        for (int i = 0; i < afnsSeleccionados.size(); i++) {
            AFN afn = afnsSeleccionados.get(i);
            int token = (tokens != null) ? tokens.get(i) : 20001;
            afn.setToken(token); // Guarda el token en el AFN

            for (Estado e : afn.getEdosAceptacion()) {
                e.setToken(token);
                e.setEdoAceptacion(true);
            }
        }

        // Crear nuevo estado inicial que apunta con transiciones lambda a todos los iniciales
        AFN resultado = new AFN();
        Estado nuevoInicial = new Estado();
        resultado.setEdoInicial(nuevoInicial);
        resultado.setIdAFN(nuevoIdAFN);
        resultado.setEdoAFN(nuevoInicial); // también lo agrega a la lista

        for (AFN afn : afnsSeleccionados) {
            // Transición lambda desde nuevo inicial al inicial de cada AFN
            nuevoInicial.agregarTransicion(new Transicion('\0', '\0', afn.getEdoInicial()));

            resultado.getEdosAFN().addAll(afn.getEdosAFN());
            resultado.getAlfabeto().addAll(afn.getAlfabeto());
        }

        // Asegurar que los estados de aceptación de resultado tengan los tokens correctos
        resultado.getEdosAceptacion().clear();
        for (AFN afn : afnsSeleccionados) {
            for (Estado e : afn.getEdosAceptacion()) {
                e.setEdoAceptacion(true); // reforzar
                resultado.getEdosAceptacion().add(e);
            }
        }

        return resultado;
    }
}
