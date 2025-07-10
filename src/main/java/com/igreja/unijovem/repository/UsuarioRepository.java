package com.igreja.unijovem.repository;

import com.igreja.unijovem.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Usuario> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    @Query("SELECT u FROM Usuario u WHERE u.sexo = :sexo")
    List<Usuario> findBySexo(@Param("sexo") String sexo);

    @Query("SELECT u FROM Usuario u WHERE u.cargoMinisterial = true")
    List<Usuario> findByCargoMinisterialTrue();

    @Query("SELECT u FROM Usuario u WHERE u.dividas = true")
    List<Usuario> findByDividasTrue();

    @Query("SELECT u FROM Usuario u WHERE u.cargoUnijovem IS NOT NULL AND u.cargoUnijovem != ''")
    List<Usuario> findByCargoUnijovemNotEmpty();

    // === QUERIES DE ANIVERSARIANTES ===

    // Query simplificada para aniversariantes por mês
    @Query("SELECT u FROM Usuario u WHERE EXTRACT(MONTH FROM u.dataNascimento) = :mes AND u.dataNascimento IS NOT NULL ORDER BY EXTRACT(DAY FROM u.dataNascimento)")
    List<Usuario> findByMesAniversario(@Param("mes") Integer mes);

    // Query para todos com data de nascimento
    @Query("SELECT u FROM Usuario u WHERE u.dataNascimento IS NOT NULL ORDER BY EXTRACT(MONTH FROM u.dataNascimento), EXTRACT(DAY FROM u.dataNascimento)")
    List<Usuario> findAllWithDataNascimentoOrdered();

    // Query para aniversariantes de hoje
    @Query("SELECT u FROM Usuario u WHERE EXTRACT(MONTH FROM u.dataNascimento) = EXTRACT(MONTH FROM CURRENT_DATE) AND EXTRACT(DAY FROM u.dataNascimento) = EXTRACT(DAY FROM CURRENT_DATE)")
    List<Usuario> findAniversariantesHoje();

    // Query SIMPLIFICADA para próximos aniversariantes (sem lógica complexa de virada de ano)
    @Query(value = "SELECT * FROM usuario_tb u WHERE u.dt_nascimento IS NOT NULL ORDER BY u.dt_nascimento LIMIT :limite", nativeQuery = true)
    List<Usuario> findProximosAniversariantes(@Param("limite") Integer limite);
}