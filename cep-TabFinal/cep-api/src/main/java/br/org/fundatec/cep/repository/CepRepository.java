package br.org.fundatec.cep.repository;

import br.org.fundatec.cep.model.Cep;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CepRepository extends CrudRepository<Cep, Integer> {
    List<Cep> findCepByCidadeContains(String cidade);
}
