package gitlet;

import ucb.junit.textui;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/** The suite of all JUnit tests for the gitlet package.
 *  @author Divya Chandrasekaran and Ilina Bhaya-Grossman
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    @Test
    public void testBlob () {
        File file = new File("test.txt");
        try {
            file.createNewFile();
        } catch (IOException io) {
            Main.error("File error.");
        }
        Blob a = new Blob ("test.txt", file);
        Blob b = new Blob ("test.txt", file);
        assertEquals("test.txt", b.getName());
        assertTrue(a.equals(b));
        b.writeBlob();
    }

    @Test
    public void testCommit () {
        File file = new File("test.txt");
        try {
            file.createNewFile();
        } catch (IOException io) {
            Main.error("File error.");
        }
        Blob a = new Blob ("test.txt", file);
        Commit c = new Commit(null);
        c.add(a);
        c.update();
        Blob b = new Blob ("test.txt", file);
        Commit d = new Commit(null);
        d.add(b);
        d.update();
        System.out.println(c.toString());
        System.out.println(d.toString());
        assertEquals("initial commit", c.getMessage());
        assertTrue(c.contains("test.txt"));
        assertTrue(d.equals(c));
    }

}


