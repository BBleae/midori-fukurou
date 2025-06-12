package uk.shiz;

import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class DialogUtils {
    public static DialogActionButtonData createButton(Text text, String args, int width, DialogType type) {
        return new DialogActionButtonData(
                new DialogButtonData(text, width),
                Optional.of(switch (type) {
                    case COMMAND -> new SimpleDialogAction(new ClickEvent.RunCommand(args));
                    case LINK -> {
                        try {
                            yield new SimpleDialogAction(new ClickEvent.OpenUrl(new URI(args)));
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
        );
    }

    public static DialogActionButtonData createButton(Text text, String command) {
        return createButton(text, command, 128, DialogType.COMMAND);
    }

    public static DialogActionButtonData createButton(Text text, String command, DialogType type) {
        return createButton(text, command, 128, type);
    }

    public static DialogActionButtonData createLongButton(Text text, String command, DialogType type) {
        return createButton(text, command, 384, type);
    }

    public static DialogActionButtonData createButton(Text text, String command, int width) {
        return createButton(text, command, width, DialogType.COMMAND);
    }

    public static DialogActionButtonData createLabelButton(String text, int width) {
        return new DialogActionButtonData(
                new DialogButtonData(Text.literal(text), width),
                Optional.empty()
        );
    }

    public static DialogActionButtonData createLabelButton(String text) {
        return createLabelButton(text, 128);
    }

    public enum DialogType {
        LINK,
        COMMAND,
    }
}