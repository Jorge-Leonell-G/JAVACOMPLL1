package analizlexic;

import java.io.Serializable;

public class Transicion implements Serializable {
    private static final long serialVersionUID = 1L;

    private char simbInf;
    private char simbSup;
    private Estado edo;

    // Constructor
    public Transicion(char simbInf, char simbSup, Estado edo) {
        this.simbInf = simbInf;
        this.simbSup = simbSup;
        this.edo = edo;
    }

    // Getters y setters
    public char getSimbInf() {
        return simbInf;
    }

    public void setSimbInf(char simbInf) {
        this.simbInf = simbInf;
    }

    public char getSimbSup() {
        return simbSup;
    }

    public void setSimbSup(char simbSup) {
        this.simbSup = simbSup;
    }

    public Estado getEdo() {
        return edo;
    }

    public void setEdo(Estado edo) {
        this.edo = edo;
    }

    @Override
    public String toString() {
        return "δ(" + simbInf + "–" + simbSup + ") → " + edo;
    }
}
