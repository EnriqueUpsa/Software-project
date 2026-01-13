package it.unicas.project;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void testLogic() {
        Usuario u = new Usuario("Enrique");
        assertEquals("Enrique", u.getNombre());
        assertEquals("Usuario Erasmus", u.getTipo());
    }
}