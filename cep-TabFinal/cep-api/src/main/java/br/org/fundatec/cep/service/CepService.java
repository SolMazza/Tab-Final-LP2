package br.org.fundatec.cep.service;


import br.org.fundatec.cep.exception.RegistroNaoEcontradoException;
import br.org.fundatec.cep.model.Cep;
import br.org.fundatec.cep.model.Cidade;
import br.org.fundatec.cep.repository.CepRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class CepService {

    @Autowired
    private CepRepository cepRepository;

    public Cep salvar(@Valid Cep cep) {
        cepRepository.save(cep);
        return  cep;
    }

    public Cep busca(Integer numCep) {
        Optional<Cep> busca = cepRepository.findById(numCep);
        return busca.orElseThrow(() ->new RegistroNaoEcontradoException("Cep: "+numCep+" nao encontrado"));
    }

    public List<Cep> buscaTodos() {
        return  StreamSupport.stream(cepRepository.findAll().spliterator(), false).toList();
    }

    public List<Cep> busca(String nomeCidade) {
        return cepRepository.findCepByCidadeContains(nomeCidade);
    }


    public Cep editar(Integer numCep, Cep cep) {
        Cep cepBusca = this.busca(numCep);
        cepBusca.setLogradouro(cep.getLogradouro());
        cepBusca.setNumFim(cep.getNumFim());
        cepBusca.setNumInicio(cep.getNumInicio());

        cepBusca.setCidade(cep.getCidade());
        Cidade cidade = cepBusca.getCidade();
        cidade.setUf(cep.getCidade().getUf());

        cepRepository.save(cepBusca);

        return cepBusca;
    }

    public void remover(Integer numCep) {
        Cep cepBusca = this.busca(numCep);
        cepRepository.delete(cepBusca);
    }
}
