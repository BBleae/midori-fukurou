package uk.shiz;

import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.text.Text;

public class TextUtils {
    private static NodeParser parser = NodeParser.merge(TagParser.DEFAULT, Placeholders.DEFAULT_PLACEHOLDER_PARSER);
    public static Text ParseQuickText(String textFormat) {
        return parser.parseNode(textFormat).toText();
    }
}
