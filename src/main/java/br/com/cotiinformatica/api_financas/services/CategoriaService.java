package br.com.cotiinformatica.api_financas.services;

import br.com.cotiinformatica.api_financas.dtos.CategoriaRequest;
import br.com.cotiinformatica.api_financas.dtos.CategoriaResponse;
import br.com.cotiinformatica.api_financas.entities.Categoria;
import br.com.cotiinformatica.api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.cotiinformatica.api_financas.exceptions.ValidacaoException;
import br.com.cotiinformatica.api_financas.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public CategoriaResponse criar(CategoriaRequest request) {

        //Criando um objeto da entidade 'Categoria'
        var categoria = new Categoria();

        //Capturando os dados recebidos
        categoria.setNome(request.nome());

        //Executando a validacao
        validarCategoria(categoria);

        //Salvando a categoria no banco de dados
        categoriaRepository.save(categoria);

        //Retornando a resposta
        return toResponse(categoria);
    }

    public CategoriaResponse alterar(UUID id, CategoriaRequest request) {

        //Buscando a categoria no banco de dados atraves do ID
        var categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Categoria não encontrada."));

        //Capturando o nome da categoria que sera alterado
        categoria.setNome(request.nome());

        //Alterando no banco de dados
        categoriaRepository.save(categoria);

        //Retornando a resposta
        return toResponse(categoria);
    }

    public CategoriaResponse excluir(UUID id) {

        //Buscando a categoria no bando de dados atraves do ID
        var categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Categoria não encontrada."));

        //Excluindo no bando de dados
        categoriaRepository.delete(categoria);

        //Retornando a resposta
        return toResponse(categoria);
    }

    public List<CategoriaResponse> consultar() {

        //Consultar todas as categorias cadstradas
        var categorias = categoriaRepository.findAll();

        //Copiando cada categoria da lista obtida do banco de dados
        //para uma lista do DTO CategoriaResponse
        return categorias.stream()
                .map(this::toResponse) //Metodo para pegar cada categoria de uma lista e converter para a lista de DTO
                .toList();
    }

    private void validarCategoria(Categoria categoria) {
        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O nome da categoria é obrigatório.");
        }
        if (categoria.getNome().length() < 6) {
            throw new ValidacaoException("O nome da categoria deve ter no minimo 6 caracteres.");
        }
    }

    private CategoriaResponse toResponse(Categoria categoria) {

        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome()
        );
    }

}
