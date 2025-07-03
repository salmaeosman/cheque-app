package bp.projetbanque.GestionCheque.repositories;

import bp.projetbanque.GestionCheque.entities.Cheque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChequeRepository extends JpaRepository<Cheque, Long> {
}