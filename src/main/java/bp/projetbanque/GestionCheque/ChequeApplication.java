package bp.projetbanque.GestionCheque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class ChequeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChequeApplication.class, args);

        try {
            Thread.sleep(3000);
            openBrowser("http://localhost:8104/");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Méthode alternative selon le système
                String os = System.getProperty("os.name").toLowerCase();
                Runtime rt = Runtime.getRuntime();

                if (os.contains("win")) {
                    rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else if (os.contains("mac")) {
                    rt.exec("open " + url);
                } else if (os.contains("nix") || os.contains("nux")) {
                    rt.exec("xdg-open " + url);
                } else {
                    System.err.println("Impossible d'ouvrir le navigateur automatiquement sur ce système.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
