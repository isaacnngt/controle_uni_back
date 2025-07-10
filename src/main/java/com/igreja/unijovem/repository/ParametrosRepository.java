package com.igreja.unijovem.repository;

import com.igreja.unijovem.entity.Parametros;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParametrosRepository extends JpaRepository<Parametros, Long> {

    // Usar Spring Data JPA nativo para buscar o primeiro ordenado por data
    List<Parametros> findTopByOrderByDataInclusaoDesc();

    @Query("SELECT COUNT(p) FROM Parametros p")
    long countParametros();

    // Método helper para buscar o mais recente
    default Optional<Parametros> findLatest() {
        List<Parametros> parametros = findTopByOrderByDataInclusaoDesc();
        return parametros.isEmpty() ? Optional.empty() : Optional.of(parametros.get(0));
    }
}