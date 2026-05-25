package composite_pattern;

public class HospitalManager {
    public static void main(String[] args) {
        System.out.println("Welcome to the hospital!");

        Department cardiology = new Department("Cardiology");
        cardiology.addComponent(new Doctor("Dr. Smith", "Cardiologist"));
        Department neurology = new Department("Neurology");
        neurology.addComponent(new Doctor("Dr. Johnson", "Neurologist"));
        Department hospital = new Department("City Hospital");
        hospital.addComponent(cardiology);
        hospital.addComponent(neurology);

        hospital.showdetails();

    }
}
