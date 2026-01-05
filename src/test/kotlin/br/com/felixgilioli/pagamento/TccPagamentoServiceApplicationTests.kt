package br.com.felixgilioli.pagamento

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import java.net.HttpURLConnection
import java.net.URI

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TccPagamentoServiceApplicationTests {

	@LocalServerPort
	private var port: Int = 0

	@Test
	fun contextLoadsSuccessfully() {
		assertTrue(port > 0)
	}

	@Test
	fun healthEndpointReturnsOk() {
		val url = URI("http://localhost:$port/actuator/health").toURL()
		val conn = (url.openConnection() as HttpURLConnection)
		conn.requestMethod = "GET"
		conn.setRequestProperty("Accept", "application/json")

		val status = conn.responseCode
		val body = runCatching { conn.inputStream.bufferedReader().use { it.readText() } }
			.getOrElse { conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "" }

		assertTrue(status in 200..299)
		assertNotNull(body)
		assertTrue(body.contains("\"status\":\"UP\""))
	}

}
