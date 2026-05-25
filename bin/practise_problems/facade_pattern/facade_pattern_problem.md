## Practice Problem — Facade

### Scenario
Your school management system needs a simple way to open student records for teachers. The system currently has separate services for attendance, grades, and student profile details. Teachers should be able to call one method and let the system gather everything.

### Your Task
1. Create three subsystem classes: `AttendanceService`, `GradesService`, and `ProfileService`.
2. Create a `StudentRecordFacade` class that provides a single method `openStudentRecord(String studentId)`.
3. `openStudentRecord` must call the three subsystem services in the correct order and combine their actions into one operation.
4. Write a `main()` method in a class named `SchoolApp` that demonstrates using `StudentRecordFacade` to open a student record.
5. Do not let the caller directly use the subsystem classes; only the facade should coordinate them.

### Starter Code
```java
public class AttendanceService {
    public void loadAttendance(String studentId) {
        // TODO: implement attendance loading
    }
}

public class GradesService {
    public void loadGrades(String studentId) {
        // TODO: implement grade loading
    }
}

public class ProfileService {
    public void loadProfile(String studentId) {
        // TODO: implement profile loading
    }
}

public class StudentRecordFacade {
    public StudentRecordFacade(AttendanceService attendanceService,
                               GradesService gradesService,
                               ProfileService profileService) {
        // TODO: implement constructor
    }

    public void openStudentRecord(String studentId) {
        // TODO: implement facade orchestration
    }
}

public class SchoolApp {
    public static void main(String[] args) {
        AttendanceService attendanceService = new AttendanceService();
        GradesService gradesService = new GradesService();
        ProfileService profileService = new ProfileService();

        StudentRecordFacade facade = new StudentRecordFacade(
            attendanceService,
            gradesService,
            profileService
        );

        facade.openStudentRecord("S12345");
    }
}
```

### Constraints
- Use Java 11+ syntax
- No external libraries
- Must include a `main()` method that demonstrates the feature
- The facade must be the only class that coordinates the subsystem calls

### Hint (read only if stuck)
> Build one wrapper class that calls the three services for you, so the `SchoolApp` only needs `openStudentRecord("S12345")`.

Take your time. Paste your solution when you're ready and I'll review it.
