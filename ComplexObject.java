import java.util.Scanner;

public class ComplexObject {
    private SubObject1 subObject1;
    private SubObject2 subObject2;

    public ComplexObject() {
        subObject1 = new SubObject1();
        subObject2 = new SubObject2();
    }

    public void configure() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Configuring ComplexObject...");

        System.out.print("Enter an integer value for SubObject1: ");
        int intValue1 = scanner.nextInt();
        subObject1.setIntValue(intValue1);

        System.out.print("Enter a double value for SubObject2: ");
        double doubleValue2 = scanner.nextDouble();
        subObject2.setDoubleValue(doubleValue2);

        // Close the scanner
        scanner.close();
    }

    public void displayInfo() {
        System.out.println("ComplexObject Information:");
        System.out.println("SubObject1 Information:");
        // Display SubObject1 information
        System.out.println("SubObject2 Information:");
        // Display SubObject2 information
    }

    public static void main(String[] args) {
        ComplexObject complexObject = new ComplexObject();
        complexObject.configure();
        complexObject.displayInfo();
    }
}
class SubObject1 {
    private int intValue;

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
    
    // Add other methods and instance variables as needed.
}

class SubObject2 {
    private double doubleValue;

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    // Add other methods and instance variables as needed.
}