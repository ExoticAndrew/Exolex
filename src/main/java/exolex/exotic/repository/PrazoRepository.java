package exolex.exotic.repository;

import exolex.exotic.model.Prazo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrazoRepository extends JpaRepository<Prazo, Long> {
    Page<Prazo> findByProcessoId(Long processoId, Pageable pageable);
}