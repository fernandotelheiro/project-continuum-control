package br.edu.fernandotelheiro.continuumcontrol.domain;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAgente;
import jakarta.persistence.*;

@Entity
@Table(name = "agente")
public class Agente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 120)
    private String especialidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusAgente status;

    @Version
    private Long versao;

    protected Agente() {
    }

    public Agente(String nome, String especialidade) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.status = StatusAgente.DISPONIVEL;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public StatusAgente getStatus() {
        return status;
    }

    public Long getVersao() {
        return versao;
    }
}