package com.igreja.unijovem.repository;

import com.igreja.unijovem.entity.PrestacaoContas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrestacaoContasRepository extends JpaRepository<PrestacaoContas, Long> {

    @Query("SELECT pc FROM PrestacaoContas pc WHERE pc.usuario.id = :usuarioId")
    List<PrestacaoContas> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT pc FROM PrestacaoContas pc WHERE pc.anoVigente = :anoVigente")
    List<PrestacaoContas> findByAnoVigente(@Param("anoVigente") Integer anoVigente);

    @Query("SELECT pc FROM PrestacaoContas pc WHERE pc.usuario.id = :usuarioId AND pc.anoVigente = :anoVigente")
    Optional<PrestacaoContas> findByUsuarioIdAndAnoVigente(@Param("usuarioId") Long usuarioId, @Param("anoVigente") Integer anoVigente);

    @Query("SELECT DISTINCT pc.anoVigente FROM PrestacaoContas pc ORDER BY pc.anoVigente DESC")
    List<Integer> findDistinctAnosVigentes();

    @Query("SELECT pc FROM PrestacaoContas pc WHERE pc.percentualMensalidadePago < 100 OR pc.percentualCamisaPago < 100")
    List<PrestacaoContas> findWithDebts();

    @Query("SELECT pc FROM PrestacaoContas pc WHERE pc.anoVigente = :anoVigente AND (pc.percentualMensalidadePago < 100 OR pc.percentualCamisaPago < 100)")
    List<PrestacaoContas> findWithDebtsByAno(@Param("anoVigente") Integer anoVigente);


}