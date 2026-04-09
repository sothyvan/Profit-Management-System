package other;

public class Product {
    public static final String DEFAULT_UNIT = "case";

    private String id;       
    private final String refCode;
    private String name;
    private String unit;
    private double price;
    private Cost productionCosts;
    private static int nextId = 1;  
    private static int nextRef = 1;
    
    // Constructor with auto-generated ID
    public Product(String name, String unit, double price) {
        this.id = String.valueOf(nextId);
        nextId++;
        this.refCode = formatRef(nextRef);
        nextRef++;
        this.name = name;
        this.unit = DEFAULT_UNIT;
        this.price = price;
        this.productionCosts = new Cost(0.0, this);
    }
    
    // GETTERS
    public String getId() {
        return id;
    }

    public String getRefCode() {
        return refCode;
    }
    
    public String getName() {
        return name;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public double getPrice() {
        return price;
    }
    
    public double getProductionCost() {
        return (productionCosts != null) ? productionCosts.getAmount() : 0;
    }
    
    // SETTERS
    public void setName(String name) {
        if (name != null) {
            this.name = name;
        } else {
            System.out.println("Error: Product name cannot be empty");
        }
    }
    
    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        } else {
            System.out.println("Error: Product price cannot be negative");
        }
    }
    
    public void setUnit(String unit) {
        this.unit = DEFAULT_UNIT;
    }
    
    public void setProductionCost(double amount) {
        if (amount >= 0) {
            this.productionCosts.setAmount(amount);
        } else {
            System.out.println("Error: Production cost cannot be negative");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "Product [ref = " + refCode + ", id = " + id + 
                ", name = " + name + 
                ", unit = " + unit + 
                ", price = $" + price + 
                ", productionCosts = $" + getProductionCost() + "]";
    }

    private String formatRef(int value) {
        return String.format("P-%04d", value);
    }
}
