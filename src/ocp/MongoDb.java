package ocp;

public class MongoDb implements DBPersistent {
    Cart cart;
    public void save(Product product) {
        System.out.println("Saving product to MongoDB: " + product.getName());
    }
}
