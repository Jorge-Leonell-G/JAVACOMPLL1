/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package analizlexic;

/**
 *
 * @author leone
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;

public class LL1Tabla extends JFrame {
    int numReglas;
    List<Regla> ReglaA = new ArrayList<>();
    public Map<String, Map<String, String>> tablaLL1 = new HashMap<>();
    public HashSet<String> Vn = new HashSet<>();
    public HashSet<String> Vt = new HashSet<>();
    
    //clase interna nodo
    /*
    class Nodo {
        public String nameSimbolo;
        public boolean isTerminal;
        public int token;

        public Nodo(String nombSimb, boolean esTerminal) {
            this.nameSimbolo = nombSimb;
            this.isTerminal = esTerminal;
            this.token = -1;
        }

        public void setToken(int token) {
            this.token = token; // Asignar el token al nodo
        }

        //Overdrive de la funcion built-in para mostrar de manera legible el valor del token o bien el nombre del simbolo
        @Override
        public String toString() {
            return isTerminal ? String.valueOf(token) : nameSimbolo;
        }
    }
    
    //clase interna regla
    
    public class Regla {
        public String nameSimbolo;
        public List<Nodo> Lista;

        public Regla(String nombSimb, List<Nodo> lista) {
            this.nameSimbolo = nombSimb;
            this.Lista = lista;
        }
    
        public Regla() {
            this.Lista = new ArrayList<>();
        } 
    }
*/
    public LL1Tabla() {
        numReglas = 0;
        ReglaA = new ArrayList<>();
        tablaLL1 = new HashMap<>();
        Vt = new HashSet<>();
        Vn = new HashSet<>();
    }

    public LL1Tabla(int n, List<Regla> reglas, HashSet<String> NoT, HashSet<String> T) {
        numReglas = n;
        ReglaA = reglas;
        tablaLL1 = new HashMap<>();
        Vt = T; //simbolos terminales
        Vn = NoT; //simbolos no terminales
    }

    //Operacion first que tiene como argumento una lista de reglas asociadas a una cadena alfa y un conjunto de simbolos visitados
    public Set<String> First(List<Nodo> L, Set<String> visitados) {
        //Conjunto a retornar que corresponde a los simbolos terminales o epsilon con los que pueden iniciar las derivaciones de alpha
        Set<String> R = new HashSet<>();

        if (L.isEmpty()) {
            return R;
        }

        Nodo simbolo = L.get(0);

        //Si la cadena inicia con un simbolo terminal, el first por defecto es ese mismo terminal
        if (simbolo.isTerminal) {
            R.add(simbolo.nameSimbolo);
            return R;
        }

        // Evitar procesar el mismo símbolo no terminal
        if (visitados.contains(simbolo.nameSimbolo)) {
            return R;
        }
        visitados.add(simbolo.nameSimbolo);

        //Se exploran las reglas asociadas con el primer simbolo no terminal de la cadena en caso de no tener un terminal al inicio de la misma
        for (Regla regla : ReglaA) {
            if (regla.nameSimbolo.equals(simbolo.nameSimbolo)) {
                R.addAll(First(regla.lista, visitados)); //El first de B1 se agrega al first de A1
            }
        }

        if (R.contains("Epsilon") && L.size() > 1) {
            R.remove("Epsilon");
            R.addAll(First(L.subList(1, L.size()), new HashSet<>(visitados)));
        }

        return R;
    }

    // Método de acceso original sin necesidad de pasar el conjunto, con una cadena alpha como argumento
    public Set<String> First(List<Nodo> L) {
        return First(L, new HashSet<>());
    }

    //La operacion follow recibe como argumento un simbolo no terminal y un conjunto de simbolos visitados
    public Set<String> Follow(String SimbNoT, Set<String> visitados) {
        Set<String> R = new HashSet<>();

        if (visitados.contains(SimbNoT)) {
            return R;
        }
        visitados.add(SimbNoT);

        if (SimbNoT.equals(ReglaA.get(0).nameSimbolo)) {
            R.add("$"); //Si A es un no terminal y es el simbolo inicial, entonces $ se agrega al follow(A)
        }

        for (int i = 0; i < numReglas; i++) {
            List<Nodo> listaSimbolos = ReglaA.get(i).lista;

            for (int j = 0; j < listaSimbolos.size(); j++) {
                if (listaSimbolos.get(j).nameSimbolo.equals(SimbNoT)) {
                    if (j + 1 < listaSimbolos.size()) {
                        List<Nodo> LAux = listaSimbolos.subList(j + 1, listaSimbolos.size());
                        Set<String> RAux = First(LAux, new HashSet<>());

                        R.addAll(RAux);
                        R.remove("Epsilon");

                        if (RAux.contains("Epsilon")) {
                            R.addAll(Follow(ReglaA.get(i).nameSimbolo, visitados));
                        }
                    } else { //Si B -> (alpha)A(beta) es una regla de G, entonces se agrega First(beta) al Follow(A) siempre y cuando no haya epsilon en First(beta)
                        if (!ReglaA.get(i).nameSimbolo.equals(SimbNoT)) {
                            R.addAll(Follow(ReglaA.get(i).nameSimbolo, visitados));
                        }
                    }
                }
            }
        }

        return R;
    }

    // Método de acceso original
    public Set<String> Follow(String SimbNoT) {
        return Follow(SimbNoT, new HashSet<>());
    }

    public void construirTablaLL1() {
        Map<String, Map<String, String>> nuevaTablaLL1 = new HashMap<>();

        System.out.println("Construyendo la tabla LL(1)...");

        for (Regla regla : ReglaA) {
            String noTerminal = regla.nameSimbolo;
            List<Nodo> produccion = regla.lista;

            // Obtener First de la producción
            Set<String> firstProduccion = First(produccion);
            nuevaTablaLL1.putIfAbsent(noTerminal, new HashMap<>());

            for (String terminal : firstProduccion) {
                if (!terminal.equals("Epsilon")) { //Si el terminal no es un epsilon, 
                    String produccionConTokens = produccionToString(produccion);
                    nuevaTablaLL1.get(noTerminal).put(terminal, produccionConTokens);
                }
            }

            if (firstProduccion.contains("Epsilon")) { //Se realiza un follow al no terminal
                Set<String> followNoTerminal = Follow(noTerminal);
                for (String terminal : followNoTerminal) {
                    nuevaTablaLL1.get(noTerminal).put(terminal, "Epsilon");
                }
            }
        }

        this.tablaLL1 = nuevaTablaLL1;
        System.out.println("Tabla LL(1) construida correctamente con tokens.");
    }

    private String produccionToString(List<Nodo> produccion) {
        StringBuilder sb = new StringBuilder();
        for (Nodo nodo : produccion) {
            if (nodo.isTerminal) {
                sb.append(nodo.token != -1 ? nodo.token : nodo.nameSimbolo).append(" ");
            } else {
                sb.append(nodo.nameSimbolo).append(" ");
            }
        }
        return sb.toString().trim();
    }

    public void imprimirTablaLL1() {
        System.out.println("------ Tabla LL(1) ------");

        // Encabezado: mostrar terminales
        Set<String> terminales = new HashSet<>();
        for (Map<String, String> entradas : tablaLL1.values()) {
            terminales.addAll(entradas.keySet());
        }
        List<String> terminalesOrdenados = new ArrayList<>(terminales);
        terminalesOrdenados.sort(String::compareTo);

        System.out.print("No Terminal\t");
        for (String terminal : terminalesOrdenados) {
            System.out.print(terminal + "\t");
        }
        System.out.println();

        // Imprimir cada fila (No Terminal y sus producciones)
        for (String noTerminal : tablaLL1.keySet()) {
            System.out.print(noTerminal + "\t\t");
            Map<String, String> producciones = tablaLL1.get(noTerminal);

            for (String terminal : terminalesOrdenados) {
                String produccion = producciones.getOrDefault(terminal, " ");
                System.out.print(produccion + "\t");
            }
            System.out.println();
        }

        System.out.println("-------------------------");
    }

    public void convertirTerminalesATokens(Map<String, Integer> tokensUsuario) {
        // Asignar tokens a los terminales en las reglas
        for (Regla regla : ReglaA) {
            for (Nodo nodo : regla.lista) {
                if (nodo.isTerminal && tokensUsuario.containsKey(nodo.nameSimbolo)) {
                    nodo.setToken(tokensUsuario.get(nodo.nameSimbolo)); // Asignar token al nodo
                }
            }
        }

        // Actualizar la tabla LL(1) con tokens en lugar de nombres
        Map<String, Map<String, String>> nuevaTablaLL1 = new HashMap<>();

        for (Map.Entry<String, Map<String, String>> fila : tablaLL1.entrySet()) {
            String noTerminal = fila.getKey();
            Map<String, String> producciones = fila.getValue();
            Map<String, String> nuevaFila = new HashMap<>();

            for (Map.Entry<String, String> produccion : producciones.entrySet()) {
                String terminal = produccion.getKey();
                String regla = produccion.getValue();

                if (terminal.equals("$")) {
                    nuevaFila.put("0", regla);
                }
                // Reemplazar terminales por tokens si existen
                if (tokensUsuario.containsKey(terminal)) {
                    int token = tokensUsuario.get(terminal);
                    nuevaFila.put(String.valueOf(token), convertirProduccionATokens(regla, tokensUsuario));
                } else {
                    nuevaFila.put(terminal, convertirProduccionATokens(regla, tokensUsuario));
                }
            }
            nuevaTablaLL1.put(noTerminal, nuevaFila);
        }

        tablaLL1 = nuevaTablaLL1;
        System.out.println("Terminales y nodos actualizados con tokens numéricos exitosamente.");
    }

    public Map<String, Map<String, String>> convertirTablaLL1ANumerosDeRegla() {
        Map<String, Map<String, String>> tablaConNumeros = new HashMap<>();

        // Iterar sobre las reglas de la tabla LL(1)
        for (int reglaIdx = 0; reglaIdx < ReglaA.size(); reglaIdx++) {
            Regla regla = ReglaA.get(reglaIdx);
            String noTerminal = regla.nameSimbolo;

            // Obtener o inicializar la fila en la tabla de salida
            tablaConNumeros.putIfAbsent(noTerminal, new HashMap<>());
            Map<String, String> filaActual = tablaConNumeros.get(noTerminal);

            // Obtener los FIRST de la producción
            Set<String> firstProduccion = First(regla.lista);

            // Llenar con los terminales del FIRST
            for (String terminal : firstProduccion) {
                if (!terminal.equals("Epsilon")) {
                    filaActual.put(terminal, String.valueOf(reglaIdx + 1)); // Número de regla
                }
            }

            // Si contiene Epsilon, agrega FOLLOW
            if (firstProduccion.contains("Epsilon")) {
                Set<String> followNoTerminal = Follow(noTerminal);
                for (String terminal : followNoTerminal) {
                    filaActual.put(terminal, String.valueOf(reglaIdx + 1)); // Número de regla
                }
            }
        }

        System.out.println("Tabla LL(1) generada con números de regla.");
        return tablaConNumeros;
    }

