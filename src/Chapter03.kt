sealed class List<out A> {
    companion object {

        fun <A> empty(): List<A> = Nil

        fun <A> of(vararg aa: A): List<A> {
            val tail = aa.sliceArray(1 until aa.size)
            return if (aa.isEmpty()) Nil else Cons(aa[0], of(*tail))
        }

        fun <A, B> foldRight(xs: List<A>, z: B, f: (A, B) -> B): B =
                when (xs) {
                    is Nil -> z
                    is Cons -> f(xs.head, foldRight(xs.tail, z, f))
                }

        tailrec fun <A, B> foldLeft(xs: List<A>, z: B, f: (B, A) -> B): B =
                when (xs) {
                    is Nil -> z
                    is Cons -> foldLeft(xs.tail, f(z, xs.head), f)
                }

        fun <A, B> foldRightL(xs: List<A>, z: B, f: (A, B) -> B): B =
                foldLeft(xs, { b: B -> b }) { g, a -> { g(f(a, it)) } }(z)

        fun <A, B> foldLeftR(xs: List<A>, z: B, f: (B, A) -> B): B =
                foldRight(xs, { b: B -> b }) { a, g -> { g(f(it, a)) } }(z)

        fun <A, B> foldRightDemystified(list: List<A>, identity: B, combiner: (A, B) -> B): B {
            val identityFunction: (B) -> B = { it }
            val delayer: ((B) -> B, A) -> (B) -> B = { delayedExec, a -> { delayedExec(combiner(a, it)) } }
            val chain = foldLeft(list, identityFunction, delayer)
            return chain(identity)
        }

        fun sum(ints: List<Int>): Int = foldRight(ints, 0) { a, b -> a + b }

        fun product(dbs: List<Double>): Double = foldRight(dbs, 1.0, { a, b -> a * b })
    }
}

fun <A> List<A>.tail(): List<A> =
        when (this) {
            is Nil -> Nil
            is Cons -> tail
        }

fun <A> List<A>.setHead(a: A): List<A> =
        when (this) {
            is Nil -> Cons(a, Nil)
            is Cons -> Cons(a, this)
        }

tailrec fun <A> List<A>.drop(numberOfElements: Int): List<A> {
    return if (numberOfElements <= 0 || this == Nil) this else this.tail().drop(numberOfElements - 1)
}

tailrec fun <A> List<A>.dropWhile(f: (A) -> Boolean): List<A> {
    return when (this) {
        is Nil -> this
        is Cons -> if (f(this.head)) this.tail.dropWhile(f) else this
    }
}

/**
 * Removes the last element from the list
 */
fun <A> List<A>.init(): List<A> {
    return when (this) {
        is Nil -> throw IllegalStateException()
        is Cons -> if (tail == Nil) Nil else Cons(head, tail.init())
    }
}

fun <A> List<A>.length(): Int = List.foldRight(this, 0) { _, acc -> acc + 1 }

fun List<Int>.sum(): Int = List.foldLeft(this, 0) { acc, new -> acc + new }

fun List<Double>.double(): Double = List.foldLeft(this, 1.0) { acc, new -> acc * new }

fun <A> List<A>.lengthWithFoldLeft(): Int = List.foldLeft(this, 0) { acc, _ -> acc + 1 }

fun <A> List<A>.reverse(): List<A> = List.foldLeft(this, List.empty()) { list, a -> list.setHead(a) }

fun <A> List<A>.append(xs: List<A>): List<A> = List.foldRight(this, xs) { x, y -> Cons(x, y) }

fun <A> List<List<A>>.concat(): List<A> = List.foldRight(this, List.empty()) { x, y -> x.append(y) }

/**
 * Exercise 3.15
 *
 * Write a function that transforms a list of integers by adding 1 to each element.
 * This should be a pure function that returns a new List.
 */
fun List<Int>.increment(): List<Int> =
        List.foldRight(this, List.empty()) { x, list -> Cons(x + 1, list) }

/**
 * Exercise 3.16
 *
 * Write a function that turns each value in a List<Double> into a String.
 * You can use the expression d.toString() to convert some d: Double to a String.
 */
fun List<Double>.mapToString(): List<String> =
        List.foldRight(this, List.empty()) { x, list -> Cons(x.toString(), list) }

/**
 * Exercise 3.17
 *
 * Write a function map that generalizes modifying each element in a list
 * while maintaining the structure of the list.
 */
fun <A, B> List<A>.map(mapper: (A) -> B): List<B> =
        List.foldRight(this, List.empty()) { x, acc -> Cons(mapper(x), acc) }

/**
 * Exercise 3.18
 *
 * Write a function filter that removes elements from a list unless they satisfy a given predicate.
 * Use it to remove all odd numbers from a List<Int>.
 */
fun <A> List<A>.filter(predicate: (A) -> Boolean): List<A> =
        List.foldRight(this, List.empty()) { x, acc -> if (predicate(x)) Cons(x, acc) else acc }

/**
 * Exercise 3.19
 *
 * Write a function flatMap that works like map except that the function given will return a list
 * instead of a single result, and that list should be inserted into the final resulting list.
 */
fun <A, B> List<A>.flatMap(mapper: (A) -> List<B>): List<B> =
        List.foldLeft(this, List.empty()) { acc, x -> acc.append(mapper(x)) }

/**
 * Exercise 3.20
 *
 * Use flatMap to implement filter.
 */
fun <A> List<A>.filterWithFlatMap(predicate: (A) -> Boolean): List<A> =
        this.flatMap { if (predicate(it)) List.of(it) else List.empty() }

object Nil : List<Nothing>()

data class Cons<out A>(val head: A, val tail: List<A>) : List<A>()

fun main() {
    val myList = List.of(1, 2, 3, 4)
    println(myList.drop(2))
    println(myList.dropWhile { it < 3 })
    println(myList.init())

    println(List.foldRight(myList, "") { number, acc -> acc + number })
    println(List.foldLeft(myList, "") { str, num -> str + num })
    println(myList.append(List.of(5, 6, 7)))

    val lists = List.of(myList, List.of(1, 2))
    println(lists.concat())

    println(List.foldRightL(myList, "s") { x, s -> s + x })
    println(myList.increment())
    println(myList.map { it + 1 })
    println(myList.filter { it % 2 == 0 })
    println(myList.filterWithFlatMap { it % 2 == 0 })
    println(myList.flatMap { List.of(it, it) })
}
