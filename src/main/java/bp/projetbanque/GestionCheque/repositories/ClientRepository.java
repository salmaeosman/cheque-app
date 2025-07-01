package bp.projetbanque.GestionCheque.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import bp.projetbanque.GestionCheque.entities.Client;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByNomAndPrenom(String nom, String prenom);
}