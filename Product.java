public class Product {
    String id;
    String name;
    String unit;
    double price;
    
    Product(String id, String name, String unit, double price) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.price = price;
    }
    
    void printDetails() {
        System.out.println(name + " - $" + price + " per " + unit);
    }
}
