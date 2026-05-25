package composite_pattern;

import java.util.List;

public class Department implements HospitalComponent {
    private String name;
    private List<HospitalComponent> components;

    public Department(String name)
    {
        this.name=name;
        this.components = new java.util.ArrayList<>();
    }

    public void addComponent(HospitalComponent component)
    {
        components.add(component);
    }

    public void removeComponent(HospitalComponent component)
    {
        components.remove(component);
    }

    @Override
    public void showdetails() {
        System.out.println("Department: " + name);
        for (HospitalComponent component : components) {
            component.showdetails();
        }
    }
}