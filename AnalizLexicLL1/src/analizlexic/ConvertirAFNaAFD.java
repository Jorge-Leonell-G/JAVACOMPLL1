package analizlexic;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Clase que convierte un AFN a un AFD utilizando el método del subconjunto (powerset).
 */
public class ConvertirAFNaAFD {

    private final AFN afn;
    private final List<String[]> tablaTransiciones = new ArrayList<>();

    public ConvertirAFNaAFD(AFN afn) {
        this.afn = afn;
    }

    public AFD convertir() {
        Map<Set<Estado>, Estado> conjuntosMap = new HashMap<>();
        Queue<Set<Estado>> pendientes = new LinkedList<>();
        AFD afd = new AFD();
        int contador = 0;

        Set<Estado> estadoInicial = cerraduraEpsilon(Set.of(afn.getEdoInicial()));
        Estado estadoAFDInicial = new Estado();
        estadoAFDInicial.setIdEdo(contador++);

        if (esFinal(estadoInicial)) {
            estadoAFDInicial.setEdoAceptacion(true);
            estadoAFDInicial.setToken(obtenerToken(estadoInicial));
            afd.agregarEstadoAceptacion(estadoAFDInicial);
        }

        afd.setEstadoInicial(estadoAFDInicial);
        afd.agregarEstado(estadoAFDInicial);
        conjuntosMap.put(estadoInicial, estadoAFDInicial);
        pendientes.add(estadoInicial);

        while (!pendientes.isEmpty()) {
            Set<Estado> actual = pendientes.poll();
            Estado estadoDesde = conjuntosMap.get(actual);

            for (char simbolo = 0; simbolo <= 255; simbolo++) {
                Set<Estado> mover = mover(actual, simbolo);
                if (mover.isEmpty()) continue;

                Set<Estado> cerradura = cerraduraEpsilon(mover);

                Estado estadoDestino = conjuntosMap.get(cerradura);
                if (estadoDestino == null) {
                    estadoDestino = new Estado();
                    estadoDestino.setIdEdo(contador++);

                    if (esFinal(cerradura)) {
                        estadoDestino.setEdoAceptacion(true);
                        estadoDestino.setToken(obtenerToken(cerradura)); // ✅ Asigna token correcto
                        afd.agregarEstadoAceptacion(estadoDestino);
                    }

                    conjuntosMap.put(cerradura, estadoDestino);
                    afd.agregarEstado(estadoDestino);
                    pendientes.add(cerradura);
                }

                afd.agregarTransicion(estadoDesde, String.valueOf(simbolo), estadoDestino);

                tablaTransiciones.add(new String[]{
                        String.valueOf(estadoDesde.getIdEdo()),
                        String.valueOf((int) simbolo),
                        String.valueOf(estadoDestino.getIdEdo())
                });
            }
        }

        return afd;
    }

    public List<String[]> getTransicionesTabla() {
        return tablaTransiciones;
    }

    public void guardarAFDenArchivo(AFD afd, String ruta) {
        try (FileWriter writer = new FileWriter(ruta)) {
            for (Estado origen : afd.getTablaDeTransiciones().keySet()) {
                Map<String, Estado> transiciones = afd.getTablaDeTransiciones().get(origen);
                for (String simbolo : transiciones.keySet()) {
                    Estado destino = transiciones.get(simbolo);
                    writer.write(origen.getIdEdo() + "," + simbolo + "," + destino.getIdEdo() + "\n");
                }
            }

            for (Estado estado : afd.getEstadosAceptacion()) {
                writer.write("F," + estado.getIdEdo() + "," + estado.getToken() + "\n");
            }

        } catch (IOException e) {
            System.err.println("Error al guardar AFD: " + e.getMessage());
        }
    }

    private Set<Estado> mover(Set<Estado> estados, char simbolo) {
        Set<Estado> resultado = new HashSet<>();
        for (Estado estado : estados) {
            for (Transicion t : estado.getTransiciones()) {
                if (t.getSimbInf() <= simbolo && simbolo <= t.getSimbSup()) {
                    resultado.add(t.getEdo());
                }
            }
        }
        return resultado;
    }

    private Set<Estado> cerraduraEpsilon(Set<Estado> estados) {
        Stack<Estado> pila = new Stack<>();
        Set<Estado> resultado = new HashSet<>(estados);
        pila.addAll(estados);

        while (!pila.isEmpty()) {
            Estado actual = pila.pop();
            for (Transicion t : actual.getTransiciones()) {
                if (t.getSimbInf() == '\0' && t.getSimbSup() == '\0') {
                    Estado destino = t.getEdo();
                    if (!resultado.contains(destino)) {
                        resultado.add(destino);
                        pila.push(destino);
                    }
                }
            }
        }

        return resultado;
    }

    private boolean esFinal(Set<Estado> estados) {
        return estados.stream().anyMatch(Estado::getEdoAceptacion);
    }

    private int obtenerToken(Set<Estado> estados) {
        return estados.stream()
                .filter(Estado::getEdoAceptacion)
                .map(Estado::getToken)
                .filter(t -> t > 0 && t != 20001)
                .min(Integer::compareTo)
                .orElse(20001); // token por defecto si no hay otro válido
    }
}
