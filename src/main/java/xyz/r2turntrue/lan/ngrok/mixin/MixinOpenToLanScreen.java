package xyz.r2turntrue.lan.ngrok.mixin;

import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.r2turntrue.lan.ngrok.screen.NgrokScreen;

@Mixin(OpenToLanScreen.class)
public class MixinOpenToLanScreen extends Screen {
    @Shadow
    private boolean allowCommands;
    @Shadow
    private GameMode gameMode;

    @Shadow @Final private Screen parent;

    protected MixinOpenToLanScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 28 - 20 - 8, 150 + 150, 20, new TranslatableText("ngrokLanServer.start"), (button) -> {
            this.client.setScreen(new NgrokScreen(gameMode, allowCommands, parent));
        }));
    }
}
