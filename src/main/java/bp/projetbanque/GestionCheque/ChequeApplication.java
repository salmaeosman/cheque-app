package bp.projetbanque.GestionCheque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class ChequeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChequeApplication.class, args);

        // Ouvrir le navigateur automatiquement après le démarrage
        try {
            String url = "http://localhost:8103/cheque/formulaire"; // ou le port que tu utilises
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("Desktop not supported. Please open your browser and go to " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
