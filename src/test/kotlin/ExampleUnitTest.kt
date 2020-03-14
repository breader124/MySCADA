import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("unitTest")
class ExampleUnitTest {
    @Test
    fun exampleUnitTest() {
        assertThat(43 - 1).isEqualTo(42)
    }
}