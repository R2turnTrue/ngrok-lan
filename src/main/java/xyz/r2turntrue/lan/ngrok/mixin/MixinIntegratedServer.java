package xyz.r2turntrue.lan.ngrok.mixin;

import net.minecraft.server.LanServerPinger;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.r2turntrue.lan.ngrok.tunnel.NgrokService;

@Mixin(IntegratedServer.class)
public class MixinIntegratedServer {

    @Inject(method = "shutdown", at = @At("HEAD"))
    public void shutdown(CallbackInfo ci) {
        if(NgrokService.INSTANCE.getCurrent() != null) {
            NgrokService.INSTANCE.getCurrent().destroyForcibly();
            NgrokService.INSTANCE.setCurrent(null);
        }
    }

}
