package dev.enjarai.rollingdowninthedeep;

import com.mojang.blaze3d.platform.InputConstants;
import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import dev.enjarai.rollingdowninthedeep.config.SwimConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class SwimKeybindings {
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(RollingDownInTheDeep.id("main"));

    public static final KeyMapping TOGGLE_ENABLED = new KeyMapping(
        "key.rolling_down_in_the_deep.toggle_enabled",
        GLFW.GLFW_KEY_O,
        CATEGORY
    );
    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
        "key.rolling_down_in_the_deep.open_config",
        InputConstants.UNKNOWN.getValue(),
        CATEGORY
    );

    public static void clientTick(Minecraft client) {
        while (TOGGLE_ENABLED.consumeClick()) {
            SwimConfig.INSTANCE.enabled = !SwimConfig.INSTANCE.enabled;
            SwimConfig.INSTANCE.save();

            if (client.player != null) {
                client.player.sendOverlayMessage(
                    Component.translatable(
                        "key.rolling_down_in_the_deep." +
                            (SwimConfig.INSTANCE.enabled ? "toggle_enabled.enable" : "toggle_enabled.disable")
                    )
                );
            }
        }

        while (OPEN_CONFIG.consumeClick()) {
            client.gui.setScreen(SwimConfigScreen.create(client.gui.screen()));
        }
    }
}
