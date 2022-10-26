package cn.torna.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author thc
 */
public class HtmlTableBuilder {

    private static final String TABLE = "<div class=\"el-table el-table--fit el-table--border el-table--enable-row-hover\"><table cellspacing=0 cellpadding=0 border=0 class=\"el-table__body\">%s</table></div>";
    private static final String TR = "<tr class=\"el-table__row\">%s</tr>";
    private static final String TH = "<th style=\"padding: 2px 4px;\"><div class=\"cell\">%s</div></th>";
    private static final String TD = "<td style=\"padding: 2px 4px;\"><div class=\"cell\">%s</div></td>";
    private String[] heads;
    private List<List<String>> rows;

    public HtmlTableBuilder heads(String... heads) {
        this.heads = heads;
        return this;
    }

    public HtmlTableBuilder addRow(List<String> row) {
        if (this.rows == null) {
            this.rows = new ArrayList<>(16);
        }
        this.rows.add(row);
        return this;
    }

    public String build() {
        StringBuilder content = new StringBuilder();
        List<String> htmlHeads = Arrays.stream(heads)
                .map(v -> String.format(TH, v))
                .collect(Collectors.toList());

        content.append(String.format(TR, String.join("", htmlHeads)));
        List<List<String>> htmlRows = this.rows.stream()
                .map(cells -> {
                    return cells.stream()
                            .map(v -> String.format(TD, v))
                            .collect(Collectors.toList());
                })
                .collect(Collectors.toList());
        for (List<String> row : htmlRows) {
            content.append(String.format(TR, String.join("", row)));
        }

        return String.format(TABLE, content);
    }

    @Override
    public String toString() {
        return build();
    }

}
