package bp.projetbanque.GestionCheque;

import java.io.*;
import java.nio.file.*;
import java.util.Locale;

public class NativeLoader {

    public static void loadLibrary(String libName) {
        String os = System.getProperty("os.name").toLowerCase();
        String prefix = os.contains("win") ? "" : "lib";
        String ext = os.contains("win") ? ".dll" : os.contains("mac") ? ".dylib" : ".so";
        String resourcePath = "/native/" + prefix + libName + ext;

        try (InputStream is = NativeLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) throw new FileNotFoundException("Lib native introuvable : " + resourcePath);
            Path tempFile = Files.createTempFile(libName, ext);
            Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
            System.load(tempFile.toAbsolutePath().toString());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement de la bibliothèque native", e);
        }
    }
}
