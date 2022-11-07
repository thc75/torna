package cn.torna.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author thc
 */
public class MarkdownTableBuilder {

    public static final String TABLE_SPLIT_CHAR = "|";
    private String[] heads;
    private List<List<String>> rows;

    private Align align = Align.LEFT;

    public MarkdownTableBuilder heads(String... heads) {
        this.heads = heads;
        return this;
    }

    public MarkdownTableBuilder align(Align align) {
        this.align = align;
        return this;
    }

    public MarkdownTableBuilder addRow(List<String> row) {
        if (this.rows == null) {
            this.rows = new ArrayList<>(16);
        }
        this.rows.add(row);
        return this;
    }

    public String build() {
        StringBuilder content = new StringBuilder();
        List<String> headSplit = Arrays.stream(heads)
                .map(v -> align.getContent())
                .collect(Collectors.toList());

        content.append(TABLE_SPLIT_CHAR).append(String.join(TABLE_SPLIT_CHAR, heads)).append(TABLE_SPLIT_CHAR).append("\n");
        content.append(TABLE_SPLIT_CHAR).append(String.join(TABLE_SPLIT_CHAR, headSplit)).append(TABLE_SPLIT_CHAR).append("\n");
        for (List<String> row : this.rows) {
            content.append(TABLE_SPLIT_CHAR).append(String.join(TABLE_SPLIT_CHAR, row)).append(TABLE_SPLIT_CHAR).append("\n");
        }

        return content.toString();
    }

    @Override
    public String toString() {
        return build();
    }

    @AllArgsConstructor
    @Getter
    public enum Align {

        LEFT(":---"),
        RIGHT("---:"),
        CENTER(":---:"),
        ;
        private final String content;
    }
}
