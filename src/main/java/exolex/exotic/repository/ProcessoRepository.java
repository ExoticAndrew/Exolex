package exolex.exotic.repository;

import exolex.exotic.model.Processo;
import exolex.exotic.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessoRepository extends JpaRepository<Processo, Long> {

    @Query("SELECT p FROM Processo p JOIN ProcessoUsuario pu ON pu.processo = p WHERE pu.usuario = :usuario")
    Page<Processo> findByUsuarioVinculado(@Param("usuario") Usuario usuario, Pageable pageable);
}