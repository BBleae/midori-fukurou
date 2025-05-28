package uk.shiz;

import net.minecraft.dialog.DialogButton;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import uk.shiz.challenge.Dialog;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class DialogUtils {
    public static DialogButton createButton(Text text, String args, int width, DialogType type) {
        return new DialogButton(
                new DialogButtonData(text, width),
                Optional.of(switch (type) {
                    case COMMAND -> new ClickEvent.RunCommand(args);
                    case LINK -> {
                        try {
                            yield new ClickEvent.OpenUrl(new URI(args));
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
        );
    }

    public static DialogButton createButton(Text text, String command) {
        return createButton(text, command, 128, DialogType.COMMAND);
    }

    public static DialogButton createButton(Text text, String command, DialogType type) {
        return createButton(text, command, 128, type);
    }

    public static DialogButton createLongButton(Text text, String command, DialogType type) {
        return createButton(text, command, 384, type);
    }

    public static DialogButton createButton(Text text, String command, int width) {
        return createButton(text, command, width, DialogType.COMMAND);
    }

    public static DialogButton createLabelButton(String text, int width) {
        return new DialogButton(
                new DialogButtonData(Text.literal(text), width),
                Optional.empty()
        );
    }

    public static DialogButton createLabelButton(String text) {
        return createLabelButton(text, 128);
    }

    public enum DialogType {
        LINK,
        COMMAND,
    }
}