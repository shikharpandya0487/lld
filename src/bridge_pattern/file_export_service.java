package bridge_pattern;

import java.util.List;

public class file_export_service {

    public static void main(String[] args) {
        GoogleCloudStorage googleStorage = new GoogleCloudStorage();
        S3Storage s3 = new S3Storage();

        CSVExportDataJob csvExportDataJob = new CSVExportDataJob(googleStorage);
        PSVExportDataJob psvExportDataJob = new PSVExportDataJob(s3);
        PDFExportDataJob pdfExportDataJob = new PDFExportDataJob(googleStorage);

        csvExportDataJob.export("employee_data", List.of(new String[]{"Name", "Age", "Department"}, new String[]{"John Doe", "30", "HR"}));
        psvExportDataJob.export("employee_data", List.of(new String[]{"Name", "Age", "Department"}, new String[]{"John Doe", "30", "HR"}));
        pdfExportDataJob.export("employee_data", List.of(new String[]{"Name", "Age", "Department"}, new String[]{"John Doe", "30", "HR"}));

        csvExportDataJob.ship("sherlock homes", "content of sherlock homes");
        
    }

}
