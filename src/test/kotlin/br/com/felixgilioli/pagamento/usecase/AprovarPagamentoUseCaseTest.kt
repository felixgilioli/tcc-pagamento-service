package br.com.felixgilioli.pagamento.usecase

import br.com.felixgilioli.pagamento.entity.PagamentoORM
import br.com.felixgilioli.pagamento.enumeration.PagamentoStatus
import br.com.felixgilioli.pagamento.repository.PagamentoRepository
import br.com.felixgilioli.pagamento.repository.PedidoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class AprovarPagamentoUseCaseTest {

    private lateinit var pagamentoRepository: PagamentoRepository
    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var useCase: AprovarPagamentoUseCase

    private val pedidoId = UUID.randomUUID()
    private val pagamentoId = UUID.randomUUID()

    private val pagamento = PagamentoORM(
        pagamentoId,
        pedidoId.toString(),
        BigDecimal.ZERO,
        LocalDateTime.now(),
        PagamentoStatus.LINK_PAGAMENTO_GERADO,
        "http://link-pagamento.com"
    )

    @BeforeEach
    fun setUp() {
        pagamentoRepository = mockk()
        pedidoRepository = mockk()
        useCase = AprovarPagamentoUseCase(pagamentoRepository, pedidoRepository)
    }

    @Test
    fun `deve aprovar pagamento e publicar evento`() {
        every { pagamentoRepository.findByIdOrNull(pagamentoId) } returns pagamento
        every { pagamentoRepository.save(any()) } answers { firstArg() }
        every { pedidoRepository.updateStatus(pedidoId.toString(), "PAGAMENTO_APROVADO") } answers { }

        useCase.execute(pagamentoId)

        verify { pagamentoRepository.save(any()) }
    }

    @Test
    fun `deve lançar exceção se pagamento não encontrado`() {
        every { pagamentoRepository.findByIdOrNull(pagamentoId) } returns null

        val ex = assertThrows(IllegalArgumentException::class.java) {
            useCase.execute(pagamentoId)
        }
        assertEquals("Pagamento não encontrado", ex.message)
    }
}