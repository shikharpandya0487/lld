
import bridge_pattern.ExportDataJob;

public abstract class BufferedExportJob {
    private ExportDataJob exportDataJob;

    public BufferedExportJob(ExportDataJob exportDataJob) {
        this.exportDataJob = exportDataJob;
    }
    // export in batches 
    public void export() {
        System.out.println("Buffering data...");
        exportDataJob.export();
        System.out.println("Exporting data...");
    }

    // Flush - multiple export calls can be shipped
    
}
