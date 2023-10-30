import java.util.Scanner;

public class ArrayOfObjects {
    public CustomObject[] objectArray;

    public ArrayOfObjects(int size) {
        objectArray = new CustomObject[size];
    }

    public void setObjectAtIndex(int index, CustomObject object) {
        if (index >= 0 && index < objectArray.length) {
            objectArray[index] = object;
        } else {
            System.out.println("Invalid index. Index must be between 0 and " + (objectArray.length - 1));
        }
    }

    public CustomObject[] getObjectArray() {
        return objectArray;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the array size: ");
        int arraySize = scanner.nextInt();
        ArrayOfObjects arrayOfObjects = new ArrayOfObjects(arraySize);

        // Create and configure CustomObject instances to be placed in the array
        for (int i = 0; i < arraySize; i++) {
           
            CustomObject customObject = new CustomObject(); // Create a new CustomObject
            
            customObject.configure(); // Configure the CustomObject
           
            arrayOfObjects.setObjectAtIndex(i, customObject);
          
        }

        
        CustomObject[] objects = arrayOfObjects.getObjectArray();
        System.out.println("ArrayOfObjects Information:");
        for (int i = 0; i < objects.length; i++) {
            System.out.println("Element " + i + ": " + objects[i].getInfo());
        }
        Serializer serializer = new Serializer();
        serializer.serialize(arrayOfObjects);
    }
}

class CustomObject {
    public int intValue;
   // public String stringValue;

    public void configure() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter an integer value: ");
        intValue = scanner.nextInt();

        scanner.nextLine();

        // System.out.print("Enter a string value: ");
        // stringValue = scanner.nextLine();


    }

    public String getInfo() {
        return "Int Value: " + intValue;
    }
}