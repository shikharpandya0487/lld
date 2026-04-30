package bridge_pattern;

import java.util.List;

public abstract class ExportDataJob {
    protected storage store;

    public ExportDataJob(storage store) {
        this.store = store;
    }

    abstract void export(String dataSetName,List<String[]>rows);

    protected void ship(String fileName, String content) {
        store.connect();
        store.write(fileName, content);
        store.disconnect();
    }
}

