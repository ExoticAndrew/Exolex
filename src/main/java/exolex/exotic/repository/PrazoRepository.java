package exolex.exotic.repository;

import exolex.exotic.enums.StatusPrazo;
import exolex.exotic.model.Prazo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrazoRepository extends JpaRepository<Prazo, Long> {
    Page<Prazo> findByProcessoId(Long processoId, Pageable pageable);
    List<Prazo> findByProcessoId(Long processoId);

    List<Prazo> findTop10ByProcessoIdInAndStatusNotOrderByDataVencimentoAsc(
            List<Long> processoIds, StatusPrazo status);
}