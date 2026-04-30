package bridge_pattern;

import java.util.List;

public class PSVExportDataJob extends ExportDataJob {

    public PSVExportDataJob(storage store) {
        super(store);
    }

    @Override
    void export(String dataSetName, List<String[]> rows) {
        StringBuilder psvContent = new StringBuilder();
        for (String[] row : rows) {
            psvContent.append(String.join("|", row)).append("\n");
        }
        ship(dataSetName + ".psv", psvContent.toString());
    }

}
