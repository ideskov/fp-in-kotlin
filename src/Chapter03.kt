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

        tailrec fun <A, B> foldLeft(xs: List<A>, z: B, f: (B, A) -> B): B {
            return when (xs) {
                is Nil -> z
                is Cons -> foldLeft(xs.tail, f(z, xs.head), f)
            }
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
}
