package com.rxk.rxc_logging

import java.io.PrintWriter

/**
 * Created by richard on 03/01/2016.
 */


interface LogStream {
    fun apply(f: () -> String )
    fun apply(f: () -> String, t:Throwable)
}

class  LogStreamBase<T>(
        val ctx: T,
        val type: String,
        val h: (T, () -> String) -> String,
        val exh: (T, Throwable, () -> String) -> String,
        val sync: Boolean = true
 ) : LogStream {

    private var isEnabled = false;


    override fun apply(f: () -> String) {
        if (isEnabled) {
            syncMaybe(this, sync) {
                h(ctx, f);

            }
        }
    }

    override fun apply(f: () -> String, t: Throwable) {
        if (isEnabled) {
            syncMaybe(this, sync) {
                exh(ctx, t, f)
            }
        }
    }

   fun setEnabled(enabled:Boolean = true) {
        this.isEnabled = enabled
    }

}

inline fun syncMaybe(lock:Any, shouldSync: Boolean, f:() -> Unit) {
    if (shouldSync) {
        synchronized(lock, f);
    } else {
        f()
    }
}

data class LogSpec(val name: String, val type:String)

fun stdoutLogHandler(ctx:LogSpec, f:()->String) {
    println("[${ctx.type}] ${ctx.name}: ${f()}")
}

class StdLogger(name:String) {
    val info = LogStreamBase(LogSpec("INFO", name), (logSpec: LogSpec, f: ()-> String) {}, ff)

    private fun format() {

    }
}

