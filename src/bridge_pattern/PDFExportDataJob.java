package bridge_pattern;

import java.util.List;

public class PDFExportDataJob extends ExportDataJob {

    public PDFExportDataJob(storage store) {
        super(store);
    }

    @Override
    void export(String dataSetName, List<String[]> rows) {
        StringBuilder content = new StringBuilder();
        content.append("PDF Export for dataset: ").append(dataSetName).append("\n");
        for (String[] row : rows) {
            content.append(String.join(", ", row)).append("\n");
        }
        ship(dataSetName + ".pdf", content.toString());
    }

}
