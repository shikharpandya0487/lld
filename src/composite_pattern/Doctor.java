package composite_pattern;

public class Doctor implements HospitalComponent {
    private String name;
    private String specialization;
    

    public Doctor(String name,String specialization) {
        this.name = name;
        this.specialization = specialization;
    }

    public String getName() {
        return name;
    }
    
    public String getSpecialization() {
        return specialization;
    }

    @Override
    public void showdetails() {
        System.out.println("Doctor: " + name + ", Specialization: " + specialization);
    }
}
