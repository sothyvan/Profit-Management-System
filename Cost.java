public class Cost {
    String id;
    String description;
    double amount;
    int typeCode;
    
    Cost(String id, String description, double amount, int typeCode) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.typeCode = typeCode;
    }
    // typeCode is used to categorize costs into different types. 
    // typeCode = 0: Direct costs - cost of making the product.
    // typeCode = 1: Indirect costs - rent, electricity, salaries (liability)
    // Gross Profit = Revenue - Direct Costs (typeCode 0)
    // Net Profit = Revenue - All Costs (typeCode 0 + 1)
    
    void printDetails() {
        System.out.println("Cost: " + description + " - $" + amount);
    }
}
