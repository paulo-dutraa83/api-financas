package br.com.cotiinformatica.api_financas.dtos;

import java.time.LocalDateTime;

public record ErrorResponse(
        Integer status,
        String mensagem,
        LocalDateTime timestamp
) {
}