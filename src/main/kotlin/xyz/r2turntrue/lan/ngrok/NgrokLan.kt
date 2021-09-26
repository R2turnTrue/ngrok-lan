package xyz.r2turntrue.lan.ngrok

import net.fabricmc.api.ModInitializer

@Suppress("UNUSED")
object NgrokLan: ModInitializer {

    private const val MOD_ID = "ngrok_lan"

    override fun onInitialize() {
        println("NgrokLan has been initialized!")
    }

}