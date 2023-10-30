import java.util.Scanner;

public class ArrayObject {
    public int[] intArray;

    public ArrayObject(int size) {
        intArray = new int[size];
    }

    public void setIntArrayValue(int index, int value) {
        if (index >= 0 && index < intArray.length) {
            intArray[index] = value;
        } else {
            System.out.println("Invalid index. Index must be between 0 and " + (intArray.length - 1));
        }
    }

    public int[] getIntArray() {
        return intArray;
    }

    public static void main(String[] args) {
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

        int[] values = arrayObject.getIntArray();
        System.out.println("ArrayObject Information:");
        for (int i = 0; i < values.length; i++) {
            System.out.println("Element " + i + ": " + values[i]);
        }
        Serializer serializer = new Serializer();
        serializer.serialize(arrayObject);
    }
}