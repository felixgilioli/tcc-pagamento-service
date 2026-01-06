package br.com.felixgilioli.pagamento.repository.http

import com.mercadopago.MercadoPagoConfig
import com.mercadopago.client.preference.PreferenceClient
import com.mercadopago.client.preference.PreferenceRequest
import com.mercadopago.resources.preference.Preference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockConstruction
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import java.math.BigDecimal

class CobrancaRepositoryImplTest {

    @Test
    fun `deve criar preferencia e retornar sandboxInitPoint`() {
        val accessToken = "access-token-teste"
        val valor = BigDecimal.TEN
        val expectedLink = "http://sandbox-link.mp"

        mockStatic(MercadoPagoConfig::class.java).use { mpConfigMock ->
            mpConfigMock.`when`<Any> { MercadoPagoConfig.setAccessToken(accessToken) }.then { null }

            mockConstruction(PreferenceClient::class.java) { constructed, _ ->
                val preference = mock(Preference::class.java)
                `when`(preference.sandboxInitPoint).thenReturn(expectedLink)

                val requestCaptor = ArgumentCaptor.forClass(PreferenceRequest::class.java)
                `when`(constructed.create(requestCaptor.capture())).thenReturn(preference)
            }.use { prefClientConstruction ->
                val repository = CobrancaRepositoryImpl(accessToken)

                val link = repository.gerarLink(valor)

                assertEquals(expectedLink, link)

                // valida que configurou o token
                mpConfigMock.verify { MercadoPagoConfig.setAccessToken(accessToken) }

                // valida o request construído
                val request = (prefClientConstruction.constructed().single() as PreferenceClient)
                    .let { client ->
                        val captor = ArgumentCaptor.forClass(PreferenceRequest::class.java)
                        org.mockito.Mockito.verify(client).create(captor.capture())
                        captor.value
                    }

                assertEquals(1, request.items.size)
                assertEquals("Checkout", request.items.first().title)
                assertEquals(1, request.items.first().quantity)
                assertEquals(valor, request.items.first().unitPrice)

                assertEquals("http://localhost:8080/sucesso", request.backUrls.success)
                assertEquals("http://localhost:8080/falha", request.backUrls.failure)
                assertEquals("http://localhost:8080/pendente", request.backUrls.pending)
            }
        }
    }
}