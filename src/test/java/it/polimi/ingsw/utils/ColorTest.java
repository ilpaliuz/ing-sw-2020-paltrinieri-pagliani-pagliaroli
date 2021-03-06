package it.polimi.ingsw.utils;

import junit.framework.TestCase;

import static it.polimi.ingsw.utils.Color.*;

public class ColorTest extends TestCase {
    Color color1 = Color.White;
    Color color2 = Red;
    Color color3 = Color.Purple;

    public void testIntToColor() {
        assertEquals(White, Color.intToColor(0));
        assertEquals(Purple, Color.intToColor(1));
        assertEquals(Red, Color.intToColor(2));
        try {
            Color a = Color.intToColor(3);
        } catch (IllegalArgumentException e) {
            assertEquals("Color must be 0|1|2", e.getLocalizedMessage());
        }
    }

    public void testToAnsiCode() {
        assertEquals("\u001B[1m", Color.toANSICode(0));
        assertEquals("\u001B[35m", Color.toANSICode(1));
        assertEquals("\u001B[31m", Color.toANSICode(2));
        try {
            String a = Color.toANSICode(3);
        } catch (IllegalArgumentException e) {
            assertEquals("Color must be 0|1|2", e.getLocalizedMessage());
        }
    }
}

