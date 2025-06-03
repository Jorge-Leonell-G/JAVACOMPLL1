/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package analizlexic;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author leone
 */
public class Regla {
    public String nameSimbolo;
    public List<Nodo> lista;

    public Regla(String simbolo, List<Nodo> l) {
        this.nameSimbolo = simbolo;
        this.lista = l;
    }
    
    public Regla() {
        this.lista = new ArrayList<>();
    }
}
