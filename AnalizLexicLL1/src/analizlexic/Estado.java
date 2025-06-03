package analizlexic;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Estado implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int contadorGlobal = 0;

    private int idEdo;
    private boolean edoAceptacion;
    private int token;
    private String nombre;
    private Set<Transicion> transiciones = new HashSet<>();

    // Constructor por defecto
    public Estado() {
        this.idEdo = contadorGlobal++;
    }

    // Constructor con estado de aceptación y token
    public Estado(boolean aceptacion, int token) {
        this.idEdo = contadorGlobal++;
        this.edoAceptacion = aceptacion;
        this.token = token;
    }

    public int getIdEdo() {
        return idEdo;
    }

    public void setIdEdo(int idEdo) {
        this.idEdo = idEdo;
    }

    public boolean getEdoAceptacion() {
        return edoAceptacion;
    }

    public void setEdoAceptacion(boolean edoAceptacion) {
        this.edoAceptacion = edoAceptacion;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getNombre() {
        return (nombre != null && !nombre.isEmpty()) ? nombre : "q" + idEdo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Transicion> getTransiciones() {
        return transiciones;
    }

    public void agregarTransicion(Transicion t) {
        this.transiciones.add(t);
    }

    public void setTransiciones(Set<Transicion> transiciones) {
        this.transiciones = transiciones;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Estado)) return false;
        Estado other = (Estado) obj;
        return this.idEdo == other.idEdo && this.token == other.token;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEdo, token);
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
