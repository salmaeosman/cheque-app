package bp.projetbanque.GestionCheque.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cheques") // ✅ La contrainte unique a été supprimée ici
public class Cheque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomCheque;
    private String nomSerie;
    private double montant;
    private LocalDate date;
    private String ville;

    @Column(nullable = false)
    private Long numeroSerie;

    private String beneficiaire;

    @Column(length = 2)
    private String langue; // Ex: "fr" ou "ar"

    // Getters & Setters
    public Long getId() { return id; }

    public String getNomCheque() { return nomCheque; }
    public void setNomCheque(String nomCheque) { this.nomCheque = nomCheque; }

    public String getNomSerie() { return nomSerie; }
    public void setNomSerie(String nomSerie) { this.nomSerie = nomSerie; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public Long getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(Long numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getBeneficiaire() { return beneficiaire; }
    public void setBeneficiaire(String beneficiaire) { this.beneficiaire = beneficiaire; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }
}
