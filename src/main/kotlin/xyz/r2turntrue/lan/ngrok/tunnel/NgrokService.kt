package xyz.r2turntrue.lan.ngrok.tunnel

import com.google.gson.JsonParser
import java.io.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors


object NgrokService {

    var current: Process? = null

    internal class StreamGobbler
        (var `is`: InputStream) : Thread() {
        override fun run() {
            try {
                val isr = InputStreamReader(`is`)
                val br = BufferedReader(isr)
                var line: String? = null
                while (br.readLine().also { line = it } != null) println(line)
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }
        }
    }

    data class NgrokServiceResult(val url: String, val process: Process)

    private fun getLastLine(log: File): String? {
        val s = log.readLines()
        return if(s.isNotEmpty()) s[s.size - 1] else null
    }

    fun startNgrok(cli: String): CompletableFuture<NgrokServiceResult> {
        val future = CompletableFuture<NgrokServiceResult>()
        Executors.newCachedThreadPool().submit {
            try {
                var url = ""
                val rt = Runtime.getRuntime()
                println("Deleting Ngrok2 Log...")
                val log = File("${System.getProperty("user.home")}${File.separator}.ngrok2${File.separator}log.txt")
                if (log.exists())
                    log.delete()
                log.createNewFile()
                val p = rt.exec(cli)
                val errorGobbler = StreamGobbler(p.errorStream)
                val outputGobbler = StreamGobbler(p.inputStream)
                errorGobbler.start()
                outputGobbler.start()
                current = p

                while (true) {
                    var last: String? = getLastLine(log)
                    if (last != null) {
                        val json = JsonParser().parse(last).asJsonObject
                        if (json.has("addr") && json.has("url")) {
                            url = json.get("url").asString
                            break
                        }
                    }
                    Thread.sleep(20)
                }

                future.complete(NgrokServiceResult(url.replace("tcp://", ""), p))
            } catch (ex: Exception) {
                ex.printStackTrace()
                future.completeExceptionally(ex)
            }
        }
        return future
    }
}