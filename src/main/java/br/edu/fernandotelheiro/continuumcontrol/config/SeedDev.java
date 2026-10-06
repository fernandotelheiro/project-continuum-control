package br.edu.fernandotelheiro.continuumcontrol.config;

import br.edu.fernandotelheiro.continuumcontrol.domain.Agente;
import br.edu.fernandotelheiro.continuumcontrol.domain.Anomalia;
import br.edu.fernandotelheiro.continuumcontrol.domain.EventoHistorico;
import br.edu.fernandotelheiro.continuumcontrol.domain.Missao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.ImportanciaEvento;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.PrioridadeMissao;
import br.edu.fernandotelheiro.continuumcontrol.domain.enums.RiscoAnomalia;
import br.edu.fernandotelheiro.continuumcontrol.repository.AgenteRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.AnomaliaRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.EventoHistoricoRepository;
import br.edu.fernandotelheiro.continuumcontrol.repository.MissaoRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@Profile("dev")
@Order(1)
public class SeedDev implements ApplicationRunner {

    private final AgenteRepository agenteRepository;
    private final EventoHistoricoRepository eventoHistoricoRepository;
    private final AnomaliaRepository anomaliaRepository;
    private final MissaoRepository missaoRepository;

    public SeedDev(
            AgenteRepository agenteRepository,
            EventoHistoricoRepository eventoHistoricoRepository,
            AnomaliaRepository anomaliaRepository,
            MissaoRepository missaoRepository
    ) {
        this.agenteRepository = agenteRepository;
        this.eventoHistoricoRepository = eventoHistoricoRepository;
        this.anomaliaRepository = anomaliaRepository;
        this.missaoRepository = missaoRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        // Evita duplicar os dados ao reiniciar a aplicação.
        if (agenteRepository.count() > 0) {
            return;
        }

        // -------------------------------------------------
        // AGENTES
        // -------------------------------------------------

        Agente agente1 = new Agente(
                "Agente Um",
                "Operações Temporais"
        );

        Agente agente2 = new Agente(
                "Agente Dois",
                "Análise Cronológica"
        );

        Agente agente3 = new Agente(
                "Agente Três",
                "Contenção Temporal"
        );

        agente3.suspender();

        agenteRepository.save(agente1);
        agenteRepository.save(agente2);
        agenteRepository.save(agente3);

        // -------------------------------------------------
        // EVENTO HISTÓRICO
        // -------------------------------------------------

        EventoHistorico evento = new EventoHistorico(
                "Incidente temporal de referência",
                "Evento histórico utilizado como base para as anomalias do ambiente de desenvolvimento.",
                OffsetDateTime.now().minusYears(10),
                "Rio de Janeiro",
                ImportanciaEvento.ALTA
        );

        eventoHistoricoRepository.save(evento);

        // -------------------------------------------------
        // ANOMALIA DE RISCO BAIXO
        // -------------------------------------------------

        Anomalia anomaliaBaixa = new Anomalia(
                evento,
                "Pequena divergência detectada na linha temporal.",
                OffsetDateTime.now().minusDays(3),
                "Monitoramento automático",
                "Anomalia adequada para missão de demonstração.",
                RiscoAnomalia.BAIXO
        );

        // -------------------------------------------------
        // ANOMALIA CRÍTICA
        // -------------------------------------------------

        Anomalia anomaliaCritica = new Anomalia(
                evento,
                "Alteração temporal de alto impacto potencial.",
                OffsetDateTime.now().minusDays(2),
                "Sensor temporal",
                "Requer atenção prioritária.",
                RiscoAnomalia.CRITICO
        );

        // -------------------------------------------------
        // ANOMALIA IRREVERSÍVEL
        // -------------------------------------------------

        Anomalia anomaliaIrreversivel = new Anomalia(
                evento,
                "Desvio temporal cuja reversão não é mais possível.",
                OffsetDateTime.now().minusDays(1),
                "Análise histórica",
                "Mantida como exemplo de estado irreversível.",
                RiscoAnomalia.ALTO
        );

        anomaliaIrreversivel.marcarIrreversivel();

        anomaliaRepository.save(anomaliaBaixa);
        anomaliaRepository.save(anomaliaCritica);
        anomaliaRepository.save(anomaliaIrreversivel);

        // -------------------------------------------------
        // MISSÃO
        // -------------------------------------------------

        Missao missao = new Missao(
                anomaliaBaixa,
                "MIS-DEV-001",
                "Investigar e corrigir a divergência temporal de baixo risco.",
                OffsetDateTime.now().plusDays(30),
                PrioridadeMissao.NORMAL
        );

        // O agente 1 participa da missão,
        // mas continua DISPONÍVEL enquanto ela estiver PLANEJADA.
        missao.designar(agente1);

        missaoRepository.save(missao);
    }
}