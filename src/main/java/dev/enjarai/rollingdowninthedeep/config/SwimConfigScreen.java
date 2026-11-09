package dev.enjarai.rollingdowninthedeep.config;

import com.mojang.blaze3d.Blaze3D;
import dev.enjarai.rollingdowninthedeep.compat.yacl.YACLImplementation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import nl.enjarai.doabarrelroll.compat.Compat;

import java.net.URI;

public class SwimConfigScreen {
    public static Screen create(Screen parent) {
        if (!Compat.isYACLLoaded()) {
            return new ConfirmScreen((result) -> {
                if (result) {
                    Blaze3D.openUri(URI.create("https://modrinth.com/mod/yacl/versions"));
                }
                Minecraft.getInstance().gui.setScreen(parent);
            }, getText("missing"), getText("missing.message"), CommonComponents.GUI_YES, CommonComponents.GUI_NO);
        } else if (!Compat.isYACLUpToDate()) {
            return new ConfirmScreen((result) -> {
                if (result) {
                    Blaze3D.openUri(URI.create("https://modrinth.com/mod/yacl/versions"));
                }
                Minecraft.getInstance().gui.setScreen(parent);
            }, getText("outdated"), getText("outdated.message"), CommonComponents.GUI_YES, CommonComponents.GUI_NO);
        } else {
            return YACLImplementation.generateConfigScreen(parent);
        }
    }

    private static Component getText(String key) {
        return Component.translatable("config.do_a_barrel_roll.yacl." + key);
    }
}
