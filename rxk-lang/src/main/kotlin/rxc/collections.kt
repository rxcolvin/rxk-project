/**
 * Created by richard.colvin on 24/12/2015.
 */

package rxk.collections


import java.util.ArrayList
import java.util.HashMap


class L<T> private constructor (args:List<T>) : List<T> by  args {

    constructor(vararg  args:T) : this(args.asList())

    class B<T> : CollectionBuilder<T, L<T>> {
        val list = ArrayList<T>();

        override fun add(t: T) {
            list.add(t)
        }

        override fun build(): L<T> {
            return L(list)
        }
    }
}

class M<K, V> private constructor(args: Map<K,V>) : Map<K,V> by args {

    constructor(vararg args:Pair<K,V>) : this(mapOf(args))

    class B<K, V> : CollectionBuilder<Pair<K,V>, M<K,V>> {
        val list = HashMap<K, V>();

        override fun add(t: Pair<K, V>) {
            list.put(t.first, t.second);
        }

        override fun build(): M<K, V> {
            return M(list)
        }
    }
}

fun <K, V> mapOf(args:Array<out Pair<K,V>>) : Map<K,V> {
    val ret = HashMap<K, V>(args.size);
    args.forEach { ret.put(it.first, it.second) }
    return ret;
}


interface CollectionBuilder<R, X> {
    fun add(t:R)
    fun build(): X
}

public inline fun <T, R, X, C : CollectionBuilder <in R, X>> Iterable<T>.map(builder: C, transform: (T) -> R): X {
    for (item in this)
        builder.add(transform(item))
    return builder.build()
}
