package com.igreja.unijovem.repository;

import com.igreja.unijovem.entity.UsuarioLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioLoginRepository extends JpaRepository<UsuarioLogin, Long> {

    // Buscar por email para login
    Optional<UsuarioLogin> findByEmail(String email);

    // Buscar por email ativo
    @Query("SELECT ul FROM UsuarioLogin ul WHERE ul.email = :email AND ul.ativo = true")
    Optional<UsuarioLogin> findByEmailAndAtivoTrue(@Param("email") String email);

    // Verificar se email já existe
    boolean existsByEmail(String email);

    // Listar usuários ativos
    @Query("SELECT ul FROM UsuarioLogin ul WHERE ul.ativo = true ORDER BY ul.email")
    List<UsuarioLogin> findAllAtivos();

    // Listar administradores
    @Query("SELECT ul FROM UsuarioLogin ul WHERE ul.tipoUsuario = 'ADMIN' AND ul.ativo = true")
    List<UsuarioLogin> findAdministradores();

    // Buscar usuários bloqueados
    @Query("SELECT ul FROM UsuarioLogin ul WHERE ul.bloqueadoAte > :agora")
    List<UsuarioLogin> findBloqueados(@Param("agora") LocalDateTime agora);

    // Limpar bloqueios expirados
    @Modifying
    @Query("UPDATE UsuarioLogin ul SET ul.bloqueadoAte = null, ul.tentativasLogin = 0 WHERE ul.bloqueadoAte <= :agora")
    void limparBloqueiosExpirados(@Param("agora") LocalDateTime agora);

    // Buscar logins recentes
    @Query("SELECT ul FROM UsuarioLogin ul WHERE ul.ultimoLogin >= :dataLimite ORDER BY ul.ultimoLogin DESC")
    List<UsuarioLogin> findLoginsRecentes(@Param("dataLimite") LocalDateTime dataLimite);

    // Contar usuários por tipo
    @Query("SELECT ul.tipoUsuario, COUNT(ul) FROM UsuarioLogin ul WHERE ul.ativo = true GROUP BY ul.tipoUsuario")
    List<Object[]> countByTipoUsuario();

    // Buscar por tipo de usuário
    @Query("SELECT ul FROM UsuarioLogin ul WHERE ul.tipoUsuario = :tipo AND ul.ativo = true")
    List<UsuarioLogin> findByTipoUsuario(@Param("tipo") UsuarioLogin.TipoUsuario tipo);

    // Buscar usuários criados em período
    @Query("SELECT ul FROM UsuarioLogin ul WHERE ul.dataCriacao BETWEEN :inicio AND :fim")
    List<UsuarioLogin> findByDataCriacaoBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}