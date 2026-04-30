# Practice Problem: File Export Service (Bridge Pattern)

## Background

You are building a data export service for an analytics platform. The service must export datasets
in different **formats** — CSV, JSON, and Parquet — and deliver them to different **storage backends**
— S3, SFTP, and Google Cloud Storage (GCS).

Both dimensions grow independently: the data team adds new formats as requirements evolve, and the
infra team adds new backends as the company expands to new regions. Neither team should need to touch
the other's code when extending their own side.

---

## Requirements

### 1. StorageBackend (Implementor Interface)

Define a `StorageBackend` interface that all storage targets implement.

| Method                                          | Behaviour                                             |
|-------------------------------------------------|-------------------------------------------------------|
| `void connect()`                                | Establishes the connection / session to the backend   |
| `void write(String fileName, String content)`   | Writes serialized content as a named file             |
| `void disconnect()`                             | Closes the connection / session                       |
| `String getBackendName()`                       | Returns a short label for logging (e.g., `"S3"`)      |

Implement three concrete backends: `S3Backend`, `SftpBackend`, `GcsBackend`.
Each `connect()` / `disconnect()` prints a connection message; `write()` prints the backend name,
file name, and the first 60 characters of content followed by `...` if longer.

---

### 2. ExportJob (Abstraction)

Define an abstract class `ExportJob` that holds a `StorageBackend` reference — **this is the bridge**.

| Member                                                          | Description                                               |
|-----------------------------------------------------------------|-----------------------------------------------------------|
| `protected StorageBackend storage`                              | Injected via constructor — never instantiated internally  |
| `abstract void export(String dataSetName, List<String[]> rows)` | Serializes rows into the format, then ships via `storage` |
| `protected void ship(String fileName, String content)`          | Template helper — calls connect, write, disconnect        |

Implement three refined abstractions:

| Class             | File extension | Serialization rule                                                       |
|-------------------|----------------|--------------------------------------------------------------------------|
| `CsvExportJob`    | `.csv`         | First row is a header; remaining rows are comma-joined lines             |
| `JsonExportJob`   | `.json`        | Output a JSON array of objects; first row provides the field names       |
| `ParquetExportJob`| `.parquet`     | Simulate columnar output: one block per column with all its values listed|

---

### 3. Combination Rules

- Any `ExportJob` subclass must work with any `StorageBackend` without modification.
- Adding a new format (e.g., `XmlExportJob`) must require **zero changes** to any backend class.
- Adding a new backend (e.g., `AzureBlobBackend`) must require **zero changes** to any export class.
- The `ExportJob` constructor must accept a `StorageBackend` — not create one internally.

---

## Example Usage

```java
List<String[]> salesData = List.of(
    new String[]{"region", "product", "revenue"},   // header row
    new String[]{"North",  "Widget A", "12000"},
    new String[]{"South",  "Widget B", "8500"},
    new String[]{"East",   "Widget C", "21000"}
);

StorageBackend s3   = new S3Backend("us-east-1");
StorageBackend sftp = new SftpBackend("files.internal.corp", 22);
StorageBackend gcs  = new GcsBackend("my-analytics-bucket");

ExportJob csvToS3      = new CsvExportJob(s3);
ExportJob jsonToSftp   = new JsonExportJob(sftp);
ExportJob parquetToGcs = new ParquetExportJob(gcs);

csvToS3.export("q1_sales", salesData);
// [S3] Connecting to bucket: us-east-1
// [S3] Writing file: q1_sales.csv
// [S3] Content: region,product,revenue\nNorth,Widget A,12000\nSouth,Widget B...
// [S3] Disconnecting.

jsonToSftp.export("q1_sales", salesData);
// [SFTP] Connecting to files.internal.corp:22
// [SFTP] Writing file: q1_sales.json
// [SFTP] Content: [{"region":"North","product":"Widget A","revenue":"12000"},...
// [SFTP] Disconnecting.

parquetToGcs.export("q1_sales", salesData);
// [GCS] Connecting to bucket: my-analytics-bucket
// [GCS] Writing file: q1_sales.parquet
// [GCS] Content: COL[region]: North | South | East\nCOL[product]: Widget A |...
// [GCS] Disconnecting.

// Swap backend at runtime — no change to CsvExportJob
ExportJob csvToGcs = new CsvExportJob(gcs);
csvToGcs.export("q1_sales", salesData);
// [GCS] Connecting to bucket: my-analytics-bucket
// ...
```

---

## Questions to Answer

1. Count the classes in your solution. Confirm it is `M + N + 1` (abstractions + implementors +
   the abstract base), not `M × N`. What would the count be if you had used inheritance instead?

2. The `ship()` helper in `ExportJob` calls `connect → write → disconnect` in sequence. Why is this
   defined on the abstraction rather than on each concrete implementor?

3. A new requirement arrives: `SecureExportJob` must encrypt the serialized content before shipping,
   regardless of the format. Where in the hierarchy does this belong and why?

4. Trace the call chain for `csvToS3.export("q1_sales", salesData)` — identify exactly which method
   belongs to the abstraction layer and which belongs to the implementor layer.

---

## Hints

> **Hint 1 — The bridge field:** `StorageBackend storage` is just a field on `ExportJob`. The
> "bridge" is nothing exotic — it is plain composition. The power comes from the fact that neither
> hierarchy references the other's concrete types.

> **Hint 2 — ship() helper:** Implement `ship()` once on `ExportJob` as a `protected` non-abstract
> method. Each refined abstraction calls `ship(fileName, serializedContent)` at the end of
> `export()`. This avoids repeating the connect/write/disconnect sequence in every subclass.

> **Hint 3 — JSON serialization:** Build the JSON manually using `StringBuilder` — no external
> library needed. Use the first row of `rows` as the key names and each subsequent row as values.

> **Hint 4 — M + N vs M × N:** With 3 formats and 3 backends you should have exactly
> 3 + 3 = **6 concrete classes** plus 1 abstract class and 1 interface — **8 total**.
> Without Bridge you would need 3 × 3 = **9 concrete classes** and they would all be leaf nodes
> with no reuse.

---

## Bonus

- Add a `BufferedExportJob` refined abstraction that wraps any other `ExportJob` and batches
  multiple `export()` calls, flushing them all in one `ship()` call when `flush()` is invoked.
  (Hint: this is composition on the abstraction side — `BufferedExportJob` holds an `ExportJob`.)
- Add a `MultiBackendExportJob` that fans out a single `export()` call to a `List<StorageBackend>`,
  writing the same file to every backend. Where does this class sit in the hierarchy?
