package analizlexic;

import java.util.*;

/**
 * Clase que implementa las operaciones fundamentales para construir nuevos AFNs
 * usando el algoritmo de Thompson.
 */
public class Operaciones {

    public static final String EPSILON = "\0";

    public static AFN union(AFN afn1, AFN afn2) {
        AFN resultado = new AFN();

        Estado nuevoEstadoInicial = new Estado();
        resultado.setEdoAFN(nuevoEstadoInicial);
        resultado.setEdoInicial(nuevoEstadoInicial);

        resultado.getEdosAFN().addAll(afn1.getEdosAFN());
        resultado.getEdosAFN().addAll(afn2.getEdosAFN());

        resultado.getEdosAceptacion().addAll(afn1.getEdosAceptacion());
        resultado.getEdosAceptacion().addAll(afn2.getEdosAceptacion());

        resultado.agregarTransicion(nuevoEstadoInicial, EPSILON, afn1.getEdoInicial());
        resultado.agregarTransicion(nuevoEstadoInicial, EPSILON, afn2.getEdoInicial());

        Estado nuevoEstadoAceptacion = new Estado(true, 0);
        resultado.setEdoAFN(nuevoEstadoAceptacion);

        for (Estado estadoAceptacion : afn1.getEdosAceptacion()) {
            resultado.agregarTransicion(estadoAceptacion, EPSILON, nuevoEstadoAceptacion);
        }
        for (Estado estadoAceptacion : afn2.getEdosAceptacion()) {
            resultado.agregarTransicion(estadoAceptacion, EPSILON, nuevoEstadoAceptacion);
        }

        resultado.setEdosAcept(nuevoEstadoAceptacion);

        return resultado;
    }

    public static AFN concatenacion(AFN afn1, AFN afn2) {
        AFN resultado = new AFN();

        resultado.getEdosAFN().addAll(afn1.getEdosAFN());
        resultado.getEdosAFN().addAll(afn2.getEdosAFN());

        resultado.setEdoInicial(afn1.getEdoInicial());
        resultado.getEdosAceptacion().addAll(afn2.getEdosAceptacion());

        for (Estado estadoAceptacion : afn1.getEdosAceptacion()) {
            resultado.agregarTransicion(estadoAceptacion, EPSILON, afn2.getEdoInicial());
        }

        return resultado;
    }

    public static AFN cerraduraKleene(AFN afn) {
        AFN resultado = new AFN();

        Estado nuevoEstadoInicial = new Estado();
        Estado nuevoEstadoAceptacion = new Estado(true, 0);

        resultado.setEdoAFN(nuevoEstadoInicial);
        resultado.setEdoAFN(nuevoEstadoAceptacion);
        resultado.setEdoInicial(nuevoEstadoInicial);
        resultado.setEdosAcept(nuevoEstadoAceptacion);

        resultado.getEdosAFN().addAll(afn.getEdosAFN());

        resultado.agregarTransicion(nuevoEstadoInicial, EPSILON, afn.getEdoInicial());
        resultado.agregarTransicion(nuevoEstadoInicial, EPSILON, nuevoEstadoAceptacion);

        for (Estado estadoAceptacion : afn.getEdosAceptacion()) {
            resultado.agregarTransicion(estadoAceptacion, EPSILON, nuevoEstadoAceptacion);
            resultado.agregarTransicion(estadoAceptacion, EPSILON, afn.getEdoInicial());
        }

        return resultado;
    }

    public static AFN cerraduraPositiva(AFN afn) {
        AFN resultado = new AFN();

        Estado nuevoEstadoInicial = new Estado();
        Estado nuevoEstadoAceptacion = new Estado(true, 0);

        resultado.setEdoAFN(nuevoEstadoInicial);
        resultado.setEdoAFN(nuevoEstadoAceptacion);
        resultado.setEdoInicial(nuevoEstadoInicial);
        resultado.setEdosAcept(nuevoEstadoAceptacion);

        resultado.getEdosAFN().addAll(afn.getEdosAFN());

        resultado.agregarTransicion(nuevoEstadoInicial, EPSILON, afn.getEdoInicial());

        for (Estado estadoAceptacion : afn.getEdosAceptacion()) {
            resultado.agregarTransicion(estadoAceptacion, EPSILON, nuevoEstadoAceptacion);
            resultado.agregarTransicion(estadoAceptacion, EPSILON, afn.getEdoInicial());
        }

        return resultado;
    }

    public static AFN opcional(AFN afn) {
        AFN resultado = new AFN();

        Estado nuevoEstadoInicial = new Estado();
        Estado nuevoEstadoAceptacion = new Estado(true, 0);

        resultado.setEdoAFN(nuevoEstadoInicial);
        resultado.setEdoAFN(nuevoEstadoAceptacion);
        resultado.setEdoInicial(nuevoEstadoInicial);
        resultado.setEdosAcept(nuevoEstadoAceptacion);

        resultado.getEdosAFN().addAll(afn.getEdosAFN());

        resultado.agregarTransicion(nuevoEstadoInicial, EPSILON, afn.getEdoInicial());
        resultado.agregarTransicion(nuevoEstadoInicial, EPSILON, nuevoEstadoAceptacion);

        for (Estado estadoAceptacion : afn.getEdosAceptacion()) {
            resultado.agregarTransicion(estadoAceptacion, EPSILON, nuevoEstadoAceptacion);
        }

        return resultado;
    }
}
