package com.igreja.unijovem.service;

import com.igreja.unijovem.entity.Parametros;
import com.igreja.unijovem.entity.PrestacaoContas;
import com.igreja.unijovem.entity.Usuario;
import com.igreja.unijovem.repository.PrestacaoContasRepository;
import com.igreja.unijovem.repository.UsuarioRepository;
import com.igreja.unijovem.repository.ParametrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PrestacaoContasService {

    @Autowired
    private PrestacaoContasRepository prestacaoContasRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ParametrosRepository parametrosRepository;

    public List<PrestacaoContas> listarTodas() {
        return prestacaoContasRepository.findAll();
    }

    public List<PrestacaoContas> listarPorAno(Integer anoVigente) {
        return prestacaoContasRepository.findByAnoVigente(anoVigente);
    }

    public List<PrestacaoContas> listarPorUsuario(Long usuarioId) {
        return prestacaoContasRepository.findByUsuarioId(usuarioId);
    }

    public Optional<PrestacaoContas> buscarPorUsuarioEAno(Long usuarioId, Integer anoVigente) {
        return prestacaoContasRepository.findByUsuarioIdAndAnoVigente(usuarioId, anoVigente);
    }

    public List<Integer> listarAnosVigentes() {
        return prestacaoContasRepository.findDistinctAnosVigentes();
    }

    public List<PrestacaoContas> listarComDividas() {
        return prestacaoContasRepository.findWithDebts();
    }

    public List<PrestacaoContas> listarComDividasPorAno(Integer anoVigente) {
        return prestacaoContasRepository.findWithDebtsByAno(anoVigente);
    }

    public PrestacaoContas salvar(PrestacaoContas prestacaoContas) {
        return prestacaoContasRepository.save(prestacaoContas);
    }

    public PrestacaoContas criarPrestacaoContas(Long usuarioId, Integer anoVigente) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);

        if (usuario.isPresent()) {
            Optional<PrestacaoContas> prestacaoExistente = buscarPorUsuarioEAno(usuarioId, anoVigente);

            if (prestacaoExistente.isPresent()) {
                return prestacaoExistente.get();
            }

            // Buscar parâmetros atuais para definir valores totais
            Optional<Parametros> parametros = parametrosRepository.findLatest();
            if (!parametros.isPresent()) {
                throw new RuntimeException("Parâmetros financeiros não encontrados. Configure os valores primeiro.");
            }

            PrestacaoContas prestacaoContas = new PrestacaoContas();
            prestacaoContas.setUsuario(usuario.get());
            prestacaoContas.setAnoVigente(anoVigente);

            // Definir valores totais automaticamente dos parâmetros
            prestacaoContas.setValorMensalidadeTotal(parametros.get().getValorMensalidade());
            prestacaoContas.setValorCamisaTotal(parametros.get().getValorCamisa());

            // Inicializar valores pagos como zero
            prestacaoContas.setValorCamisaPago(BigDecimal.ZERO);
            prestacaoContas.setValorMensalidadePago(BigDecimal.ZERO);

            return prestacaoContasRepository.save(prestacaoContas);
        }

        throw new RuntimeException("Usuário não encontrado com ID: " + usuarioId);
    }

    public PrestacaoContas atualizarPagamentos(Long prestacaoContasId, BigDecimal valorMensalidadePago, BigDecimal valorCamisaPago, String formaPagamento) {
        Optional<PrestacaoContas> prestacaoExistente = prestacaoContasRepository.findById(prestacaoContasId);

        if (prestacaoExistente.isPresent()) {
            PrestacaoContas prestacaoContas = prestacaoExistente.get();

            // Verificar se os valores totais estão definidos, se não, buscar dos parâmetros
            if (prestacaoContas.getValorMensalidadeTotal() == null || prestacaoContas.getValorCamisaTotal() == null) {
                Optional<Parametros> parametros = parametrosRepository.findLatest();
                if (parametros.isPresent()) {
                    if (prestacaoContas.getValorMensalidadeTotal() == null) {
                        prestacaoContas.setValorMensalidadeTotal(parametros.get().getValorMensalidade());
                    }
                    if (prestacaoContas.getValorCamisaTotal() == null) {
                        prestacaoContas.setValorCamisaTotal(parametros.get().getValorCamisa());
                    }
                }
            }

            if (valorMensalidadePago != null) {
                prestacaoContas.setValorMensalidadePago(valorMensalidadePago);
            }

            if (valorCamisaPago != null) {
                prestacaoContas.setValorCamisaPago(valorCamisaPago);
            }

            if (formaPagamento != null) {
                prestacaoContas.setFormaPagamento(formaPagamento);
            }

            PrestacaoContas prestacaoAtualizada = prestacaoContasRepository.save(prestacaoContas);

            // Atualizar status de dívidas do usuário
            atualizarStatusDividasUsuario(prestacaoContas.getUsuario().getId());

            return prestacaoAtualizada;
        }

        throw new RuntimeException("Prestação de contas não encontrada com ID: " + prestacaoContasId);
    }

    private void atualizarStatusDividasUsuario(Long usuarioId) {
        List<PrestacaoContas> prestacoesUsuario = prestacaoContasRepository.findByUsuarioId(usuarioId);

        boolean temDividas = prestacoesUsuario.stream().anyMatch(prestacao ->
                (prestacao.getPercentualMensalidadePago() != null && prestacao.getPercentualMensalidadePago().compareTo(BigDecimal.valueOf(100)) < 0) ||
                        (prestacao.getPercentualCamisaPago() != null && prestacao.getPercentualCamisaPago().compareTo(BigDecimal.valueOf(100)) < 0)
        );

        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            u.setDividas(temDividas);
            usuarioRepository.save(u);
        }
    }

    public void deletar(Long id) {
        if (prestacaoContasRepository.existsById(id)) {
            prestacaoContasRepository.deleteById(id);
        } else {
            throw new RuntimeException("Prestação de contas não encontrada com ID: " + id);
        }
    }

    public List<Map<String, Object>> buscarResumoGeral(Integer anoVigente) {
        // Buscar todos os usuários
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Buscar prestações do ano
        List<PrestacaoContas> prestacoes = prestacaoContasRepository.findByAnoVigente(anoVigente);

        // Criar mapa para acesso rápido
        Map<Long, PrestacaoContas> prestacoesPorUsuario = prestacoes.stream()
                .collect(Collectors.toMap(p -> p.getUsuario().getId(), p -> p));

        // Buscar parâmetros atuais
        Optional<Parametros> parametros = parametrosRepository.findLatest();

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            Map<String, Object> item = new HashMap<>();
            item.put("usuarioId", usuario.getId());
            item.put("usuarioNome", usuario.getNome());
            item.put("anoVigente", anoVigente);

            PrestacaoContas prestacao = prestacoesPorUsuario.get(usuario.getId());

            if (prestacao != null) {
                // Tem prestação
                item.put("prestacaoId", prestacao.getId());
                item.put("valorMensalidadeTotal", prestacao.getValorMensalidadeTotal());
                item.put("valorMensalidadePago", prestacao.getValorMensalidadePago());
                item.put("valorCamisaTotal", prestacao.getValorCamisaTotal());
                item.put("valorCamisaPago", prestacao.getValorCamisaPago());
                item.put("formaPagamento", prestacao.getFormaPagamento());
                item.put("percentualMens", prestacao.getPercentualMensalidadePago());
                item.put("percentualCamisa", prestacao.getPercentualCamisaPago());
            } else {
                // Não tem prestação
                item.put("prestacaoId", null);
                item.put("valorMensalidadeTotal", parametros.isPresent() ? parametros.get().getValorMensalidade() : BigDecimal.ZERO);
                item.put("valorMensalidadePago", BigDecimal.ZERO);
                item.put("valorCamisaTotal", parametros.isPresent() ? parametros.get().getValorCamisa() : BigDecimal.ZERO);
                item.put("valorCamisaPago", BigDecimal.ZERO);
                item.put("formaPagamento", null);
                item.put("percentualMens", BigDecimal.ZERO);
                item.put("percentualCamisa", BigDecimal.ZERO);
            }

            resultado.add(item);
        }

        // Ordenar por nome
        resultado.sort((a, b) -> ((String)a.get("usuarioNome")).compareToIgnoreCase((String)b.get("usuarioNome")));

        return resultado;
    }
}