package br.org.fundatec.cep.controller;

import br.org.fundatec.cep.CepApiApplication;
import br.org.fundatec.cep.exception.RegistroNaoEcontradoException;
import br.org.fundatec.cep.exception.handler.ErroResponse;
import br.org.fundatec.cep.model.Cep;
import br.org.fundatec.cep.service.CepService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.eq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = CepApiApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CepControllerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CepService cepService;

    private static final Integer CEP_INSERIR = 95680807;

    private Cep build(Integer cep, String uf, String cidade, String logradouro) {
        return new Cep(cep, cidade, uf, logradouro);
    }

    @Test
    void testaAdicaoCep()  throws Exception{
        Cep cep = build(CEP_INSERIR, "RS", "Canela", "Teste 1");


        doAnswer(invocation -> {
            return cep;
        }).when(cepService).salvar(eq(cep));


        MvcResult mvcResult =
                mockMvc.perform(post("/ceps").contentType("application/json")
                        .content(MAPPER.writeValueAsString(cep)))
                .andExpect(status().is(HttpStatus.CREATED.value())).andReturn();

        Cep retorno = parseResponse(mvcResult, Cep.class);

        verify(cepService, times(1)).salvar(eq(cep));

        assertThat("Não retornou o CEP Correto", retorno.getCep(), equalTo(CEP_INSERIR));
    }


    @Test
    void testaAdicaoCepInvalido()  throws Exception{
        Cep cep = build(15680807, "RS", "Canela", "Teste 1");

        MvcResult mvcResult = mockMvc.perform(post("/ceps").contentType("application/json")
                        .content(MAPPER.writeValueAsString(cep)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn();

        ErroResponse retorno = parseResponse(mvcResult, ErroResponse.class);
        assertThat("Não retornou a calidação correta", retorno.getMensagem(), containsString("Cep nulo ou em faixa invalida"));
    }

    @Test
    void testaAdicaoUFNulo()  throws Exception{
        Cep cep = build(15680807, null, "Canela", "Teste 1");
        MvcResult mvcResult = mockMvc.perform(post("/ceps").contentType("application/json")
                        .content(MAPPER.writeValueAsString(cep)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn();

        ErroResponse retorno = parseResponse(mvcResult, ErroResponse.class);
        assertThat("Não retornou a calidação correta", retorno.getMensagem(), containsString("uf - UF nao pode ser nulo"));
    }


    @Test
    void testaAdicaoUFTamanhoInvalido()  throws Exception{
        MvcResult mvcResult = mockMvc.perform(post("/ceps").contentType("application/json")
                        .content(MAPPER.writeValueAsString(build(95680807, "RSS", "Canela", "Teste 1"))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn();

        ErroResponse retorno = parseResponse(mvcResult, ErroResponse.class);
        assertThat("Não retornou a calidação correta", retorno.getMensagem(), containsString("UF nao pode ter mais de dois caracteres"));
    }


    @Test
    void testaAdicaoCidadeNula()  throws Exception{
        MvcResult mvcResult = mockMvc.perform(post("/ceps").contentType("application/json")
                        .content(MAPPER.writeValueAsString(build(95680807, "RS", null, "Teste 1"))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn();

        ErroResponse retorno = parseResponse(mvcResult, ErroResponse.class);
        assertThat("Não retornou a calidação correta", retorno.getMensagem(), containsString("Cidade nao pode ser nulo"));
    }

    @Test
    void testaLista()  throws Exception{
        List<Cep> resultados = new ArrayList<>();
        resultados.add(build(95680808, "RS", "Canela", null));

        doAnswer(invocation -> {
            return resultados;
        }).when(cepService).buscaTodos();

        MvcResult mvcResult =
                mockMvc.perform(get("/ceps")).andExpect(status().is(HttpStatus.OK.value())).andReturn();

        List<Cep> ceps = parseResponseList(mvcResult, Cep.class);
        assertThat("Não retornou quantidade correta", ceps.size(), is(1));

        Cep validar = build(95680808, null, null, null);


        verify(cepService, times(1)).buscaTodos();
        assertThat("Não retornou cep correto", ceps.get(0), equalTo(validar));
    }

    @Test
    void testaBuscaPorCep()  throws Exception{
        Cep retorno = build(95680808, null, null, null);

        doAnswer(invocation -> {
            return retorno;
        }).when(cepService).busca(eq(retorno.getCep()));

        MvcResult mvcResult =
                mockMvc.perform(get("/ceps/95680808")).andExpect(status().is(HttpStatus.OK.value())).andReturn();

        Cep cepRetorno = parseResponse(mvcResult, Cep.class);

        verify(cepService, times(1)).busca(eq(retorno.getCep()));
        assertThat("Não retornou cep correto", cepRetorno, equalTo(retorno));
    }


    @Test
    void testaBuscaPorCepNaoEcontrado()  throws Exception{
        doAnswer(invocation -> {
            throw  new RegistroNaoEcontradoException("Cep: 95680800 nao encontrado");
        }).when(cepService).busca(eq(95680800));

        MvcResult mvcResult =
                mockMvc.perform(get("/ceps/95680800")).andExpect(status().is(HttpStatus.NOT_FOUND.value())).andReturn();

        ErroResponse retorno = parseResponse(mvcResult, ErroResponse.class);

        verify(cepService, times(1)).busca(eq(95680800));
        assertThat("Não retornou a mensagem correta", retorno.getMensagem(), equalTo("Cep: 95680800 nao encontrado"));
    }


    @Test
    void testaBuscaPorCidade()  throws Exception{
        List<Cep> result = new ArrayList<>();
        result.add(build(95680808, null, null, null));

        doAnswer(invocation -> {
            return result;
        }).when(cepService).busca(eq("Cane"));

        MvcResult mvcResult =
                mockMvc.perform(get("/ceps/consulta?cidade=Cane")).andExpect(status().is(HttpStatus.OK.value())).andReturn();

        List<Cep> cepsRetorno = parseResponseList(mvcResult, Cep.class);

        Cep validar = build(95680808, null, null, null);


        verify(cepService, times(1)).busca(eq("Cane"));
        assertThat("Não retornou a quantidade correta", cepsRetorno.size(), equalTo(1));
        assertThat("Não retornou cep correto", cepsRetorno.get(0), equalTo(validar));
    }


    @Test
    void testaBuscaPorCidadeNaoEcontrada()  throws Exception{
        List<Cep> result = new ArrayList<>();

        doAnswer(invocation -> {
            return result;
        }).when(cepService).busca(eq("Porto"));


        MvcResult mvcResult =
                mockMvc.perform(get("/ceps/consulta?cidade=Porto")).andExpect(status().is(HttpStatus.OK.value())).andReturn();

        List<Cep> cepsRetorno = parseResponseList(mvcResult, Cep.class);

        verify(cepService, times(1)).busca(eq("Porto"));
        assertThat("Não retornou a quantidade correta", cepsRetorno.size(), equalTo(0));
    }


    @Test
    void testaEdicaoCep()  throws Exception{
        Cep cep = build(95680808, "RS", "Canela", "Teste 3");

        doAnswer(invocation -> {
            return cep;
        }).when(cepService).editar(eq(cep.getCep()), eq(cep));

        MvcResult mvcResult =
                mockMvc.perform(put("/ceps/95680808").contentType("application/json")
                                .content(MAPPER.writeValueAsString(build(95680808, "RS", "Canela", "Teste 3"))))
                        .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        Cep retorno = parseResponse(mvcResult, Cep.class);

        verify(cepService, times(1)).editar(eq(cep.getCep()), eq(cep));
        assertThat("Não retornou o CEP Correto", retorno.getCep(), equalTo(95680808));
        assertThat("Não fez a aleteracao", retorno.getLogradouro(), equalTo("Teste 3"));
    }


    @Test
    void testaEdicaoCepEncontrado()  throws Exception{
        Cep cep = build(95680800, "RS", "Canela", "Teste 3");

        doAnswer(invocation -> {
            throw  new RegistroNaoEcontradoException("Cep: 95680800 nao encontrado");
        }).when(cepService).editar(eq(95680800), eq(cep));

        MvcResult mvcResult =
                mockMvc.perform(put("/ceps/95680800").contentType("application/json")
                                .content(MAPPER.writeValueAsString(build(95680800, "RS", "Canela", "Teste 3"))))
                        .andExpect(status().is(HttpStatus.NOT_FOUND.value())).andReturn();

        verify(cepService, times(1)).editar(eq(95680800), eq(cep));
        ErroResponse retorno = parseResponse(mvcResult, ErroResponse.class);
        assertThat("Não retornou a mensagem correta", retorno.getMensagem(), equalTo("Cep: 95680800 nao encontrado"));
    }


    @Test
    void testaRemocaoCep()  throws Exception{

        doNothing().when(cepService).remover(eq(CEP_INSERIR));

        mockMvc.perform(post("/ceps").contentType("application/json")
                        .content(MAPPER.writeValueAsString(build(CEP_INSERIR, "RS", "Canela", "Teste 1"))))
                .andExpect(status().is(HttpStatus.CREATED.value())).andReturn();

        MvcResult mvcResult =
                mockMvc.perform(delete("/ceps/"+CEP_INSERIR).contentType("application/json"))
                        .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        verify(cepService, times(1)).remover(eq(CEP_INSERIR));
    }

    @Test
    void testaRemocaoCepEncontrado()  throws Exception{
        doAnswer(invocation -> {
            throw  new RegistroNaoEcontradoException("Cep: 95680800 nao encontrado");
        }).when(cepService).remover(eq(95680800));


        MvcResult mvcResult =
                mockMvc.perform(delete("/ceps/95680800").contentType("application/json"))
                        .andExpect(status().is(HttpStatus.NOT_FOUND.value())).andReturn();

        verify(cepService, times(1)).remover(eq(95680800));
        ErroResponse retorno = parseResponse(mvcResult, ErroResponse.class);
        assertThat("Não retornou a mensagem correta", retorno.getMensagem(), equalTo("Cep: 95680800 nao encontrado"));
    }



    @AfterEach
    public void tearDown()  throws Exception {
        MvcResult mvcResult =
                mockMvc.perform(get("/ceps/"+ CEP_INSERIR)).andReturn();

        if(mvcResult.getResponse().getStatus() != HttpStatus.NOT_FOUND.value()) {
            mockMvc.perform(delete("/ceps/"+CEP_INSERIR)).andReturn();
        }
    }


    private static <T> List<T> parseResponseList(MvcResult mockHttpServletResponse, Class<T> clazz) {
        try {
            String contentAsString = mockHttpServletResponse.getResponse().getContentAsString();
            return MAPPER.readValue(contentAsString, MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T parseResponse(MvcResult mockHttpServletResponse, Class<T> clazz) {
        try {
            String contentAsString = mockHttpServletResponse.getResponse().getContentAsString();
            return MAPPER.readValue(contentAsString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}