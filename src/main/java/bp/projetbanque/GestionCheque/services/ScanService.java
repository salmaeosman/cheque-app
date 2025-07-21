package bp.projetbanque.GestionCheque.services;

import bp.projetbanque.GestionCheque.entities.Cheque;
import bp.projetbanque.GestionCheque.entities.Scan;
import bp.projetbanque.GestionCheque.repositories.ChequeRepository;
import bp.projetbanque.GestionCheque.repositories.ScanRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScanService {

    @Value("${scan.output-folder}")
    private String outputFolder;

    @Value("${scan.naps2-path}")
    private String naps2Path;

    private final ScanRepository scanRepository;
    private final ChequeRepository chequeRepository;

    public ScanService(ScanRepository scanRepository, ChequeRepository chequeRepository) {
        this.scanRepository = scanRepository;
        this.chequeRepository = chequeRepository;
    }

    public Scan launchRealScan(Long chequeId, String profileName) throws Exception {
        // Vérifie que le chèque existe
        Cheque cheque = chequeRepository.findById(chequeId)
                .orElseThrow(() -> new RuntimeException("Chèque introuvable avec ID: " + chequeId));

        // Préparation du dossier et du nom de fichier
        File dir = new File(outputFolder);
        if (!dir.exists()) dir.mkdirs();

        String fileName = "scan_" + UUID.randomUUID() + ".png";
        String outputPath = new File(dir, fileName).getAbsolutePath();

        // Lancement du scan via NAPS2
        ProcessBuilder builder = new ProcessBuilder(
                naps2Path,
                "scan",
                "--profile", profileName,
                "--output", outputPath
        );

        Process process = builder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0 || !new File(outputPath).exists()) {
            throw new RuntimeException("Erreur lors du scan (code=" + exitCode + ")");
        }

        // Lecture de l’image en bytes
        byte[] imageBytes = Files.readAllBytes(new File(outputPath).toPath());

        // Vérifie s’il existe déjà un scan pour ce chèque
        Optional<Scan> existingScan = scanRepository.findByCheque(cheque);
        Scan scan = existingScan.orElse(new Scan());

        scan.setCheque(cheque);
        scan.setFileName(fileName);
        scan.setFileType("image/png");
        scan.setImage(imageBytes);

        return scanRepository.save(scan);
    }

    public Scan getScanById(Long id) {
        return scanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scan introuvable"));
    }
}
