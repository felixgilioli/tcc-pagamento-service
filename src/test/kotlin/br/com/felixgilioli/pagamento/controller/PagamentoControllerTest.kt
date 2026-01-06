package br.com.felixgilioli.pagamento.controller

import br.com.felixgilioli.pagamento.dto.Pagamento
import br.com.felixgilioli.pagamento.enumeration.PagamentoStatus
import br.com.felixgilioli.pagamento.usecase.AprovarPagamentoUseCase
import br.com.felixgilioli.pagamento.usecase.BuscarPagamentoByPedidoUseCase
import br.com.felixgilioli.pagamento.usecase.RecusarPagamentoUseCase
import br.com.felixgilioli.pagamento.usecase.SolicitarPagamentoUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import java.math.BigDecimal
import java.util.*

class PagamentoControllerTest {

    @Test
    fun `getPagamento deve retornar 200 com body quando existir pagamento`() {
        val buscarUseCase = mock(BuscarPagamentoByPedidoUseCase::class.java)
        val aprovarUseCase = mock(AprovarPagamentoUseCase::class.java)
        val recusarUseCase = mock(RecusarPagamentoUseCase::class.java)
        val solicitarUseCase = mock(SolicitarPagamentoUseCase::class.java)

        val controller = PagamentoController(
            buscarPagamentoByPedidoUseCase = buscarUseCase,
            aprovarPagamentoUseCase = aprovarUseCase,
            recusarPagamentoUseCase = recusarUseCase,
            solicitarPagamentoUseCase = solicitarUseCase
        )

        val pedidoId = "PED-1"
        val pagamento = Pagamento(
            id = UUID.fromString("11111111-1111-1111-1111-111111111111"),
            pedidoId = pedidoId,
            valor = BigDecimal("10.50"),
            status = PagamentoStatus.PAGAMENTO_APROVADO,
            link = "http://link"
        )

        `when`(buscarUseCase.execute(pedidoId)).thenReturn(pagamento)

        val response = controller.getPagamento(pedidoId)

        assertEquals(200, response.statusCode.value())
        assertEquals(pagamento.id.toString(), response.body?.pagamentoId)
        assertEquals(pedidoId, response.body?.pedidoId)
        assertEquals(pagamento.valor, response.body?.valor)
        assertEquals(pagamento.status.name, response.body?.status)
        assertEquals(pagamento.link, response.body?.link)

        verify(buscarUseCase).execute(pedidoId)
    }

    @Test
    fun `getPagamento deve retornar 404 quando nao existir pagamento`() {
        val buscarUseCase = mock(BuscarPagamentoByPedidoUseCase::class.java)
        val aprovarUseCase = mock(AprovarPagamentoUseCase::class.java)
        val recusarUseCase = mock(RecusarPagamentoUseCase::class.java)
        val solicitarUseCase = mock(SolicitarPagamentoUseCase::class.java)

        val controller = PagamentoController(
            buscarPagamentoByPedidoUseCase = buscarUseCase,
            aprovarPagamentoUseCase = aprovarUseCase,
            recusarPagamentoUseCase = recusarUseCase,
            solicitarPagamentoUseCase = solicitarUseCase
        )

        val pedidoId = "PED-404"
        `when`(buscarUseCase.execute(pedidoId)).thenReturn(null)

        val response = controller.getPagamento(pedidoId)

        assertEquals(404, response.statusCode.value())
        assertNull(response.body)
        verify(buscarUseCase).execute(pedidoId)
    }

    @Test
    fun `aprovarPagamento deve chamar usecase com UUID convertido e retornar 204`() {
        val buscarUseCase = mock(BuscarPagamentoByPedidoUseCase::class.java)
        val aprovarUseCase = mock(AprovarPagamentoUseCase::class.java)
        val recusarUseCase = mock(RecusarPagamentoUseCase::class.java)
        val solicitarUseCase = mock(SolicitarPagamentoUseCase::class.java)

        val controller = PagamentoController(
            buscarPagamentoByPedidoUseCase = buscarUseCase,
            aprovarPagamentoUseCase = aprovarUseCase,
            recusarPagamentoUseCase = recusarUseCase,
            solicitarPagamentoUseCase = solicitarUseCase
        )

        val pagamentoId = "22222222-2222-2222-2222-222222222222"

        val response = controller.aprovarPagamento(pagamentoId)

        assertEquals(204, response.statusCode.value())
    }

    @Test
    fun `recusarPagamento deve chamar usecase com UUID convertido e retornar 204`() {
        val buscarUseCase = mock(BuscarPagamentoByPedidoUseCase::class.java)
        val aprovarUseCase = mock(AprovarPagamentoUseCase::class.java)
        val recusarUseCase = mock(RecusarPagamentoUseCase::class.java)
        val solicitarUseCase = mock(SolicitarPagamentoUseCase::class.java)

        val controller = PagamentoController(
            buscarPagamentoByPedidoUseCase = buscarUseCase,
            aprovarPagamentoUseCase = aprovarUseCase,
            recusarPagamentoUseCase = recusarUseCase,
            solicitarPagamentoUseCase = solicitarUseCase
        )

        val pagamentoId = "33333333-3333-3333-3333-333333333333"

        val response = controller.recusarPagamento(pagamentoId)

        assertEquals(204, response.statusCode.value())
    }

    @Test
    fun `solicitarPagamento deve chamar usecase com parametros e retornar 204`() {
        val buscarUseCase = mock(BuscarPagamentoByPedidoUseCase::class.java)
        val aprovarUseCase = mock(AprovarPagamentoUseCase::class.java)
        val recusarUseCase = mock(RecusarPagamentoUseCase::class.java)
        val solicitarUseCase = mock(SolicitarPagamentoUseCase::class.java)

        val controller = PagamentoController(
            buscarPagamentoByPedidoUseCase = buscarUseCase,
            aprovarPagamentoUseCase = aprovarUseCase,
            recusarPagamentoUseCase = recusarUseCase,
            solicitarPagamentoUseCase = solicitarUseCase
        )

        val pedidoId = "PED-9"
        val valorTotal = BigDecimal("99.90")

        val response = controller.solicitarPagamento(pedidoId, valorTotal)

        assertEquals(204, response.statusCode.value())

        val pedidoCaptor = ArgumentCaptor.forClass(String::class.java)
        val valorCaptor = ArgumentCaptor.forClass(BigDecimal::class.java)
        assertEquals(pedidoId, pedidoCaptor.value)
        assertEquals(valorTotal, valorCaptor.value)
    }
}