package br.com.felixgilioli.pagamento.controller

import br.com.felixgilioli.pagamento.dto.response.PagamentoResponse
import br.com.felixgilioli.pagamento.dto.response.toResponse
import br.com.felixgilioli.pagamento.usecase.AprovarPagamentoUseCase
import br.com.felixgilioli.pagamento.usecase.BuscarPagamentoByPedidoUseCase
import br.com.felixgilioli.pagamento.usecase.RecusarPagamentoUseCase
import br.com.felixgilioli.pagamento.usecase.SolicitarPagamentoUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("/v1/pagamento")
@Tag(name = "Pagamento API", description = "Gerenciamento de pagamentos")
class PagamentoController(
    private val buscarPagamentoByPedidoUseCase: BuscarPagamentoByPedidoUseCase,
    private val aprovarPagamentoUseCase: AprovarPagamentoUseCase,
    private val recusarPagamentoUseCase: RecusarPagamentoUseCase,
    private val solicitarPagamentoUseCase: SolicitarPagamentoUseCase
) {

    @GetMapping
    @Operation(
        summary = "Obter pagamento por ID do pedido",
        description = "Recupera o pagamento associado a um pedido específico"
    )
    fun getPagamento(@RequestParam pedidoId: String): ResponseEntity<PagamentoResponse> =
        buscarPagamentoByPedidoUseCase.execute(pedidoId)?.toResponse()
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping("/webhook/aprovar")
    @Operation(summary = "Aprovar pagamento via webhook", description = "Aprova um pagamento recebido via webhook")
    fun aprovarPagamento(@RequestParam pagamentoId: String): ResponseEntity<PagamentoResponse> =
        aprovarPagamentoUseCase.execute(UUID.fromString(pagamentoId))
            .let { ResponseEntity.noContent().build() }

    @PostMapping("/webhook/recusar")
    @Operation(summary = "Recusar pagamento via webhook", description = "Recusa um pagamento recebido via webhook")
    fun recusarPagamento(@RequestParam pagamentoId: String): ResponseEntity<PagamentoResponse> =
        recusarPagamentoUseCase.execute(UUID.fromString(pagamentoId))
            .let { ResponseEntity.noContent().build() }

    @PostMapping("/solicitar")
    @Operation(summary = "Solicitar pagamento", description = "Solicita um pagamento para um pedido confirmado")
    fun solicitarPagamento(
        @RequestParam pedidoId: String,
        @RequestParam valorTotal: BigDecimal
    ): ResponseEntity<PagamentoResponse> =
        solicitarPagamentoUseCase.execute(pedidoId, valorTotal)
            .let { ResponseEntity.noContent().build() }
}