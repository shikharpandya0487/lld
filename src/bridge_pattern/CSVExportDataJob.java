package bridge_pattern;

import java.util.List;

public class CSVExportDataJob extends ExportDataJob {

    public CSVExportDataJob(storage store) {
        super(store);
    }

    @Override
    void export(String dataSetName, List<String[]> rows) {
        StringBuilder csvContent = new StringBuilder();
        for (String[] row : rows) {
            csvContent.append(String.join(",", row)).append("\n");
        }
        ship(dataSetName + ".csv", csvContent.toString());
    }

}
