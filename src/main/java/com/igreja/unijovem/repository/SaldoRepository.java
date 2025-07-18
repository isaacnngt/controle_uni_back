package com.igreja.unijovem.repository;

import com.igreja.unijovem.entity.Saldo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaldoRepository extends JpaRepository<Saldo, Long> {

    // Buscar o saldo atual (mais recente)
    @Query("SELECT s FROM Saldo s ORDER BY s.dataCriacao DESC, s.id DESC")
    Optional<Saldo> findSaldoAtual();

    // Contar quantos registros existem
    @Query("SELECT COUNT(s) FROM Saldo s")
    long countSaldos();
}