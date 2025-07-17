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

    @Column(nullable = false)
    private Long chequeId;

    private String fileName;
    private String fileType;

    // Getters & Setters

    public Long getId() { return id; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }

    public Long getChequeId() { return chequeId; }
    public void setChequeId(Long chequeId) { this.chequeId = chequeId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
}