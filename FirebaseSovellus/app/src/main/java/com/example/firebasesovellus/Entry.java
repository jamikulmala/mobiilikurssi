package com.example.firebasesovellus;

public class Entry {
    private String id;
    private int numero;
    private String nimi;
    private int painos;
    private String hankinta;

    public int getNumero() {
        return numero;
    }

    public String getId() { return id; }

    @Override
    public String toString() {
        return numero + ". " + nimi;
    }

    public String getNimi() {
        return nimi;
    }

    public int getPainos() {
        return painos;
    }

    public String hankinta() {
        return hankinta;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public void setPainos(int painos) {
        this.painos = painos;
    }

    public void setId(String id) { this.id = id; }

    public void setHankinta(String hankinta) {
        this.hankinta = hankinta;
    }
}
