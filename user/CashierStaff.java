package user;
public class CashierStaff extends Staff {
    private double salary;

    public CashierStaff(Staff staff, double salary) {
        super(staff, "CASHIER");
        setSalary(salary);
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public boolean can(String action) {
        return action.equals("CREATE_TRANSACTION")
                || action.equals("VIEW_TRANSACTIONS")
                || action.equals("VIEW_PRODUCTS")
                || action.equals("VIEW_CUSTOMERS");
    }

        // CashierStaff.java
    @Override
    public String getPosition() {
        return "Cashier";
    }


    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString()+"CashierStaff [\"Position: Cashier salary=" + salary + "]";
    }

    
}
