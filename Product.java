public class Product {
    private String id;       
    private String name;
    private String unit;
    private double price;
    private Cost productionCosts;
    private static int nextId = 1;  
    
    // Constructor with auto-generated ID
    public Product(String name, String unit, double price) {
        this.id = "PROD-" + nextId;
        nextId++;
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.productionCosts = new Cost(0.0, this);
    }
    
    // GETTERS (all fields private)
    public String getId() {
        return id;
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
    
    // SETTERS with validation (Week 4 requirement)
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
        if (unit != null) {
            this.unit = unit;
        } else {
            System.out.println("Error: Product unit cannot be empty");
        }
    }
    
    public void setProductionCost(double amount) {
        if (amount >= 0) {
            this.productionCosts.setAmount(amount);
        } else {
            System.out.println("Error: Production cost cannot be negative");
        }
    }

    // equals() method that compares based on ID
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
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (unit == null) {
            if (other.unit != null)
                return false;
        } else if (!unit.equals(other.unit))
            return false;
        if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
            return false;
        if (productionCosts == null) {
            if (other.productionCosts != null)
                return false;
        } else if (!productionCosts.equals(other.productionCosts))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "Product [id = " + id + 
                ", name = " + name + 
                ", unit = " + unit + 
                ", price = $" + price + 
                ", productionCosts = $" + getProductionCost() + "]";
    }
}