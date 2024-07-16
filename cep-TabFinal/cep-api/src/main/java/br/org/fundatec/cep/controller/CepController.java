package br.org.fundatec.cep.controller;


import br.org.fundatec.cep.exception.ParametroInvalidoException;
import br.org.fundatec.cep.exception.RegistroNaoEcontradoException;
import br.org.fundatec.cep.model.Cep;
import br.org.fundatec.cep.service.CepService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "ceps")
public class CepController {

    @Autowired
    private CepService cepService;

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<List<Cep>> get() {
        return ResponseEntity.status(HttpStatus.OK).body(cepService.buscaTodos());
    }

    @GetMapping(value = { "{cep}" }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Cep> get(@PathVariable("cep") Integer numCep) {
        Cep retorno = cepService.busca(numCep);
        return ResponseEntity.status(HttpStatus.OK).body(retorno);
    }

    @GetMapping(value={"consulta"},produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<List<Cep>> get(@RequestParam("cidade") String cidade) {
        return ResponseEntity.status(HttpStatus.OK).body(cepService.busca(cidade));
    }

    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Cep> adicionar(@RequestBody @Valid Cep cep) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cepService.salvar(cep));
    }

    @PutMapping(value = { "{cep}" },  consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Cep> editar(@PathVariable("cep") Integer numCep, @RequestBody Cep cep) {
        return ResponseEntity.status(HttpStatus.OK).body(cepService.editar(numCep, cep));
    }

    @DeleteMapping(value = { "{cep}" },
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Cep> remover(@PathVariable("cep") Integer numCep) {
        cepService.remover(numCep);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
