package analizlexic;

import java.io.*;
import java.util.*;

public class AFD implements SerializableSupport, Serializable {
    private static final long serialVersionUID = 1L;

    private int idAFD;
    private Estado estadoInicial;
    private Set<Estado> estados = new HashSet<>();
    private Set<Estado> estadosAceptacion = new HashSet<>();
    private Map<Estado, Map<String, Estado>> tablaDeTransiciones = new HashMap<>();

    public AFD() {}

    // Getters y Setters
    public int getIdAFD() {
        return idAFD;
    }

    public void setIdAFD(int idAFD) {
        this.idAFD = idAFD;
    }

    public Estado getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(Estado estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public Set<Estado> getEstados() {
        return estados;
    }

    public Set<Estado> getEstadosAceptacion() {
        return estadosAceptacion;
    }

    public Map<Estado, Map<String, Estado>> getTablaDeTransiciones() {
        return tablaDeTransiciones;
    }

    public void agregarEstado(Estado estado) {
        estados.add(estado);
    }

    public void agregarEstadoAceptacion(Estado estado) {
        estadosAceptacion.add(estado);
    }

    public void agregarTransicion(Estado origen, String simbolo, Estado destino) {
        tablaDeTransiciones
            .computeIfAbsent(origen, k -> new HashMap<>())
            .put(simbolo, destino);
    }

    public Estado getEstadoDestino(Estado origen, String simbolo) {
        return tablaDeTransiciones.getOrDefault(origen, Collections.emptyMap()).get(simbolo);
    }

    public void guardarComoBinario(String ruta) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(this);
            System.out.println("✅ AFD guardado como archivo binario: " + ruta);
        } catch (IOException e) {
            System.err.println("❌ Error al guardar el AFD: " + e.getMessage());
        }
    }

    public static AFD leerDesdeBinario(String ruta) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ruta))) {
            return (AFD) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error al cargar el AFD: " + e.getMessage());
            return null;
        }
    }

    public boolean simular(String cadena) {
        Estado actual = this.estadoInicial;

        for (int i = 0; i < cadena.length(); i++) {
            String simbolo = String.valueOf(cadena.charAt(i));
            if (!tablaDeTransiciones.containsKey(actual)) return false;

            Map<String, Estado> transiciones = tablaDeTransiciones.get(actual);
            if (!transiciones.containsKey(simbolo)) return false;

            actual = transiciones.get(simbolo);
        }

        return estadosAceptacion.contains(actual);
    }

    public List<Simbolo> analizarCadena(String cadena) {
        List<Simbolo> resultado = new ArrayList<>();
        int i = 0;

        while (i < cadena.length()) {
            Estado actual = this.estadoInicial;
            StringBuilder lexema = new StringBuilder();
            Estado ultimoEstadoAceptacion = null;
            int ultimaPosicionAceptada = i;

            int j = i;
            while (j < cadena.length()) {
                String simbolo = String.valueOf(cadena.charAt(j));

                if (!tablaDeTransiciones.containsKey(actual) || !tablaDeTransiciones.get(actual).containsKey(simbolo)) {
                    break;
                }

                actual = tablaDeTransiciones.get(actual).get(simbolo);
                lexema.append(cadena.charAt(j));

                if (estadosAceptacion.contains(actual)) {
                    ultimoEstadoAceptacion = actual;
                    ultimaPosicionAceptada = j + 1;
                }

                j++;
            }

            if (ultimoEstadoAceptacion != null) {
                Simbolo simboloToken = new Simbolo(
                        lexema.toString(),
                        ultimoEstadoAceptacion.getToken(),
                        1, 1,
                        "Token"
                );
                resultado.add(simboloToken);
                i = ultimaPosicionAceptada;
            } else {
                resultado.add(new Simbolo("ERROR: '" + cadena.charAt(i) + "'", -1, 1, 1, "Error"));
                i++;
            }
        }

        return resultado;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Estado, Map<String, Estado>> fila : tablaDeTransiciones.entrySet()) {
            Estado origen = fila.getKey();
            for (Map.Entry<String, Estado> trans : fila.getValue().entrySet()) {
                sb.append(origen.getIdEdo()).append(" ")
                        .append(trans.getKey()).append(" ")
                        .append(trans.getValue().getIdEdo()).append("\n");
            }
        }

        if (estadoInicial != null) {
            sb.append("inicio ").append(estadoInicial.getIdEdo()).append("\n");
        }

        for (Estado estado : estadosAceptacion) {
            sb.append("aceptar ").append(estado.getIdEdo()).append("\n");
        }

        return sb.toString();
    }
}
