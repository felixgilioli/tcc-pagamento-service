package br.com.felixgilioli.pagamento.usecase

import br.com.felixgilioli.pagamento.entity.PagamentoORM
import br.com.felixgilioli.pagamento.enumeration.PagamentoStatus
import br.com.felixgilioli.pagamento.repository.PagamentoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class BuscarPagamentoByPedidoUseCaseTest {

    private lateinit var pagamentoRepository: PagamentoRepository
    private lateinit var useCase: BuscarPagamentoByPedidoUseCase

    private val pedidoId = UUID.randomUUID().toString()

    private val pagamentoOrm = PagamentoORM(
        id = UUID.randomUUID(),
        pedidoId = pedidoId,
        valor = BigDecimal.TEN,
        data = LocalDateTime.now(),
        status = PagamentoStatus.LINK_PAGAMENTO_GERADO,
        link = "http://link-pagamento.com"
    )

    @BeforeEach
    fun setUp() {
        pagamentoRepository = mockk()
        useCase = BuscarPagamentoByPedidoUseCase(pagamentoRepository)
    }

    @Test
    fun `deve buscar pagamento pelo pedidoId e retornar domain`() {
        every { pagamentoRepository.findFirstByPedidoIdOrderByDataDesc(pedidoId) } returns pagamentoOrm

        val result = useCase.execute(pedidoId)

        verify(exactly = 1) { pagamentoRepository.findFirstByPedidoIdOrderByDataDesc(pedidoId) }
        assertEquals(pagamentoOrm.toDomain(), result)
    }

    @Test
    fun `deve retornar null quando nao existir pagamento para o pedidoId`() {
        every { pagamentoRepository.findFirstByPedidoIdOrderByDataDesc(pedidoId) } returns null

        val result = useCase.execute(pedidoId)

        verify(exactly = 1) { pagamentoRepository.findFirstByPedidoIdOrderByDataDesc(pedidoId) }
        assertNull(result)
    }
}