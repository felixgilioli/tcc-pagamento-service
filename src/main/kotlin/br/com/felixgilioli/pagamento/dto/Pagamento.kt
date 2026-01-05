package br.com.felixgilioli.pagamento.dto

import br.com.felixgilioli.pagamento.enumeration.PagamentoStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class Pagamento(
    val id: UUID? = null,
    val pedidoId: String,
    val valor: BigDecimal,
    val data: LocalDateTime = LocalDateTime.now(),
    val status: PagamentoStatus,
    val link: String
)