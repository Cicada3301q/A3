import java.util.Scanner;

public class SimpleObject {
    private int intValue;
    private double doubleValue;
    private String stringValue;

    public SimpleObject() {
        // Default constructor
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public void displayInfo() {
        System.out.println("SimpleObject Information:");
        System.out.println("Int Value: " + intValue);
        System.out.println("Double Value: " + doubleValue);
        System.out.println("String Value: " + stringValue);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Creating a SimpleObject...");
        SimpleObject simpleObject = new SimpleObject();

        // Set values for the instance variables
        System.out.print("Enter an integer value: ");
        int intValue = scanner.nextInt();
        simpleObject.setIntValue(intValue);

        System.out.print("Enter a double value: ");
        double doubleValue = scanner.nextDouble();
        simpleObject.setDoubleValue(doubleValue);

        scanner.nextLine(); // Consume newline
        System.out.print("Enter a string value: ");
        String stringValue = scanner.nextLine();
        simpleObject.setStringValue(stringValue);

        // Display the object's information
        simpleObject.displayInfo();

        // Close the scanner
        scanner.close();
    }
}