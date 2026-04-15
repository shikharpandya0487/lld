package ocp;

public class SQLDb implements DBPersistent {
    public void save(Product product) {
        System.out.println("Saving product to SQL Database: " + product.getName());
    }
}
