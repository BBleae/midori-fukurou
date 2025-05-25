package uk.shiz.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.dialog.*;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogListDialog;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.dialog.type.ServerLinksDialog;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DialogTags;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {

    @Inject(method = "onPlayerConnect", at = @At(value = "RETURN"))
    public void afterPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) throws URISyntaxException {
        var commonData = new DialogCommonData(
                Text.literal("Welcome to NekoCraft!"),
                Optional.of(Text.literal("Click the button below to start your adventure!")),
                false,
                List.of(
                        new PlainMessageDialogBody(
                                Text.literal("This is a custom dialog for NekoCraft. Enjoy your stay!"),
                                256
                        )
                )
        );
        Dialog dialog = getDialog(commonData);
        player.openDialog(RegistryEntry.of(dialog));
        player.sendMessage(
                Text.literal(String.format("Welcome to NekoCraft, %s! Enjoy your stay!", player.getName().getString()))
                        .formatted(net.minecraft.util.Formatting.GREEN)
        );
    }

    private static @NotNull Dialog getDialog(DialogCommonData commonData) throws URISyntaxException {
        var btns = List.of(
                new DialogButton(
                        new DialogButtonData(Text.literal("ovo"), 128),
                        Optional.of(new ClickEvent.RunCommand("/challenge \"Hello, NekoCraft!\""))
                ),
                new DialogButton(
                        new DialogButtonData(Text.literal("1919"), 128),
                        Optional.of(new ClickEvent.OpenUrl(new URI("https://github.com")))
                ),
                new DialogButton(
                        new DialogButtonData(Text.literal("1919"), 128),
                        Optional.of(new ClickEvent.OpenUrl(new URI("https://github.com")))
                ),
                new DialogButton(
                        new DialogButtonData(Text.literal("1919"), 128),
                        Optional.of(new ClickEvent.OpenUrl(new URI("https://github.com")))
                )
        );
        Dialog dialog = new MultiActionDialog(
                commonData,
                btns,
                Optional.empty(),
                2
        );
        return dialog;
    }
}

