import java.util.Scanner;

public class ObjectCreator {
    public static void main(String[] args) {
        // Create a Scanner to read user input
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose a class to create an instance (1-5):");
        System.out.println("1. SimpleObject");
        System.out.println("2. ComplexObject");
        System.out.println("3. ArrayObject");
        System.out.println("4. ArrayOfObjects");
        System.out.println("5. ObjectCollection");

        int choice = scanner.nextInt();

        // Create an object based on the user's choice
        Object obj = createObject(choice);

        if (obj != null) {
            System.out.println("Object created: " + obj.toString());
        } else {
            System.out.println("Invalid choice.");
        }

        // Close the scanner
        scanner.close();
    }

    public static Object createObject(int choice) {
        switch (choice) {
            case 1:
                return new SimpleObject(); // Instantiate a SimpleObject
            case 2:
                return new ComplexObject(); // Instantiate a ComplexObject
            case 3:
                return createArrayObject(); // Instantiate an ArrayObject
            case 4:
                return createArrayOfObjects(); // Instantiate an ArrayOfObjects
            case 5:
                return new ObjectCollection(); // Instantiate an ObjectCollection
            default:
                return null; // Invalid choice
        }
    }

    private static ArrayObject createArrayObject() {
        Scanner scanner = new Scanner(System.in);
    
        System.out.print("Enter the array size: ");
        int arraySize = scanner.nextInt();
        ArrayObject arrayObject = new ArrayObject(arraySize);
    
        for (int i = 0; i < arraySize; i++) {
            System.out.print("Enter value for element " + i + ": ");
            int value = scanner.nextInt();
            arrayObject.setIntArrayValue(i, value);
        }
    
        scanner.close();
        return arrayObject;
    }

    private static ArrayOfObjects createArrayOfObjects() {
        Scanner scanner = new Scanner(System.in);
    
        System.out.print("Enter the array size: ");
        int arraySize = scanner.nextInt();
        ArrayOfObjects arrayOfObjects = new ArrayOfObjects(arraySize);
    
        // Create and configure CustomObject instances to be placed in the array
        for (int i = 0; i < arraySize; i++) {
            CustomObject customObject = new CustomObject();
            customObject.configure();
            arrayOfObjects.setObjectAtIndex(i, customObject);
        }
    
        scanner.close();
        return arrayOfObjects;
    }
    
}