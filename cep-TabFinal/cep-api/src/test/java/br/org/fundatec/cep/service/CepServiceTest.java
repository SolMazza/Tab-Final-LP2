package br.org.fundatec.cep.service;

import br.org.fundatec.cep.CepApiApplication;
import br.org.fundatec.cep.exception.RegistroNaoEcontradoException;
import br.org.fundatec.cep.model.Cep;
import br.org.fundatec.cep.repository.CepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CepServiceTest {

    @Mock
    private CepRepository cepRepository;

    @InjectMocks
    private  CepService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void testaAdicao() {
        Cep inserir = build(9568000, "RS", "Canela", "Rua 1");

        doAnswer(invocation -> {
            return inserir;
        }).when(cepRepository).save(eq(inserir));

        Cep retorno = service.salvar(inserir);

        verify(cepRepository, times(1)).save(eq(inserir));
        assertThat("Não retornou o CEP Correto", retorno.getCep(), equalTo(inserir.getCep()));
    }


    @Test
    void testaListagem() {
        List<Cep> listagem = new ArrayList<>();
        listagem.add(build(9568000, "RS", "Canela", "Rua 1"));

        doAnswer(invocation -> {
            return listagem;
        }).when(cepRepository).findAll();

        List<Cep> retorno = service.buscaTodos();

        verify(cepRepository, times(1)).findAll();
        assertThat("Não retornou o CEP Correto", retorno.get(0).getCep(), equalTo(listagem.get(0).getCep()));
    }

    @Test
    void testaBuscaPorCep() {
        Optional<Cep> cep = Optional.of(build(9568000, "RS", "Canela", "Rua 1"));

        doAnswer(invocation -> {
            return cep;
        }).when(cepRepository).findById(9568000);

        Cep retorno = service.busca(9568000);

        verify(cepRepository, times(1)).findById(9568000);
        assertThat("Não retornou o CEP Correto", retorno.getCep(), equalTo(cep.get().getCep()));
    }


    @Test
    void testaBuscaCepNaoEcontrado() {
        Optional<Cep> cep = Optional.empty();

        doAnswer(invocation -> {
           return cep;
        }).when(cepRepository).findById(9568000);


        try {
            service.busca(9568000);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEcontradoException e) {
            assertThat("Não retornou o CEP Correto", e.getMessage(), equalTo("Cep: 9568000 nao encontrado"));
        }

        verify(cepRepository, times(1)).findById(9568000);
    }


    @Test
    void testaRemover() {
        Optional<Cep> cep = Optional.of(build(9568000, "RS", "Canela", "Rua 1"));

        doAnswer(invocation -> {
            return cep;
        }).when(cepRepository).findById(9568000);

        doNothing().when(cepRepository).delete(eq(cep.get()));

        service.remover(9568000);

        verify(cepRepository, times(1)).findById(9568000);
        verify(cepRepository, times(1)).delete(eq(cep.get()));
    }


    @Test
    void testaRemoverRegistroNaoEcontrado() {
        Optional<Cep> cep = Optional.empty();

        doAnswer(invocation -> {
            return cep;
        }).when(cepRepository).findById(9568000);
        
        try {
            service.remover(9568000);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEcontradoException e) {
            assertThat("Não retornou o CEP Correto", e.getMessage(), equalTo("Cep: 9568000 nao encontrado"));
        }

        verify(cepRepository, times(1)).findById(9568000);
    }







    private Cep build(Integer cep, String uf, String cidade, String logradouro) {
        return new Cep(cep, cidade, uf, logradouro);
    }
}
