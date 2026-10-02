package br.edu.fernandotelheiro.continuumcontrol.historico;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document("historico_temporal")
public class RegistroHistorico {

    @Id
    private String id;

    @Indexed
    private Long missaoId;

    private String codigoMissao;
    private Long anomaliaId;
    private List<Long> agenteIds;
    private String tipo;
    private String statusMissao;
    private Instant instante;
    private Map<String, Object> detalhes;

    protected RegistroHistorico() {
    }

    public RegistroHistorico(
            Long missaoId,
            String codigoMissao,
            Long anomaliaId,
            List<Long> agenteIds,
            String tipo,
            String statusMissao,
            Instant instante,
            Map<String, Object> detalhes
    ) {
        this.missaoId = missaoId;
        this.codigoMissao = codigoMissao;
        this.anomaliaId = anomaliaId;
        this.agenteIds = agenteIds;
        this.tipo = tipo;
        this.statusMissao = statusMissao;
        this.instante = instante;
        this.detalhes = detalhes;
    }

    public String getId() {
        return id;
    }

    public Long getMissaoId() {
        return missaoId;
    }

    public String getCodigoMissao() {
        return codigoMissao;
    }

    public Long getAnomaliaId() {
        return anomaliaId;
    }

    public List<Long> getAgenteIds() {
        return agenteIds;
    }

    public String getTipo() {
        return tipo;
    }

    public String getStatusMissao() {
        return statusMissao;
    }

    public Instant getInstante() {
        return instante;
    }

    public Map<String, Object> getDetalhes() {
        return detalhes;
    }
}