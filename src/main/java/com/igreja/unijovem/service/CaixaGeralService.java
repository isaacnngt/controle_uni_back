package com.igreja.unijovem.service;

import com.igreja.unijovem.entity.Parametros;
import com.igreja.unijovem.entity.PrestacaoContas;
import com.igreja.unijovem.entity.Usuario;
import com.igreja.unijovem.repository.ParametrosRepository;
import com.igreja.unijovem.repository.PrestacaoContasRepository;
import com.igreja.unijovem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CaixaGeralService {

    @Autowired
    private ParametrosRepository parametrosRepository;

    @Autowired
    private PrestacaoContasRepository prestacaoContasRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SaldoService saldoService;

    public Map<String, Object> buscarRelatorioCaixaGeral(Integer anoVigente) {
        Map<String, Object> relatorio = new HashMap<>();

        // Buscar parâmetros atuais
        Optional<Parametros> parametros = parametrosRepository.findLatest();
        if (!parametros.isPresent()) {
            throw new RuntimeException("Parâmetros financeiros não encontrados.");
        }

        Parametros params = parametros.get();
        BigDecimal valorMensalidade = params.getValorMensalidade();
        BigDecimal valorCamisa = params.getValorCamisa();

        // Contar total de usuários
        long totalUsuarios = usuarioRepository.count();

        // Calcular valores totais esperados
        BigDecimal totalEsperadoMensalidade = valorMensalidade.multiply(BigDecimal.valueOf(totalUsuarios));
        BigDecimal totalEsperadoCamisa = valorCamisa.multiply(BigDecimal.valueOf(totalUsuarios));

        // Buscar prestações do ano
        List<PrestacaoContas> prestacoes = prestacaoContasRepository.findByAnoVigente(anoVigente);

        // Calcular valores arrecadados
        BigDecimal totalArrecadadoMensalidade = prestacoes.stream()
                .map(p -> p.getValorMensalidadePago() != null ? p.getValorMensalidadePago() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalArrecadadoCamisa = prestacoes.stream()
                .map(p -> p.getValorCamisaPago() != null ? p.getValorCamisaPago() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Saldo atual
        BigDecimal saldoAtual = saldoService.getSaldoAtualValor();

        // Montar relatório
        relatorio.put("anoVigente", anoVigente);
        relatorio.put("valorMensalidade", valorMensalidade);
        relatorio.put("valorCamisa", valorCamisa);
        relatorio.put("totalUsuarios", totalUsuarios);
        relatorio.put("totalEsperadoMensalidade", totalEsperadoMensalidade);
        relatorio.put("totalEsperadoCamisa", totalEsperadoCamisa);
        relatorio.put("totalArrecadadoMensalidade", totalArrecadadoMensalidade);
        relatorio.put("totalArrecadadoCamisa", totalArrecadadoCamisa);
        relatorio.put("totalEsperadoGeral", totalEsperadoMensalidade.add(totalEsperadoCamisa));
        relatorio.put("totalArrecadadoGeral", totalArrecadadoMensalidade.add(totalArrecadadoCamisa));
        relatorio.put("saldoAtual", saldoAtual);

        return relatorio;
    }
}