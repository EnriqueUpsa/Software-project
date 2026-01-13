package it.unicas.project;
import java.util.ArrayList;
import java.util.List;

public class AplicacionMensajeria {
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Chat> chats = new ArrayList<>();

    public void registrarUsuario(String nombre) {
        usuarios.add(new Usuario(nombre));
    }

    public void crearChat(String nombre) {
        chats.add(new Chat(nombre));
    }
}