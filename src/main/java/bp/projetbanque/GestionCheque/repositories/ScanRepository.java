package bp.projetbanque.GestionCheque.repositories;

import bp.projetbanque.GestionCheque.entities.Cheque;
import bp.projetbanque.GestionCheque.entities.Scan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScanRepository extends JpaRepository<Scan, Long> {
    Optional<Scan> findByCheque(Cheque cheque);
}
