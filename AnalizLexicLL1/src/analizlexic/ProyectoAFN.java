package analizlexic;

import java.util.ArrayList;
import java.util.List;

public class ProyectoAFN {
    public static final List<AFN> listaAFNs = new ArrayList<>();

    public static AFN obtenerAFN(int id) {
        return listaAFNs.stream()
            .filter(afn -> afn.getIdAFN() == id)
            .findFirst()
            .orElse(null);
    }

    public static void limpiarAFNs() {
        listaAFNs.clear();
    }

   public static void agregarAFN(AFN afn) {
    if (!listaAFNs.contains(afn)) {
        listaAFNs.add(afn);
    }
}
}
