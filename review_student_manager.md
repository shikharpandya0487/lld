## Code Review

### Summary
This code already shows the Facade pattern well: `StudentFacade` centralizes student setup and retrieval while `StudentManager` uses only that facade. The design separates storage, attendance, and grades into distinct services, which is a strong foundation.

---

### SOLID Principle Check

| Principle | Status | Finding |
|---|---|---|
| S — Single Responsibility | ✓ | Each service class has a focused role: student storage, attendance, or grades. |
| O — Open/Closed | ⚠ | The facade is central, but adding another subsystem would require changing `StudentFacade`. |
| L — Liskov Substitution | ✓ | There is no inheritance hierarchy here, so this principle is respected. |
| I — Interface Segregation | ✓ | There are no fat interfaces; each class exposes a small, relevant API. |
| D — Dependency Inversion | ✓ | `StudentFacade` receives its dependencies via constructor injection rather than creating them internally. |

---

### Issues Found

#### Issue 1 — API mismatch in `AttendanceService` — Moderate
**What's wrong:** `addAttendance(String studentId, String attendance)` receives an attendance value but ignores it and always stores `1`.

**Problematic code:**
```java
public void addAttendance(String studentId, String attendance) {
    attendanceRecords.put(studentId, 1);
}
```

**Suggested fix:**
```java
public void addAttendance(String studentId, int attendance) {
    attendanceRecords.put(studentId, attendance);
}
```

**Why this fix works:** It makes the method signature match the behavior and avoids confusion when callers expect the passed value to matter.

#### Issue 2 — `getStudentRecord` can return null values — Moderate
**What's wrong:** `StudentFacade.getStudentRecord` concatenates values from services that may return `null` when a student is missing.

**Problematic code:**
```java
String name = studentsStore.getStudentRecord(studentId);
String attendance = attendanceService.loadAttendance(studentId);
String grade = gradesService.loadGrades(studentId);
return "Name: " + name + ", Attendance: " + attendance + ", Grade: " + grade;
```

**Suggested fix:**
```java
String name = Optional.ofNullable(studentsStore.getStudentRecord(studentId)).orElse("Unknown");
String attendance = attendanceService.loadAttendance(studentId);
String grade = Optional.ofNullable(gradesService.loadGrades(studentId)).orElse("N/A");
return "Name: " + name + ", Attendance: " + attendance + ", Grade: " + grade;
```

**Why this fix works:** It prevents the output from showing `null` and makes the response more user-friendly when a record is missing.

#### Issue 3 — `StudentsStore.getStudentRecord` returns null for missing students — Minor
**What's wrong:** `getStudentRecord` uses `Map.get` directly, so missing student IDs produce `null` rather than a safer default.

**Problematic code:**
```java
public String getStudentRecord(String studentId) {
    return studentRecords.get(studentId);
}
```

**Suggested fix:**
```java
public String getStudentRecord(String studentId) {
    return studentRecords.getOrDefault(studentId, "No student found");
}
```

**Why this fix works:** It keeps the service responsible for a consistent result instead of forcing every caller to handle `null`.

#### Issue 4 — `addAttendance` parameter type should reflect stored data — Minor
**What's wrong:** Attendance is stored as `Integer`, but `addAttendance` takes a `String` input.

**Problematic code:**
```java
public void addAttendance(String studentId, String attendance) {
    attendanceRecords.put(studentId, 1);
}
```

**Suggested fix:**
```java
public void addAttendance(String studentId, int attendance) {
    attendanceRecords.put(studentId, attendance);
}
```

**Why this fix works:** It makes the service API type-safe and clearer to future readers.

---

### Design Pattern Opportunities

#### Suggested Pattern: Data Transfer Object (DTO)
**Why:** Packing student name, attendance, and grade into a single `StudentRecord` object would make `StudentFacade` easier to extend and avoid string formatting logic in the facade.

**Where to apply:** `StudentFacade.getStudentRecord` return type and the service coordination layer.

**Quick sketch:**
```java
public class StudentRecord {
    private final String name;
    private final int attendance;
    private final String grade;

    public StudentRecord(String name, int attendance, String grade) {
        this.name = name;
        this.attendance = attendance;
        this.grade = grade;
    }
}
```

No additional structural pattern is needed beyond Facade; the current architecture already follows the intended design.

---

### What You Did Well
- `StudentFacade` correctly hides the interactions between `StudentsStore`, `AttendanceService`, and `GradesService`.
- You used constructor injection in `StudentFacade`, which is a good Dependency Inversion practice.
- `StudentManager.main()` uses only the facade, keeping the client code clean.
- The service classes each hold a single responsibility, which is a strong design choice.

### Priority Fix List
1. Fix `AttendanceService.addAttendance` so its parameter type and stored value match.
2. Guard against `null` return values in `StudentFacade.getStudentRecord` and service methods.
3. Make `StudentsStore.getStudentRecord` return a safe default instead of `null`.
4. Consider returning a `StudentRecord` object rather than a formatted string from the facade.

Want me to show the fully refactored version? Or would you prefer to try the fixes yourself first and call `/review-my-code` again?