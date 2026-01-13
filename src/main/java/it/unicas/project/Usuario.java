package it.unicas.project;

public class Usuario extends Persona {
    public Usuario(String nombre) { super(nombre); }
    @Override
    public String getTipo() { return "Usuario Erasmus"; }
}
