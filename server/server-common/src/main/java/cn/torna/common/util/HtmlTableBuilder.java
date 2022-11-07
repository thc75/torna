package cn.torna.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author thc
 */
public class HtmlTableBuilder {

    private static final String TABLE = "<div class=\"rich-editor\"><figure class=\"table\"><table>%s</table></figure></div>";
    private static final String TR = "<tr>%s</tr>";
    private static final String TH = "<th>%s</th>";
    private static final String TD = "<td>%s</td>";
    public static final String THEAD = "<thead>%s</thead>";
    public static final String TBODY = "<tbody>%s</tbody>";
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
        String headTrs = String.format(TR, String.join("", htmlHeads));
        String thead = String.format(THEAD, headTrs);
        content.append(thead);
        List<List<String>> htmlRows = this.rows.stream()
                .map(cells -> {
                    return cells.stream()
                            .map(v -> String.format(TD, v))
                            .collect(Collectors.toList());
                })
                .collect(Collectors.toList());

        List<String> trs = new ArrayList<>(htmlRows.size());

        for (List<String> row : htmlRows) {
            trs.add(String.format(TR, String.join("", row)));
        }

        String tbody = String.format(TBODY, String.join("", trs));
        content.append(tbody);

        return String.format(TABLE, content);
    }

    @Override
    public String toString() {
        return build();
    }

}
