package bridge_pattern;

import java.util.List;

public class ParquestExportDataJob extends ExportDataJob {

    public ParquestExportDataJob(storage store) {
        super(store);
    }

    @Override
    void export(String dataSetName, List<String[]> rows) {
        StringBuilder parquetContent = new StringBuilder();
        for (String[] row : rows) {
            parquetContent.append(String.join("|", row)).append("\n");
        }
        ship(dataSetName + ".parquet", parquetContent.toString());
    }

}
