package testutils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Statics {
    /**
     * @param c class to call getResource on
     * @param name name of the resource
     * @return String read from the resource file
     */
    public static String getStringFromResource(Class c, String name) {
        String ret;
        try {
            ret = new String(
                    Files.readAllBytes(Paths.get(c.getResource(name).toURI())),
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            System.err.println("Problem with the testing code: Error loading resource!");
            throw new RuntimeException(e);
        }
        return ret;
    }
}
