package xyz.r2turntrue.lan.ngrok.screen

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ScreenTexts
import net.minecraft.client.gui.screen.world.CreateWorldScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.NetworkUtils
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText
import net.minecraft.world.GameMode
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import xyz.r2turntrue.lan.ngrok.tunnel.NgrokService
import java.io.File

class NgrokScreen(val gameMode: GameMode, val allowCommands: Boolean, val parent: Screen): Screen(TranslatableText("ngrokLanServer.start")) {

    var commandLine: String = ""
    lateinit var f: TextFieldWidget

    override fun init() {
        val fileNgrokConfig = File("${System.getProperty("user.home")}${File.separator}.ngrok2${File.separator}ngrok.yml")
        val copied = fileNgrokConfig.copyTo(File("${System.getProperty("user.home")}${File.separator}.ngrok2${File.separator}ngrok_lan.yml"), true)
        val snakeYaml = Yaml(DumperOptions().apply {
            indent = 2
            isPrettyFlow = true
            defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        })
        val yaml = snakeYaml.load<Map<String, Any>>(copied.readText()).toMutableMap()
        yaml["log_format"] = "json"
        yaml["console_ui"] = false
        yaml["log"] = "${System.getProperty("user.home")}${File.separator}.ngrok2${File.separator}log.txt"
        snakeYaml.dump(yaml, copied.bufferedWriter())

        val i = NetworkUtils.findLocalPort()
        f = TextFieldWidget(textRenderer, this.width / 2 - 155, this.height - 28 - 20 - 8, 150 + 150, 20, TranslatableText("ngrok.commandline"))
        f.setMaxLength(300)
        f.text = "ngrok tcp -region=jp -config=\"${System.getProperty("user.home")}${File.separator}.ngrok2${File.separator}ngrok_lan.yml\" \${port}"
        val b = ButtonWidget(
            width / 2 - 155, height - 28, 150, 20, TranslatableText("lanServer.start")
        ) { button: ButtonWidget? ->
            this.client!!.setScreen(null as Screen?)
            var text2: TranslatableText
            if (this.client!!.server!!.openToLan(gameMode, this.allowCommands, i)) {
                val future = NgrokService.startNgrok(commandLine.replace("\${port}", "$i"))
                future.whenComplete { result, exception ->
                    if(exception != null) {
                        exception.printStackTrace()
                        text2 = TranslatableText("commands.publish.failed")
                        this.client!!.inGameHud.chatHud.addMessage(text2)
                        this.client!!.updateWindowTitle()
                        return@whenComplete
                    }
                    text2 = TranslatableText("ngrok.publish.started", result.url)
                    this.client!!.inGameHud.chatHud.addMessage(text2)
                    this.client!!.updateWindowTitle()
                }
            } else {
                text2 = TranslatableText("commands.publish.failed")
                this.client!!.inGameHud.chatHud.addMessage(text2)
                this.client!!.updateWindowTitle()
            }

        }
        commandLine = f.text
        f.setChangedListener { commandLine: String ->
            this.commandLine = commandLine
            b.active = f.text.isNotEmpty()
        }

        addDrawableChild(ButtonWidget(
            width / 2 + 5, height - 28, 150, 20, ScreenTexts.CANCEL
        ) { button: ButtonWidget? -> this.client!!.setScreen(this.parent) })
        addDrawableChild(b)
        addSelectableChild(f)
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        drawTextWithShadow(
            matrices,
            textRenderer, LiteralText("Ngrok Command Line"), this.width / 2 - 155, this.height - 28 - 20 - 8 - textRenderer.fontHeight, -6250336
        )
        f.render(matrices, mouseX, mouseY, delta)
        super.render(matrices, mouseX, mouseY, delta)
    }

}