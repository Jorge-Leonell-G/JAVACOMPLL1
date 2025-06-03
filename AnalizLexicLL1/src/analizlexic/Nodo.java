/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package analizlexic;
/**
 *
 * @author leone
 */
public class Nodo {
    public String nameSimbolo;
    public boolean isTerminal;
    public int token;

    public Nodo(String simbolo, boolean esTerminal) {
        this.nameSimbolo = simbolo;
        this.isTerminal = esTerminal;
        this.token = -1;
    }

    public void setToken(int token) {
        this.token = token; // Asignar el token al nodo
    }

    @Override
    public String toString() {
        return isTerminal ? String.valueOf(token) : nameSimbolo;
    }
}
