package com.twotothirdpower.morkborgcharactersheet.database

import java.util.concurrent.Executors

// Let the database run callback on a separate thread
private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

fun ioThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}