package br.com.cotiinformatica.api_financas;

import br.com.cotiinformatica.api_financas.dtos.CategoriaRequest;
import br.com.cotiinformatica.api_financas.dtos.CategoriaResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiFinancasApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("Deve criar uma nova categoria com sucesso")
	public void criarCategoriaTest() throws Exception {

		//ARRANGE (Preparar os dados de teste)
		var request = new CategoriaRequest("Categoria teste");

		//ACT (Executar o endpoint POST /api/v1/categorias/criar)
		var result = mockMvc.perform(
				post("/api/v1/categorias/criar") //Requisição POST para a API
						.contentType("application/json") //Formato dos dados JSON
						.content(objectMapper.writeValueAsString(request))) //Dados enviados
					.andExpect(status().isCreated()) //Esperando retorno HTTP 201
					.andReturn(); //Capturando os dados da resposta

		//ASSERT (verificando o resultado do teste)
		var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var response = objectMapper.readValue(jsonContent, CategoriaResponse.class);

		//ASSERT: O ID da categoria deve vir preenchido com um UUID aleatorio
		assertNotNull(response.id());

		//ASSERT: O nome da categoria retornado deve ser igual ao enviado no cadastro
		assertEquals(request.nome(), response.nome());
	}

	@Test
	@DisplayName("Deve retornar erro se o nome da categoria estiver vazio")
	public void validarNomeDaCategoriaObrigatorioTest() throws Exception {

		//ARRANGE (Preparar os dados de teste)
		var request = new CategoriaRequest(""); //Nome da categoria vazio

		//ACT (Executar o endpoint POST /api/v1/categorias/criar)
		var result = mockMvc.perform(
						post("/api/v1/categorias/criar") //Requisição POST para a API
								.contentType("application/json") //Formato dos dados JSON
								.content(objectMapper.writeValueAsString(request))) //Dados enviados
				.andExpect(status().isBadRequest()) //Esperando retorno HTTP 400
				.andReturn(); //Capturando os dados da resposta

		//ASSERT (verificando o resultado do teste)
		var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(jsonContent.contains("O nome da categoria é obrigatório."));
	}

	@Test
	@DisplayName("Deve retornar erro se o nome da categoria tiver menos de 6 caracteres")
	public void validarNomeDaCategoriaMinimoDeCaracteres() throws Exception {

		//ARRANGE (Preparar os dados de teste)
		var request = new CategoriaRequest("Teste"); //Nome da categoria com menos de 6 caracteres

		//ACT (Executar o endpoint POST /api/v1/categorias/criar)
		var result = mockMvc.perform(
						post("/api/v1/categorias/criar") //Requisição POST para a API
								.contentType("application/json") //Formato dos dados JSON
								.content(objectMapper.writeValueAsString(request))) //Dados enviados
				.andExpect(status().isBadRequest()) //Esperando retorno HTTP 400
				.andReturn(); //Capturando os dados da resposta

		//ASSERT (verificando o resultado do teste)
		var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(jsonContent.contains("O nome da categoria deve ter no minimo 6 caracteres."));
	}

}
