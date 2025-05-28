package uk.shiz.challenge;

import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.text.ClickEvent;
import org.jetbrains.annotations.NotNull;
import uk.shiz.DialogUtils;
import uk.shiz.TextUtils;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static uk.shiz.DialogUtils.createButton;
import static uk.shiz.DialogUtils.createLongButton;

public class Dialog {
    public static @NotNull net.minecraft.dialog.type.Dialog getChallengeDialog(
            String title,
            Challenge ch
    ) throws URISyntaxException {
        var commonData = new DialogCommonData(
                TextUtils.ParseQuickText(title),
                Optional.empty(),
                false,
                List.of(
                        new PlainMessageDialogBody(
                                ch.challengeText,
                                256
                        )
                )
        );

        AtomicBoolean hasAnyOptionsLengthTooLong = new AtomicBoolean(false);
        var btnList = ch.responses.stream().map(
                chOpts -> {
                    if (chOpts.name.getString().length() >= 16) {
                        hasAnyOptionsLengthTooLong.set(true);
                        return createLongButton(
                                chOpts.name,
                                String.format("/challenge \"%s %s\"", ch.challengeId, chOpts.value),
                                DialogUtils.DialogType.COMMAND
                        );
                    }
                    return createButton(
                            chOpts.name,
                            String.format("/challenge \"%s %s\"", ch.challengeId, chOpts.value),
                            DialogUtils.DialogType.COMMAND
                    );
                }
        ).toList();
        net.minecraft.dialog.type.Dialog dialog = new MultiActionDialog(
                commonData,
                btnList,
                Optional.of(new ClickEvent.RunCommand(
                        String.format("/challenge \"%s %s\"", ch.challengeId, "CANCEL")
                )),
                hasAnyOptionsLengthTooLong.get() ? 1 : 2
        );
        return dialog;
    }
}
