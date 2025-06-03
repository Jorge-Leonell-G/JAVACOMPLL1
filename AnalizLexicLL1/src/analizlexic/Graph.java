package analizlexic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Esta clase permite generar una representación visual de un autómata (AFN o AFD)
 * en formato DOT, que puede ser interpretado por Graphviz para crear un diagrama.
 */
public class Graph {

    /**
     * Genera un archivo .dot para visualizar un AFN.
     *
     * @param afn El autómata AFN a graficar.
     * @param nombreAFN Nombre del archivo y del grafo.
     * @param directorioSalida Carpeta de salida del archivo.
     * @return Ruta del archivo generado.
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
    public static String graficarAFN(AFN afn, String nombreAFN, String directorioSalida) throws IOException {
        String nombreArchivoDot = nombreAFN + ".dot";
        File carpeta = new File(directorioSalida);

        if (!carpeta.exists()) carpeta.mkdirs();

        String pathDot = directorioSalida + File.separator + nombreArchivoDot;

        try (FileWriter writer = new FileWriter(pathDot)) {
            writer.write("digraph " + nombreAFN + " {\n");
            writer.write(" rankdir=LR;\n");
            writer.write(" node [shape = circle];\n");

            for (Estado estado : afn.getEdosAFN()) {
                String nombre = estado.getNombre();
                if (afn.getEdosAceptacion().contains(estado)) {
                    writer.write(" \"" + nombre + "\" [shape = doublecircle];\n");
                } else if (afn.getEdoInicial().equals(estado)) {
                    writer.write(" \"" + nombre + "\" [style = bold];\n");
                } else {
                    writer.write(" \"" + nombre + "\";\n");
                }
            }

            for (Map.Entry<Estado, Map<String, java.util.Set<Estado>>> entrada : afn.getTransiciones().entrySet()) {
                Estado origen = entrada.getKey();
                for (Map.Entry<String, java.util.Set<Estado>> trans : entrada.getValue().entrySet()) {
                    String simbolo = trans.getKey();
                    for (Estado destino : trans.getValue()) {
                        String etiqueta = simbolo.equals("\0") ? "ε" : simbolo;
                        writer.write(" \"" + origen.getNombre() + "\" -> \"" + destino.getNombre()
                                + "\" [label = \"" + etiqueta + "\"];\n");
                    }
                }
            }

            writer.write("}\n");
        } catch (IOException e) {
            throw new IOException("Error al escribir el archivo .dot: " + e.getMessage());
        }

        return pathDot;
    }

    /**
     * Genera un archivo .dot para visualizar un AFD.
     *
     * @param afd El autómata AFD a graficar.
     * @param nombreAFD Nombre del archivo y del grafo.
     * @param directorioSalida Carpeta de salida del archivo.
     * @return Ruta del archivo generado.
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
    public static String graficarAFD(AFD afd, String nombreAFD, String directorioSalida) throws IOException {
        String nombreArchivoDot = nombreAFD + ".dot";
        File carpeta = new File(directorioSalida);

        if (!carpeta.exists()) carpeta.mkdirs();

        String pathDot = directorioSalida + File.separator + nombreArchivoDot;

        try (FileWriter writer = new FileWriter(pathDot)) {
            writer.write("digraph " + nombreAFD + " {\n");
            writer.write(" rankdir=LR;\n");
            writer.write(" node [shape = circle];\n");

            for (Estado estado : afd.getEstados()) {
                String nombre = estado.getNombre();
                if (afd.getEstadosAceptacion().contains(estado)) {
                    writer.write(" \"" + nombre + "\" [shape = doublecircle];\n");
                } else if (afd.getEstadoInicial().equals(estado)) {
                    writer.write(" \"" + nombre + "\" [style = bold];\n");
                } else {
                    writer.write(" \"" + nombre + "\";\n");
                }
            }

            for (Map.Entry<Estado, Map<String, Estado>> entrada : afd.getTablaDeTransiciones().entrySet()) {
                Estado origen = entrada.getKey();
                for (Map.Entry<String, Estado> trans : entrada.getValue().entrySet()) {
                    String simbolo = trans.getKey();
                    Estado destino = trans.getValue();
                    String etiqueta = simbolo.equals("\0") ? "ε" : simbolo;
                    writer.write(" \"" + origen.getNombre() + "\" -> \"" + destino.getNombre()
                            + "\" [label = \"" + etiqueta + "\"];\n");
                }
            }

            writer.write("}\n");
        } catch (IOException e) {
            throw new IOException("Error al escribir el archivo .dot: " + e.getMessage());
        }

        return pathDot;
    }
}