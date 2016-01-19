package com.rxk.rxc_logging

import java.io.PrintWriter

/**
 * Created by richard on 03/01/2016.
 */


interface LogStream {
    operator fun invoke(f: () -> String)
    operator fun invoke( t: Throwable, f: () -> String)
    var enabled: Boolean
}

class LogStreamBase(
        val name: String,
        val level: String,
        val h: (String, String, () -> String) -> Unit,
        val exh: (String, String, Throwable, () -> String) -> Unit,
        val sync: Boolean = true,
        override var enabled: Boolean = true
) : LogStream {


    final override fun invoke(f: () -> String) {
        if (enabled) {
            syncMaybe(this, sync) {
                h(name, level, f);

            }
        }
    }

    final override fun invoke(t: Throwable, f: () -> String) {
        if (enabled) {
            syncMaybe(this, sync) {
                exh(name, level, t, f)
            }
        }
    }
}

inline fun syncMaybe(lock: Any, shouldSync: Boolean, f: () -> Unit) {
    if (shouldSync) {
        synchronized(lock, f);
    } else {
        f()
    }
}


interface StdLogger {
    val info: LogStream
    val error: LogStream
    val debug: LogStream
}

val sysOutPrintWriter = PrintWriter(System.out)

val _formatPw = {
    pw: PrintWriter, name: String, level: String, f: () -> String ->
    pw.println("${name} [${level}] ${f()}")
    pw.flush()
}

val _formatPwE = {
    pw: PrintWriter, name: String, level: String, t: Throwable, f: () -> String ->
    pw.println("${name} [${level}] ${f()}")
    t.printStackTrace(pw)
    pw.flush()
}

class StdLoggerImpl(name: String,
                    val formatPw: (PrintWriter, String, String, () -> String) -> Unit = _formatPw,
                    val formatPwE: (PrintWriter, String, String, t: Throwable, () -> String) -> Unit = _formatPwE,
                    val sync: Boolean = true,
                    val pw: PrintWriter = sysOutPrintWriter) : StdLogger {

    val format: (String, String, () -> String) -> Unit = { name: String, level: String, f: () -> String -> formatPw(pw, name, level, f) }
    val formath: (String, String, Throwable, () -> String) -> Unit = { name: String, level: String, t: Throwable, f: () -> String -> formatPwE(pw, name, level, t, f) }
    override val info = LogStreamBase(name, "INFO", format, formath, sync)
    override val debug = LogStreamBase(name, "DEBUG", format, formath, sync)
    override val error = LogStreamBase(name, "ERROR", format, formath, sync)
}

fun main(args: Array<String>) {
    val log: StdLogger = StdLoggerImpl(name = "Test")

    log.debug { "Hello Word" }
    log.debug.enabled = false
    log.debug { "Hello Word Again" }

    log.debug.enabled = true
    log.debug (Exception(), {" Some Exception" })

}

