package xyz.r2turntrue.lan.ngrok.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.r2turntrue.lan.ngrok.tunnel.NgrokService;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(CallbackInfo ci) {
        if(NgrokService.INSTANCE.getCurrent() != null) {
            NgrokService.INSTANCE.getCurrent().destroyForcibly();
            NgrokService.INSTANCE.setCurrent(null);
        }
    }

}
