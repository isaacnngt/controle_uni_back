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
}