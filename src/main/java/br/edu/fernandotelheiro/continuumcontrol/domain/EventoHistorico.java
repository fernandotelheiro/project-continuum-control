package br.edu.fernandotelheiro.continuumcontrol.domain;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.ImportanciaEvento;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "evento_historico")
public class EventoHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "text")
    private String descricao;

    @Column(nullable = false)
    private OffsetDateTime dataEvento;

    @Column(nullable = false, length = 180)
    private String localizacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ImportanciaEvento importancia;

    protected EventoHistorico() {
    }

    public EventoHistorico(
            String titulo,
            String descricao,
            OffsetDateTime dataEvento,
            String localizacao,
            ImportanciaEvento importancia
    ) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataEvento = dataEvento;
        this.localizacao = localizacao;
        this.importancia = importancia;
    }


    public void atualizar(
            String titulo,
            String descricao,
            OffsetDateTime dataEvento,
            String localizacao,
            ImportanciaEvento importancia
    ) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataEvento = dataEvento;
        this.localizacao = localizacao;
        this.importancia = importancia;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public OffsetDateTime getDataEvento() {
        return dataEvento;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public ImportanciaEvento getImportancia() {
        return importancia;
    }
}