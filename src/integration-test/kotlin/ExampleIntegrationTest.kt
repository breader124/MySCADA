import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("integrationTest")
class ExampleIntegrationTest {
    @Test
    fun exampleIntegrationTest() {
        Assertions.assertThat(41 + 1).isEqualTo(42)
    }
}