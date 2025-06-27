package analizlexic;

import java.io.Serializable;

public class Simbolo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String lexema;
    private final int token;
    private final String tipo; // Tipo de símbolo
    private final int linea;  // Línea donde se encuentra el lexema
    private final int columna; // Columna donde se encuentra el lexema
    
    // Constructor básico para análisis léxico-sintáctico
    public Simbolo(String lexema, int token) {
        this(lexema, token, -1, -1, "TERMINAL"); // Valores por defecto
    }

    // Constructor completo
    public Simbolo(String lexema, int token, int linea, int columna, String tipo) {
        this.lexema = lexema;
        this.token = token;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
    }

    public String getLexema() {
        return lexema;
    }

    public int getToken() {
        return token;
    }

    public String getTipo() {
        return tipo; // Devuelve el tipo de símbolo asignado
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    public String toString() {
        return "Simbolo [lexema=" + lexema + ", token=" + token + ", tipo=" + tipo + ", linea=" + linea + ", columna=" + columna + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Simbolo)) return false;
        Simbolo other = (Simbolo) obj;
        return this.token == other.token && this.lexema.equals(other.lexema);
    }

    @Override
    public int hashCode() {
        return 31 * lexema.hashCode() + token;
    }
}
