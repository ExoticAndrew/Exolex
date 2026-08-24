package exolex.exotic.repository;

import exolex.exotic.model.Notificacao;
import exolex.exotic.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUsuarioOrderByCriadoEmDesc(Usuario usuario);
    long countByUsuarioAndLidaFalse(Usuario usuario);
}