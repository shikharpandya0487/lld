package facade_pattern;
import java.util.*;

class StudentsStore{
    Map<String, String> studentRecords = new HashMap<>();

    
    public void addStudentRecord(String studentId, String name) {
        studentRecords.put(studentId, name);
    }
    public String getStudentRecord(String studentId) {
        return studentRecords.getOrDefault(studentId, "No student found");
    }
    public void updateStudentRecord(String studentId, String name) {
        studentRecords.put(studentId, name);
    }

    public void removeStudentRecord(String studentId) {
        studentRecords.remove(studentId);
    }
}

class AttendanceService {
    
    Map<String, Integer> attendanceRecords = new HashMap<>();
    public String loadAttendance(String studentId) {
       return attendanceRecords.getOrDefault(studentId, 0).toString();
    }

    public void updateAttendance(String studentId) {
        attendanceRecords.put(studentId, attendanceRecords.getOrDefault(studentId, 0) + 1);
    }

    public void addAttendance(String studentId, int attendance) {
        attendanceRecords.put(studentId, attendance);
    }

    public void removeAttendance(String studentId) {
        attendanceRecords.remove(studentId);
    }
}

class GradesService {
    Map<String, String> gradesRecords = new HashMap<>();

    public String loadGrades(String studentId) {
        return gradesRecords.getOrDefault(studentId, "N/A");
    }

    public void removeGrades(String studentId) {
        gradesRecords.remove(studentId);
    }

    public void updateGrades(String studentId, String grade) {
        gradesRecords.put(studentId, grade);
    }
    
    public void addGrades(String studentId, String grade) {
        gradesRecords.put(studentId, grade);
    }
}



class StudentFacade {

    private final StudentsStore studentsStore;
    private final AttendanceService attendanceService;
    private final GradesService gradesService;    

    public StudentFacade(StudentsStore studentsStore, AttendanceService attendanceService, GradesService gradesService) {
        this.studentsStore = studentsStore;
        this.attendanceService = attendanceService;
        this.gradesService = gradesService;
    }

    public void addStudent(String studentId, String name, String grade) {
        studentsStore.addStudentRecord(studentId, name);
        attendanceService.addAttendance(studentId, 0);
        gradesService.addGrades(studentId, grade);
    }

    public String getStudentRecord(String studentId) {
        String name = Optional.ofNullable(studentsStore.getStudentRecord(studentId)).orElse("Unknown");
        String attendance = attendanceService.loadAttendance(studentId);
        String grade = gradesService.loadGrades(studentId);
        return "Name: " + name + ", Attendance: " + attendance + ", Grade: " + grade;
    }

    public void updateStudentRecord(String studentId, String name, String grade) {
        studentsStore.updateStudentRecord(studentId, name);
        gradesService.updateGrades(studentId, grade);
    }

    public void removeStudentRecord(String studentId) {
        studentsStore.removeStudentRecord(studentId);
        attendanceService.removeAttendance(studentId);
        gradesService.removeGrades(studentId);
    }




}


public class StudentManager{
    public static void main(String[] args) {
        
        StudentsStore studentsStore = new StudentsStore();
        AttendanceService attendanceService = new AttendanceService();
        GradesService gradesService = new GradesService();

        StudentFacade studentFacade = new StudentFacade(studentsStore, attendanceService, gradesService);

        studentFacade.addStudent("1", "Alice", "A");
        studentFacade.addStudent("2", "Bob", "B");

        System.out.println(studentFacade.getStudentRecord("1"));
        System.out.println(studentFacade.getStudentRecord("2"));

        studentFacade.updateStudentRecord("1", "Alice Smith", "A+");
        System.out.println(studentFacade.getStudentRecord("1"));

        studentFacade.removeStudentRecord("2");
        System.out.println(studentFacade.getStudentRecord("2"));
    }
}