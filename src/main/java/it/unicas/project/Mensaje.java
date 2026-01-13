package it.unicas.project;
import java.time.LocalDateTime;

public class Mensaje {
    private String contenido;
    private String remitente;
    private LocalDateTime fecha = LocalDateTime.now();

    public Mensaje(String contenido, String remitente) {
        this.contenido = contenido;
        this.remitente = remitente;
    }
    @Override
    public String toString() { return "[" + remitente + "]: " + contenido; }
}