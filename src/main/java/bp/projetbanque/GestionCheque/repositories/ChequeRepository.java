package bp.projetbanque.GestionCheque.repositories;

import bp.projetbanque.GestionCheque.entities.Cheque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChequeRepository extends JpaRepository<Cheque, Long> {
	Optional<Cheque> findByNomChequeAndNomSerieAndNumeroSerie(String nomCheque, String nomSerie, Long numeroSerie);
	Optional<Cheque> findByNumeroSerie(Long numeroSerie); // 👈 Ajouté ici
}