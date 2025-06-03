package analizlexic;

import java.util.*;

public class AnalizLexico {
    private int estadoActual;
    private String cadenaEntrada;
    public int indiceCaracterActual;
    private AFD automataAFD;
    private int numeroLinea;
    private int columnaInicio;
    private String lexema;
    public List<Simbolo> simbolos;
    private Stack<Integer> pilaIndices;

    public AnalizLexico() {
        this.cadenaEntrada = "";
        this.indiceCaracterActual = 0;
        this.estadoActual = 0;
        this.automataAFD = null;
        this.numeroLinea = 1;
        this.columnaInicio = 1;
        this.lexema = "";
        this.simbolos = new ArrayList<>();
        this.pilaIndices = new Stack<>();
    }

    public AnalizLexico(String cadenaEntrada, String archivoAFD) {
        this();
        this.cadenaEntrada = cadenaEntrada;
        this.automataAFD = AFD.leerDesdeBinario(archivoAFD);
    }

    public Simbolo obtenerSiguienteSimbolo() {
        if (automataAFD == null || automataAFD.getEstadoInicial() == null) {
            System.err.println("❌ El AFD no ha sido cargado correctamente.");
            return null;
        }

        Estado estado = automataAFD.getEstadoInicial();
        estadoActual = estado.getIdEdo();
        lexema = "";
        columnaInicio = indiceCaracterActual + 1;

        while (indiceCaracterActual < cadenaEntrada.length()) {
            char caracterActual = cadenaEntrada.charAt(indiceCaracterActual);
            String simbolo = String.valueOf(caracterActual);

            Estado siguienteEstado = automataAFD.getEstadoDestino(estado, simbolo);

            if (siguienteEstado != null) {
                estado = siguienteEstado;
                estadoActual = estado.getIdEdo();
                lexema += caracterActual;
                pilaIndices.push(indiceCaracterActual);
                indiceCaracterActual++;
            } else {
                break;
            }
        }

        // Si el lexema es válido y el estado es de aceptación, se genera el símbolo
        if (!lexema.isEmpty() && estado.getEdoAceptacion()) {
            // Aquí pasamos los valores correctamente, incluyendo el tipo de símbolo
            Simbolo simbolo = new Simbolo(lexema, estado.getToken(), numeroLinea, columnaInicio, determinarTipoSimbolo(estado.getToken()));
            simbolos.add(simbolo);
            return simbolo;
        } else if (!lexema.isEmpty()) {
            // Error: no es un estado de aceptación
            System.err.println("⚠️ Error léxico en línea " + numeroLinea + ", columna " + columnaInicio + ": \"" + lexema + "\"");
            indiceCaracterActual++;
            return null;
        }

        return null;
    }

    private String determinarTipoSimbolo(int token) {
        switch (token) {
            case 10: return "NUMERO";
            case 20: return "IDENTIFICADOR";
            case 30: return "OPERADOR";
            case 40: return "AGRUPADOR";
            case 50: return "SEPARADOR";
            default: return "DESCONOCIDO";
        }
    }

    public List<String> obtenerErrores() {
        List<String> errores = new ArrayList<>();
        for (Simbolo simbolo : simbolos) {
            if (simbolo.getTipo().equals("DESCONOCIDO")) {
                errores.add("Error léxico en línea " + simbolo.getLinea() + ", columna " + simbolo.getColumna() + ": \"" + simbolo.getLexema() + "\"");
            }
        }
        return errores;
    }

    public List<Simbolo> obtenerSimbolos() {
        List<Simbolo> resultado = new ArrayList<>();
        Simbolo simbolo;
        while ((simbolo = obtenerSiguienteSimbolo()) != null) {
            resultado.add(simbolo);
        }
        return resultado;
    }
}
