package it.unicas.project;
import java.util.ArrayList;
import java.util.List;

public class Chat {
    private String nombre;
    private List<Mensaje> mensajes = new ArrayList<>();

    public Chat(String nombre) { this.nombre = nombre; }
    public void agregarMensaje(Mensaje m) { mensajes.add(m); }
    public String getNombre() { return nombre; }
}