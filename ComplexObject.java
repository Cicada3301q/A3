import java.util.Scanner;

public class ComplexObject {
    public SubObject1 subObject1;
    public SubObject2 subObject2;

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
        subObject1.setCircularReference(this); // Set the circular reference.

        System.out.print("Enter a double value for SubObject2: ");
        double doubleValue2 = scanner.nextDouble();
        subObject2.setDoubleValue(doubleValue2);
        subObject2.setCircularReference(this); // Set the circular reference.

        // Close the scanner
        scanner.close();
    }

    public void displayInfo() {
        System.out.println("ComplexObject Information:");
        System.out.println("SubObject1 Information:");
        System.out.println("Int Value: " + subObject1.getIntValue());
        ComplexObject reference1 = subObject1.getCircularReference();
        if (reference1 != null) {
            System.out.println("Circular Reference in SubObject1: " + reference1);
        } else {
            System.out.println("No Circular Reference in SubObject1");
        }

        System.out.println("SubObject2 Information:");
        System.out.println("Double Value: " + subObject2.getDoubleValue());
        ComplexObject reference2 = subObject2.getCircularReference();
        if (reference2 != null) {
            System.out.println("Circular Reference in SubObject2: " + reference2);
        } else {
            System.out.println("No Circular Reference in SubObject2");
        }
    }

    public static void main(String[] args) {
        ComplexObject complexObject = new ComplexObject();
        complexObject.configure();
        complexObject.displayInfo();
        Serializer serializer = new Serializer();
        serializer.serialize(complexObject);

    }
}

class SubObject1 {
    public int intValue;
    public ComplexObject circularReference;

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public int getIntValue() {
        return this.intValue;
    }

    public void setCircularReference(ComplexObject circularReference) {
        this.circularReference = circularReference;
    }

    public ComplexObject getCircularReference() {
        return this.circularReference;
    }

}

class SubObject2 {
    public double doubleValue;
    public ComplexObject circularReference;

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public double getDoubleValue() {
        return this.doubleValue;
    }

    public void setCircularReference(ComplexObject circularReference) {
        this.circularReference = circularReference;
    }

    public ComplexObject getCircularReference() {
        return this.circularReference;
    }

}