public class Product {
    String id;
    String name;
    String unit;
    double standardPrice;
    
    Product(String id, String name, String unit, double standardPrice) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.standardPrice = standardPrice;
    }
    
    void printDetails() {
        System.out.println(name + " - $" + standardPrice + " per " + unit);
    }
}
