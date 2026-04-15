package srp;

public class ChatInvoiceProduct 
{
    public String getInvoice(String[] args) {
        ShoppingCart cart= new ShoppingCart();
        return "Invoice for cart with total: " + cart.calculateTotal();
    }
}