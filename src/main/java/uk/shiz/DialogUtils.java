package uk.shiz;

import net.minecraft.class_11519; // DialogButton
import net.minecraft.class_11525; // Action
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
public class DialogUtils {
    public static class_11519 createButton(Text text, String args, int width, DialogType type) {
        return new class_11519(
                new DialogButtonData(text, width),
                Optional.of(switch (type) {
                    case COMMAND -> new class_11525(new ClickEvent.RunCommand(args));
                    case LINK -> {
                        try {
                            yield new class_11525(new ClickEvent.OpenUrl(new URI(args)));
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
        );
    }

    public static class_11519 createButton(Text text, String command) {
        return createButton(text, command, 128, DialogType.COMMAND);
    }

    public static class_11519 createButton(Text text, String command, DialogType type) {
        return createButton(text, command, 128, type);
    }

    public static class_11519 createLongButton(Text text, String command, DialogType type) {
        return createButton(text, command, 384, type);
    }

    public static class_11519 createButton(Text text, String command, int width) {
        return createButton(text, command, width, DialogType.COMMAND);
    }

    public static class_11519 createLabelButton(String text, int width) {
        return new class_11519(
                new DialogButtonData(Text.literal(text), width),
                Optional.empty()
        );
    }

    public static class_11519 createLabelButton(String text) {
        return createLabelButton(text, 128);
    }

    public enum DialogType {
        LINK,
        COMMAND,
    }
}