package exolex.exotic.repository;

import exolex.exotic.model.Processo;
import exolex.exotic.model.ProcessoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessoUsuarioRepository extends JpaRepository<ProcessoUsuario, Long> {

    Optional<ProcessoUsuario> findByProcessoIdAndUsuarioId(Long processoId, Long usuarioId);

    List<ProcessoUsuario> findByProcesso(Processo processo);

    boolean existsByProcessoIdAndUsuarioId(Long processoId, Long usuarioId);
}