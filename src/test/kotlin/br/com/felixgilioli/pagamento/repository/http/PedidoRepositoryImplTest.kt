package br.com.felixgilioli.pagamento.repository.http

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import java.net.URI

class PedidoRepositoryImplTest {

    @Test
    fun `deve chamar endpoint de status com PUT e query param status`() {
        val restTemplate = mock(RestTemplate::class.java)
        val repository = PedidoRepositoryImpl(restTemplate)

        val pedidoId = "123"
        val status = "APROVADO"

        repository.updateStatus(pedidoId, status)

        val uriCaptor = ArgumentCaptor.forClass(URI::class.java)

        verify(restTemplate).exchange(
            uriCaptor.capture(),
            eq(HttpMethod.PUT),
            eq(HttpEntity.EMPTY),
            eq(Void::class.java),
        )

        val uri = uriCaptor.value
        assertNotNull(uri)
        assertEquals("/v1/pedido/$pedidoId/status", uri.path)
        assertEquals("status=$status", uri.query)
    }

    @Test
    fun `deve codificar status na query quando possuir espacos`() {
        val restTemplate = mock(RestTemplate::class.java)
        val repository = PedidoRepositoryImpl(restTemplate)

        val pedidoId = "999"
        val status = "EM ANALISE"

        repository.updateStatus(pedidoId, status)

        val uriCaptor = ArgumentCaptor.forClass(URI::class.java)

        verify(restTemplate).exchange(
            uriCaptor.capture(),
            eq(HttpMethod.PUT),
            eq(HttpEntity.EMPTY),
            eq(Void::class.java),
        )

        val uri = uriCaptor.value
        assertEquals("/v1/pedido/$pedidoId/status", uri.path)
        assertEquals("status=EM%20ANALISE", uri.rawQuery)
    }
}