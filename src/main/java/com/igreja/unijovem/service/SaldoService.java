package com.igreja.unijovem.service;

import com.igreja.unijovem.entity.Saldo;
import com.igreja.unijovem.repository.SaldoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class SaldoService {

    @Autowired
    private SaldoRepository saldoRepository;

    public Optional<Saldo> buscarSaldoAtual() {
        return saldoRepository.findSaldoAtual();
    }

    public Saldo atualizarSaldo(BigDecimal novoSaldo) {
        Optional<Saldo> saldoExistente = buscarSaldoAtual();

        if (saldoExistente.isPresent()) {
            // Atualizar saldo existente
            Saldo saldo = saldoExistente.get();
            saldo.setSaldo(novoSaldo);
            return saldoRepository.save(saldo);
        } else {
            // Criar primeiro saldo
            Saldo novoSaldoEntity = new Saldo();
            novoSaldoEntity.setSaldo(novoSaldo);
            return saldoRepository.save(novoSaldoEntity);
        }
    }

    public Saldo criarSaldoInicial() {
        if (saldoRepository.countSaldos() == 0) {
            Saldo saldoInicial = new Saldo();
            saldoInicial.setSaldo(BigDecimal.ZERO);
            return saldoRepository.save(saldoInicial);
        }
        return buscarSaldoAtual().orElse(null);
    }

    public BigDecimal getSaldoAtualValor() {
        Optional<Saldo> saldo = buscarSaldoAtual();
        return saldo.map(Saldo::getSaldo).orElse(BigDecimal.ZERO);
    }
}