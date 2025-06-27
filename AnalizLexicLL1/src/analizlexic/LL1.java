package analizlexic;

import java.util.Collections;
import java.util.Stack;
import java.util.Map;
import java.util.List;

public class LL1 {
    
    public final LL1Tabla tabla;
    public final Stack<String> pila;
    public String accion;
    public String cadenaActual;
    
    private final List<Simbolo> simbolos;
    private int currentSymbolIndex;
    Simbolo simboloActual;
    
    public LL1(LL1Tabla tabla, List<Simbolo> simbolosLexicos) {
        if (tabla == null || simbolosLexicos == null) {
            throw new IllegalArgumentException("Tabla y símbolos no pueden ser nulos");
        }
        this.tabla = tabla;
        this.pila = new Stack<>();
        //this.accion = "";
        //this.simbolos = simbolosLexicos;
        this.simbolos = Collections.unmodifiableList(simbolosLexicos);
        //this.currentSymbolIndex = 0;
        
        // Inicializar pila
        this.pila.push("$"); // Fin de cadena
        this.pila.push(tabla.ReglaA.get(0).nameSimbolo); // Símbolo inicial
        
        // Obtener primer símbolo
        updateCurrentSymbol();
        
        // Construir representación de cadena para visualización
        StringBuilder cadenaStr = new StringBuilder();
        for (Simbolo s : simbolos) {
            cadenaStr.append(s.getLexema()).append(" ");
        }
        this.cadenaActual = cadenaStr.toString().trim() + " $";
        
        System.out.println("Token inicial: " + simboloActual.getToken() + 
                         ". Lexema: " + simboloActual.getLexema());
    }
    
    private void updateCurrentSymbol() {
        if (currentSymbolIndex < simbolos.size()) {
            simboloActual = simbolos.get(currentSymbolIndex);
        } else {
            // Crear símbolo de fin de cadena
            simboloActual = new Simbolo("$", SimbolosEspeciales.FIN);
        }
    }
    
    public boolean analisisRecursivo() {
        System.out.println("\nIniciando análisis sintáctico LL(1)...");
        System.out.println("Cadena a analizar: " + cadenaActual);
        
        while (!pila.isEmpty()) {
            String top = pila.peek();
            accion = "";
            imprimirEstadoPila(top);
            
            if (isTerminal(top)) {
                if (matchTerminal(top, simboloActual.getToken())) {
                    accion = "Match '" + simboloActual.getLexema() + "'";
                    System.out.println(accion);
                    pila.pop();
                    advanceSymbol();
                } else {
                    System.err.println("Error sintáctico: Se esperaba " + getExpectedTerminal(top) + 
                                     " pero se encontró '" + simboloActual.getLexema() + "'");
                    return false;
                }
            } else {
                String produccion = getProduccion(top, simboloActual.getToken());
                if (produccion == null) {
                    System.err.println("Error: No hay producción definida para [" + top + 
                                      "][" + simboloActual.getToken() + "]");
                    return false;
                } else {
                    accion = top + " → " + produccion;
                    System.out.println(accion);
                    pila.pop();
                    pushProductionToStack(produccion);
                }
            }
        }
        
        if (simboloActual.getToken() == SimbolosEspeciales.FIN) {
            accion = "Aceptar";
            System.out.println(accion);
            return true;
        }
        
        System.err.println("Error: Cadena no completamente analizada");
        return false;
    }
    
    private boolean matchTerminal(String stackTop, int inputToken) {
        try {
            int stackToken = Integer.parseInt(stackTop);
            return stackToken == inputToken;
        } catch (NumberFormatException e) {
            return stackTop.equals("$") && inputToken == SimbolosEspeciales.FIN;
        }
    }
    
    private String getExpectedTerminal(String stackTop) {
        try {
            int token = Integer.parseInt(stackTop);
            // Buscar el nombre del token en la tabla
            for (Regla r : tabla.ReglaA) {
                for (Nodo n : r.lista) {
                    if (n.token == token) {
                        return n.nameSimbolo;
                    }
                }
            }
            return "token " + token;
        } catch (NumberFormatException e) {
            return stackTop.equals("$") ? "fin de cadena ($)" : stackTop;
        }
    }
    
    private void pushProductionToStack(String production) {
        String[] simbolos = production.split(" ");
        for (int i = simbolos.length - 1; i >= 0; i--) {
            if (!simbolos[i].equals("Epsilon")) {
                pila.push(simbolos[i]);
            }
        }
    }
    
    private void advanceSymbol() {
        currentSymbolIndex++;
        updateCurrentSymbol();
    }
    
    private void imprimirEstadoPila(String tope) {
        String pilaEstado = pila.toString().replace("[", "").replace("]", "").replace(", ", " ");
        System.out.println(pilaEstado + "\t" + simboloActual.getLexema() + "\t" + accion);
    }
    
    private boolean isTerminal(String simb) {
        return simb.equals("$") || isNumeric(simb);
    }
    
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String getProduccion(String noTerminal, int terminal) {
        String tokenCadena = String.valueOf(terminal);
        
        if (tabla.tablaLL1.containsKey(noTerminal)) {
            Map<String, String> prods = tabla.tablaLL1.get(noTerminal);
            if (prods.containsKey(tokenCadena)) {
                return prods.get(tokenCadena);
            }
        }
        return null;
    }
}