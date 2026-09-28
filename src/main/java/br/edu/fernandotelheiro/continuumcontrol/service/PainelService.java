package br.edu.fernandotelheiro.continuumcontrol.service;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.ImpactoIntervencao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.RiscoAnomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAgente;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAnomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusMissao;
import br.edu.fernandotelheiro.continuumcontrol.dto.EstabilidadeResponse;
import br.edu.fernandotelheiro.continuumcontrol.dto.EstadoOperacionalResponse;
import br.edu.fernandotelheiro.continuumcontrol.repository.AgenteRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.AnomaliaRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.IntervencaoRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.MissaoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PainelService {

    private static final List<StatusAnomalia> STATUS_ENCERRADOS =
            List.of(
                    StatusAnomalia.ESTABILIZADA,
                    StatusAnomalia.IRREVERSIVEL
            );

    private final AgenteRepository agenteRepository;
    private final MissaoRepository missaoRepository;
    private final AnomaliaRepository anomaliaRepository;
    private final IntervencaoRepository intervencaoRepository;

    public PainelService(
            AgenteRepository agenteRepository,
            MissaoRepository missaoRepository,
            AnomaliaRepository anomaliaRepository,
            IntervencaoRepository intervencaoRepository
    ) {
        this.agenteRepository = agenteRepository;
        this.missaoRepository = missaoRepository;
        this.anomaliaRepository = anomaliaRepository;
        this.intervencaoRepository = intervencaoRepository;
    }

    @Cacheable("estado-operacional")
    @Transactional(readOnly = true)
    public EstadoOperacionalResponse estadoOperacional() {

        long agentesDisponiveis =
                agenteRepository.countByStatus(
                        StatusAgente.DISPONIVEL
                );

        long agentesEmMissao =
                agenteRepository.countByStatus(
                        StatusAgente.EM_MISSAO
                );

        MetricasEstabilidade metricas =
                obterMetricasEstabilidade();

        CalculoEstabilidade calculo =
                calcularEstabilidade(metricas);

        return new EstadoOperacionalResponse(
                agentesDisponiveis,
                agentesEmMissao,
                metricas.missoesEmExecucao(),
                metricas.anomaliasAtivas(),
                metricas.anomaliasCriticasAtivas(),
                calculo.indice(),
                calculo.rotulo()
        );
    }

    @Cacheable("estabilidade")
    @Transactional(readOnly = true)
    public EstabilidadeResponse estabilidade() {

        MetricasEstabilidade metricas =
                obterMetricasEstabilidade();

        CalculoEstabilidade calculo =
                calcularEstabilidade(metricas);

        return new EstabilidadeResponse(
                calculo.indice(),
                calculo.rotulo(),
                metricas.anomaliasCriticasAtivas(),
                metricas.anomaliasAtivas(),
                metricas.missoesEmExecucao(),
                metricas.missoesFalhas(),
                metricas.intervencoesSeverasUltimos30Dias()
        );
    }

    private MetricasEstabilidade obterMetricasEstabilidade() {

        long anomaliasAtivas =
                anomaliaRepository.countByStatusNotIn(
                        STATUS_ENCERRADOS
                );

        long anomaliasCriticasAtivas =
                anomaliaRepository.countByRiscoAndStatusNotIn(
                        RiscoAnomalia.CRITICO,
                        STATUS_ENCERRADOS
                );

        long missoesEmExecucao =
                missaoRepository.countByStatus(
                        StatusMissao.EM_EXECUCAO
                );

        long missoesFalhas =
                missaoRepository.countByStatus(
                        StatusMissao.FALHOU
                );

        OffsetDateTime trintaDiasAtras =
                OffsetDateTime.now().minusDays(30);

        long intervencoesSeverasUltimos30Dias =
                intervencaoRepository.countByImpactoAndInstanteAfter(
                        ImpactoIntervencao.SEVERO,
                        trintaDiasAtras
                );

        return new MetricasEstabilidade(
                anomaliasAtivas,
                anomaliasCriticasAtivas,
                missoesEmExecucao,
                missoesFalhas,
                intervencoesSeverasUltimos30Dias
        );
    }

    private CalculoEstabilidade calcularEstabilidade(
            MetricasEstabilidade metricas
    ) {

        long anomaliasAtivasNaoCriticas = Math.max(
                0,
                metricas.anomaliasAtivas()
                        - metricas.anomaliasCriticasAtivas()
        );

        long penalidade =
                15 * metricas.anomaliasCriticasAtivas()
                        + 5 * anomaliasAtivasNaoCriticas
                        + 3 * metricas.missoesEmExecucao()
                        + 10 * metricas.missoesFalhas()
                        + 8 * metricas.intervencoesSeverasUltimos30Dias();

        int indice = (int) Math.max(
                0,
                Math.min(100, 100 - penalidade)
        );

        String rotulo;

        if (indice >= 80) {
            rotulo = "ESTAVEL";
        } else if (indice >= 50) {
            rotulo = "INSTAVEL";
        } else {
            rotulo = "CRITICO";
        }

        return new CalculoEstabilidade(
                indice,
                rotulo
        );
    }

    private record MetricasEstabilidade(
            long anomaliasAtivas,
            long anomaliasCriticasAtivas,
            long missoesEmExecucao,
            long missoesFalhas,
            long intervencoesSeverasUltimos30Dias
    ) {
    }

    private record CalculoEstabilidade(
            int indice,
            String rotulo
    ) {
    }
}