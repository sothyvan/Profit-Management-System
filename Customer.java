public class Customer {
    private String firstName;
    private String lastName;
    private String name;
    private String phoneNumber;
    private static int nextId = 1;  
    private String id;              
    
    public Customer(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        name = firstName + " " + lastName;
        this.phoneNumber = phoneNumber;
        this.id = "CUST-" + nextId;  
        nextId++;
    }

    //Getter methods
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getId() {
        return id;
    }

    //Setter methods
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @Override
    public String toString() {
        return "Customer [id = " + id + ", firstName = " + firstName + 
                ", lastName = " + lastName + ", phoneNumber = " + phoneNumber + "]";
    }
}