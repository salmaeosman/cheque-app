package bp.projetbanque.GestionCheque.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cheques")
public class Cheque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomCheque;

    private double montant;

    private LocalDate date;

    private String ville;

    private String serieNo;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client beneficiaire;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomCheque() { return nomCheque; }
    public void setNomCheque(String nomCheque) { this.nomCheque = nomCheque; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getSerieNo() { return serieNo; }
    public void setSerieNo(String serieNo) { this.serieNo = serieNo; }

    public Client getBeneficiaire() { return beneficiaire; }
    public void setBeneficiaire(Client beneficiaire) { this.beneficiaire = beneficiaire; }
}