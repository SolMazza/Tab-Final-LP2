package br.org.fundatec.cep.model;

import br.org.fundatec.cep.annotation.CepValidation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.util.Objects;


@Entity
@Table(name = "cep")
public class Cep {
    @Id
    @CepValidation(message = "Cep nulo ou em faixa invalida")
    @Column(name = "num_cep", nullable = false)
    private Integer numCep;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_cidade", nullable = false)
    private Cidade cidade;

    @NotBlank
    @Column(name = "logradouro", nullable = false)
    private String logradouro;

    @NotNull
    @Column(name = "num_inicio", nullable = false)
    private Integer numInicio;

    @NotNull
    @Column(name = "num_fim", nullable = false)
    private Integer numFim;

    public Integer getNumCep() {
        return numCep;
    }

    public void setNumCep(Integer numCep) {
        this.numCep = numCep;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public Integer getNumInicio() {
        return numInicio;
    }

    public void setNumInicio(Integer numInicio) {
        this.numInicio = numInicio;
    }

    public Integer getNumFim() {
        return numFim;
    }

    public void setNumFim(Integer numFim) {
        this.numFim = numFim;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cep cep = (Cep) o;
        return Objects.equals(numCep, cep.numCep);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numCep);
    }


}
