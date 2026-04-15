package srp;

public class CartDbStorage{
    ShoppingCart cart;
    public CartDbStorage(ShoppingCart cart){
        this.cart=cart;
    }

    public void saveCart(){
        // code to save cart to database
        System.out.println("Cart saved to database with total: "+cart.calculateTotal());
    }
}