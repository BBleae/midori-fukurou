package uk.shiz.challenge;

import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import uk.shiz.DialogUtils;
import uk.shiz.TextUtils;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static uk.shiz.DialogUtils.createButton;

public class Dialog {
    public static @NotNull net.minecraft.dialog.type.Dialog getChallengeDialog(
            Text title,
            Challenge ch
    ) throws URISyntaxException {
        var commonData = new DialogCommonData(
                title,
                Optional.empty(),
                false,
                List.of(
                        new PlainMessageDialogBody(
                                ch.challengeText,
                                256
                        )
                )
        );

        var btnList = ch.responses.stream().map(
                chOpts -> createButton(
                        chOpts.name,
                        String.format("/challenge \"%s %s\"", ch.challengeId, chOpts.value),
                        DialogUtils.DialogType.COMMAND
                )
        ).toList();
        net.minecraft.dialog.type.Dialog dialog = new MultiActionDialog(
                commonData,
                btnList,
                Optional.of(new ClickEvent.RunCommand(
                        String.format("/challenge \"%s %s\"", ch.challengeId, "CANCEL")
                )),
                2
        );
        return dialog;
    }
}
