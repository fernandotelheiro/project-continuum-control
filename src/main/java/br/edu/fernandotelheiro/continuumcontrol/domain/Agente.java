package br.edu.fernandotelheiro.continuumcontrol.domain;

import br.edu.fernandotelheiro.continuumcontrol.domain.enums.StatusAgente;
import br.edu.fernandotelheiro.continuumcontrol.domain.exception.TransicaoInvalidaException;
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

    public boolean estaDisponivel() {
        return status == StatusAgente.DISPONIVEL;
    }

    public void entrarEmMissao() {
        if (status != StatusAgente.DISPONIVEL) {
            throw new TransicaoInvalidaException(
                    "Somente agente disponível pode entrar em missão."
            );
        }

        status = StatusAgente.EM_MISSAO;
    }

    public void liberar() {
        if (status != StatusAgente.EM_MISSAO) {
            throw new TransicaoInvalidaException(
                    "Somente agente em missão pode ser liberado."
            );
        }

        status = StatusAgente.DISPONIVEL;
    }

    public void suspender() {
        if (status != StatusAgente.DISPONIVEL) {
            throw new TransicaoInvalidaException(
                    "Somente agente disponível pode ser suspenso."
            );
        }

        status = StatusAgente.SUSPENSO;
    }

    public void atualizarCadastro(
            String nome,
            String especialidade
    ) {
        this.nome = nome;
        this.especialidade = especialidade;
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