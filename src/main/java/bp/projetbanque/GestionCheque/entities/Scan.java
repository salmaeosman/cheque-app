package bp.projetbanque.GestionCheque.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "scans")
public class Scan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cheque_id", nullable = false) // ce champ sera la clé étrangère
    private Cheque cheque;

    private String fileName;
    private String fileType;

    // Getters & Setters
    public Long getId() { return id; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }

    public Cheque getCheque() { return cheque; }
    public void setCheque(Cheque cheque) { this.cheque = cheque; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
}