// Método auxiliar para convertir los terminales de una producción a tokens
    private String convertirProduccionATokens(String regla, Map<String, Integer> tokensUsuario) {
        StringBuilder sb = new StringBuilder();
        String[] simbolos = regla.split(" ");
        for (String simbolo : simbolos) {
            if (tokensUsuario.containsKey(simbolo)) {
                sb.append(tokensUsuario.get(simbolo)).append(" ");
            } else {
                sb.append(simbolo).append(" ");
            }
        }
        return sb.toString().trim();
    }
    
    private String producirCadena(Regla regla) {
        StringBuilder produccion = new StringBuilder();

        for (Nodo n : regla.lista) {
            produccion.append(n.nameSimbolo).append(" ");
        }

        return produccion.toString().trim();
    }
    
    public void imprimirTabla() {
    System.out.println("\nTABLA LL(1):");
    for (String noTerminal : tablaLL1.keySet()) {
        for (String terminal : tablaLL1.get(noTerminal).keySet()) {
            String produccion = tablaLL1.get(noTerminal).get(terminal);
            System.out.println("M[" + noTerminal + ", " + terminal + "] = " + produccion);
        }
    }
}
    
    public void construirEstructuraBasica() {
    // Construye la tabla sin conversión de tokens
    this.tablaLL1 = new HashMap<>();
    
    // Lógica para llenar la tabla con producciones en formato string
    for (String noTerminal : this.Vn) {
        Map<String, String> fila = new HashMap<>();
        // ... lógica de construcción ...
        this.tablaLL1.put(noTerminal, fila);
    }
}


}
