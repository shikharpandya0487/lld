package srp;
import java.util.*;


public class ShoppingCart
{
    List<Product> products;
    public ShoppingCart(){
        products= new ArrayList<>();
    }

    public double calculateTotal(){
        double total=0;
        for(Product p: products){
            total+=p.getPrice();
        }
        
        return total;
    }
}