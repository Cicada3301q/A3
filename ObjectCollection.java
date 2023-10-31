import java.util.ArrayList;
import java.util.Scanner;

public class ObjectCollection {
    public ArrayList<CustomObject> objectList;

    public ObjectCollection() {
        objectList = new ArrayList<>();
    }

    public void addObject(CustomObject object) {
        objectList.add(object);
    }

    public ArrayList<CustomObject> getObjectList() {
        return objectList;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of objects to create: ");
        int numObjects = scanner.nextInt();
        ObjectCollection collection = new ObjectCollection();

        for (int i = 0; i < numObjects; i++) {
            CustomObject customObject = new CustomObject();
            customObject.configure();
            collection.addObject(customObject);
        }

        scanner.close();

        ArrayList<CustomObject> objects = collection.getObjectList();
        System.out.println("ObjectCollection Information:");
        for (int i = 0; i < objects.size(); i++) {
            System.out.println("Element " + i + ": " + objects.get(i).getInfo());
        }
        Serializer serializer = new Serializer();
        serializer.serialize(collection);
    }
}