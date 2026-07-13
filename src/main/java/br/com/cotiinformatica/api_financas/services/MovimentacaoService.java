package br.com.cotiinformatica.api_financas.services;

import br.com.cotiinformatica.api_financas.dtos.CategoriaResponse;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoRequest;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoResponse;
import br.com.cotiinformatica.api_financas.entities.Movimentacao;
import br.com.cotiinformatica.api_financas.enums.TipoMovimentacao;
import br.com.cotiinformatica.api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.cotiinformatica.api_financas.exceptions.ValidacaoException;
import br.com.cotiinformatica.api_financas.repositories.CategoriaRepository;
import br.com.cotiinformatica.api_financas.repositories.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MovimentacaoService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    //Criando uma movimentação no banco de dados
    public MovimentacaoResponse criar(MovimentacaoRequest request) {

        //Verificando se a categoria existe no banco de dados
        var categoria  = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RegistroNaoEncontradoException("Categoria não encontrada"));

        //Executando as validacoes
        validarMovimentacao(request);

        //Criando um objeto da classe Movimentação
        var movimentacao = new Movimentacao();

        //Preenchendo os dados da movimentação
        movimentacao.setNome(request.nome());
        movimentacao.setData(request.data());
        movimentacao.setValor(BigDecimal.valueOf(request.valor()));
        movimentacao.setTipo(TipoMovimentacao.valueOf(request.tipo()));
        movimentacao.setCategoria(categoria);

        //Salvando a movimentação no banco de dados
        movimentacaoRepository.save(movimentacao);

        //Retornando os dados da movimentação criada
        return toResponse(movimentacao);
    }

    //Metodo para validar os dados da movimentacao
    public void validarMovimentacao(MovimentacaoRequest request) {
        if(request.nome() == null || request.nome().trim().isEmpty()) {
            throw new ValidacaoException("O nome da movimentação é obrigatório");
        }
        if(request.nome().length() < 6) {
            throw new ValidacaoException("O nome da movimentação deve ter pelo menos 6 caracteres");
        }
        if(request.valor().doubleValue() <= 0) {
            throw new ValidacaoException("O valor da movimentação deve ser maior que zero");
        }
        if(!request.tipo().toString().equals("DESPESA") && !request.tipo().toString().equals("RECEITA")) {
            throw new ValidacaoException("O tipo da movimentação deve ser DESPESA ou RECEITA");
        }

    }

    //Metodo para retornar os dados do DTO  de resposta da movimentacao
    public MovimentacaoResponse toResponse(Movimentacao movimentacao) {
        return new MovimentacaoResponse(
            movimentacao.getId(),
            movimentacao.getNome(),
            movimentacao.getData(),
            movimentacao.getValor().doubleValue(),
            movimentacao.getTipo().toString(),
            new CategoriaResponse(
                    movimentacao.getCategoria().getId(),
                    movimentacao.getCategoria().getNome()
            )
        );
    }
}
