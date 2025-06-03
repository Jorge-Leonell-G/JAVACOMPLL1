package analizlexic;

import java.util.*;

public class AFN implements SerializableSupport {
    private int idAFN;
    private int token;
    private Estado edoInicial;
    private Set<Estado> edosAceptacion = new HashSet<>();
    private Set<Character> alfabeto = new HashSet<>();
    private Set<Estado> edosAFN = new HashSet<>();
    private Map<Estado, Map<String, Set<Estado>>> transiciones = new HashMap<>();
    private boolean AFNUnionLex;

    public AFN() {
        this.idAFN = -1;
    }

    public int getIdAFN() { return idAFN; }
    public void setIdAFN(int idAFN) { this.idAFN = idAFN; }

    public int getToken() { return token; }
    public void setToken(int token) { this.token = token; }

    public Estado getEdoInicial() { return edoInicial; }
    public void setEdoInicial(Estado edoInicial) { this.edoInicial = edoInicial; }

    public Set<Estado> getEdosAceptacion() { return edosAceptacion; }
    public Set<Character> getAlfabeto() { return alfabeto; }
    public Set<Estado> getEdosAFN() { return edosAFN; }
    public Map<Estado, Map<String, Set<Estado>>> getTransiciones() { return transiciones; }

    public void setEdoAFN(Estado edo) { this.edosAFN.add(edo); }
    public void setEdosAcept(Estado edo) {
        this.edosAceptacion.add(edo);
        edo.setEdoAceptacion(true);
        edo.setToken(this.token); // Se asegura de establecer el token también aquí
    }

    public void setSimbolos(char simbolo) { this.alfabeto.add(simbolo); }

    public void actualizarEstadoFinal(int idNuevoFinal) {
        for (Estado e : edosAFN) {
            if (e.getIdEdo() == idNuevoFinal) {
                edosAceptacion.clear();
                edosAceptacion.add(e);
                e.setEdoAceptacion(true);
                e.setToken(this.token);
                return;
            }
        }
    }

    public void agregarTransicion(Estado origen, String simbolo, Estado destino) {
        transiciones.computeIfAbsent(origen, k -> new HashMap<>())
                    .computeIfAbsent(simbolo, k -> new HashSet<>())
                    .add(destino);
    }

    public AFN crearAFNBasico(char s1, char s2, int token) {
        AFN f = new AFN();
        f.setIdAFN(token);
        f.setToken(token);

        Estado e1 = new Estado();
        Estado e2 = new Estado(true, token);
        e1.agregarTransicion(new Transicion(s1, s2, e2));

        f.setEdoInicial(e1);
        f.setEdoAFN(e1);
        f.setEdoAFN(e2);
        f.setEdosAcept(e2);

        for (char c = s1; c <= s2; c++) {
            f.setSimbolos(c);
        }

        ProyectoAFN.agregarAFN(f);
        return f;
    }

    public void unirAFNs(AFN f2) {
        if (f2 == null) return;

        Estado e0 = new Estado();
        Estado ef = new Estado(true, this.token);

        e0.agregarTransicion(new Transicion('\0', '\0', this.edoInicial));
        e0.agregarTransicion(new Transicion('\0', '\0', f2.edoInicial));

        for (Estado e : this.edosAceptacion) {
            e.agregarTransicion(new Transicion('\0', '\0', ef));
            e.setEdoAceptacion(false);
        }
        for (Estado e : f2.edosAceptacion) {
            e.agregarTransicion(new Transicion('\0', '\0', ef));
            e.setEdoAceptacion(false);
        }

        this.edosAceptacion.clear();
        this.edosAceptacion.add(ef);
        this.edosAFN.addAll(f2.edosAFN);
        this.edosAFN.add(e0);
        this.edosAFN.add(ef);
        this.edoInicial = e0;
        this.AFNUnionLex = true;

        ProyectoAFN.listaAFNs.remove(f2);
    }

    public void concatenarAFNs(AFN f2) {
        if (f2 == null) return;

        for (Estado e : this.edosAceptacion) {
            e.agregarTransicion(new Transicion('\0', '\0', f2.edoInicial));
            e.setEdoAceptacion(false);
        }

        this.edosAceptacion = f2.edosAceptacion;
        this.edosAFN.addAll(f2.edosAFN);
        ProyectoAFN.listaAFNs.remove(f2);
    }

    public void cerraduraKleene() {
        Estado e0 = new Estado();
        Estado ef = new Estado(true, this.token);

        e0.agregarTransicion(new Transicion('\0', '\0', this.edoInicial));
        e0.agregarTransicion(new Transicion('\0', '\0', ef));

        for (Estado e : this.edosAceptacion) {
            e.agregarTransicion(new Transicion('\0', '\0', this.edoInicial));
            e.agregarTransicion(new Transicion('\0', '\0', ef));
            e.setEdoAceptacion(false);
        }

        this.edosAceptacion.clear();
        this.edosAceptacion.add(ef);
        this.edosAFN.add(e0);
        this.edosAFN.add(ef);
        this.edoInicial = e0;
    }

    public void cerraduraPositiva() {
        Estado e0 = new Estado();
        Estado ef = new Estado(true, this.token);

        e0.agregarTransicion(new Transicion('\0', '\0', this.edoInicial));

        for (Estado e : this.edosAceptacion) {
            e.agregarTransicion(new Transicion('\0', '\0', this.edoInicial));
            e.agregarTransicion(new Transicion('\0', '\0', ef));
            e.setEdoAceptacion(false);
        }

        this.edosAceptacion.clear();
        this.edosAceptacion.add(ef);
        this.edosAFN.add(e0);
        this.edosAFN.add(ef);
        this.edoInicial = e0;
    }

    public void opcional() {
        Estado e0 = new Estado();
        Estado ef = new Estado(true, this.token);

        e0.agregarTransicion(new Transicion('\0', '\0', this.edoInicial));
        e0.agregarTransicion(new Transicion('\0', '\0', ef));

        for (Estado e : this.edosAceptacion) {
            e.agregarTransicion(new Transicion('\0', '\0', ef));
            e.setEdoAceptacion(false);
        }

        this.edosAceptacion.clear();
        this.edosAceptacion.add(ef);
        this.edosAFN.add(e0);
        this.edosAFN.add(ef);
        this.edoInicial = e0;
    }

    // Métodos mover y cerradura
    private Set<Estado> cerrarEpsilon(Set<Estado> conjunto) {
        Stack<Estado> pila = new Stack<>();
        Set<Estado> cerrado = new HashSet<>(conjunto);
        pila.addAll(conjunto);

        while (!pila.isEmpty()) {
            Estado e = pila.pop();
            for (Transicion t : e.getTransiciones()) {
                if (t.getSimbInf() == '\0' && !cerrado.contains(t.getEdo())) {
                    cerrado.add(t.getEdo());
                    pila.push(t.getEdo());
                }
            }
        }

        return cerrado;
    }

    private Set<Estado> mover(Set<Estado> conjunto, char simbolo) {
        Set<Estado> resultado = new HashSet<>();
        for (Estado e : conjunto) {
            for (Transicion t : e.getTransiciones()) {
                if (t.getSimbInf() <= simbolo && simbolo <= t.getSimbSup()) {
                    resultado.add(t.getEdo());
                }
            }
        }
        return resultado;
    }
}
