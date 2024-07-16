package br.org.fundatec.cep.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "uf")
public class Uf extends Cidade {
    @Id
    @NotBlank
    @Column(name = "uf", nullable = false)
    private String uf;

    @NotBlank
    @Column(name = "nome", nullable = false)
    private String nome;

    @OneToMany(mappedBy = "uf")
    private Set<Cidade> cidades;

    // Getters and Setters
    public Uf getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<Cidade> getCidades() {
        return cidades;
    }

    public void setCidades(Set<Cidade> cidades) {
        this.cidades = cidades;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Uf uf1 = (Uf) o;
        return Objects.equals(uf, uf1.uf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uf);
    }
}
