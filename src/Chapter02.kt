import Example.formatResult
import Example.factorial
import kotlin.collections.List

object Example {

    fun factorial(i: Int): Int {
        fun go(n: Int, acc: Int): Int =
            if (n <= 0) acc
            else go(n - 1, n * acc)
        return go(i, 1)
    }

    fun formatResult(name: String, number: Int, f: (Int) -> Int): String {
        return "The $name of $number is ${f(number)}"
    }
}

fun main() {
    println(formatResult("absolute value", -42) { if (it < 0) -it else it })
    println(formatResult("factorial", 7, ::factorial))
}

val <T> List<T>.tail: List<T>
    get() = drop(1)

val <T> List<T>.head: T
    get() = first()

fun <A> isSorted(aa: List<A>, ordered: (A, A) -> Boolean): Boolean = ordered(aa.first(), aa.tail.first())

fun <A, B, C> partial1(a: A, f: (A, B) -> C): (B) -> C = { b: B -> f(a, b) }
fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C = { partial1(it, f) }
fun <A, B, C> uncurry(f: (A) -> (B) -> C): (A, B) -> C = { a, b -> f(a)(b) }
fun <A, B, C> compose(f: (A) -> B, g: (B) -> C): (A) -> C = { g(f(it)) }
