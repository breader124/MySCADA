package elka.achlebos.viewmodel

import org.mockito.Mockito

// for mocking ordinary types
fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

// for mocking lambda expression used in subscribe function above
fun <T> anyObject(): T {
    Mockito.anyObject<T>()
    return uninitialized()
}

fun <T> uninitialized(): T = null as T