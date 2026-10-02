package br.edu.fernandotelheiro.continuumcontrol.evento;

import br.edu.fernandotelheiro.continuumcontrol.historico.HistoricoRepository;
import br.edu.fernandotelheiro.continuumcontrol.historico.RegistroHistorico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ListenerHistorico {

    private static final Logger log =
            LoggerFactory.getLogger(ListenerHistorico.class);

    private final HistoricoRepository historicoRepository;
    private final CacheManager cacheManager;

    public ListenerHistorico(
            HistoricoRepository historicoRepository,
            CacheManager cacheManager
    ) {
        this.historicoRepository = historicoRepository;
        this.cacheManager = cacheManager;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void registrar(MissaoMudouDeEstado e) {

        try {
            RegistroHistorico registro =
                    new RegistroHistorico(
                            e.missaoId(),
                            e.codigo(),
                            e.anomaliaId(),
                            e.agenteIds(),
                            e.tipo(),
                            e.statusMissao().name(),
                            e.instante().toInstant(),
                            e.detalhes()
                    );

            historicoRepository.save(registro);

        } catch (RuntimeException ex) {
            log.error(
                    "Falha ao gravar histórico da missão {}",
                    e.missaoId(),
                    ex
            );
        }

        limparCache("estado-operacional");
        limparCache("estabilidade");
    }

    private void limparCache(String nome) {
        try {
            Cache cache = cacheManager.getCache(nome);

            if (cache != null) {
                cache.clear();
            }

        } catch (RuntimeException ex) {
            log.warn(
                    "Falha ao limpar cache {} após commit: {}",
                    nome,
                    ex.getMessage()
            );
        }
    }
}