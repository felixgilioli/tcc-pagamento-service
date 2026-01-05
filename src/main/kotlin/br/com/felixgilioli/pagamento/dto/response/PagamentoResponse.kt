package br.com.felixgilioli.pagamento.dto.response

import br.com.felixgilioli.pagamento.dto.Pagamento
import java.math.BigDecimal
import java.time.LocalDateTime

data class PagamentoResponse(
    val pagamentoId: String,
    val pedidoId: String,
    val data: LocalDateTime,
    val valor: BigDecimal,
    val status: String,
    val link: String
)

fun Pagamento.toResponse() = PagamentoResponse(
    pagamentoId = this.id.toString(),
    pedidoId = this.pedidoId,
    data = this.data,
    valor = this.valor,
    status = this.status.name,
    link = this.link
)