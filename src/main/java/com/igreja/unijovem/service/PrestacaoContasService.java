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
import java.util.List;
import java.util.Optional;

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
}