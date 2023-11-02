import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;


public class SerializerTest {
    private Serializer serializer;

    @Before
    public void setUp() {
        serializer = new Serializer();
    }
    @Test
    public void testSerializeSimpleObject() {
        // Create a SimpleObject
        SimpleObject simpleObject = new SimpleObject();
        simpleObject.setIntValue(42);
        simpleObject.setDoubleValue(3.14);

        // Create a Serializer
        Serializer serializer = new Serializer();

        // Serialize the SimpleObject
        Document serializedDocument = serializer.serialize(simpleObject);

        //Convert the Document to a string (you may need to adjust this based on your serialization format)
        String serializedString = convertDocumentToString(serializedDocument);

        // Example: Assert that the serialized output contains expected values
        assertTrue(serializedString.contains("42"));
        assertTrue(serializedString.contains("3.14"));
    }

    @Test
    public void testSerializeComplexObject() {
        // Create a ComplexObject
        ComplexObject complexObject = new ComplexObject();
        
        // Configure the ComplexObject
        complexObject.configure();

        // Serialize the ComplexObject
        Document serializedDocument = serializer.serialize(complexObject);

        String serializedString = convertDocumentToString(serializedDocument);

        // Example: Assert that the serialized output contains expected values
        assertTrue(serializedString.contains("intValue")); // Check for the integer value
        assertTrue(serializedString.contains("doubleValue")); // Check for the double value
        assertTrue(serializedString.contains("SubObject1")); // Check for circular reference
        assertTrue(serializedString.contains("SubObject2")); // Check for circular reference
    }

    @Test
    public void testSerializeArrayObject() {
        // Create an ArrayObject
        int arraySize = 3; // Set the array size for testing
        ArrayObject arrayObject = new ArrayObject(arraySize);
        
        // Set values for the elements
        arrayObject.setIntArrayValue(0, 10);
        arrayObject.setIntArrayValue(1, 20);
        arrayObject.setIntArrayValue(2, 30);

        // Serialize the ArrayObject
        Document serializedDocument = serializer.serialize(arrayObject);

        String serializedString = convertDocumentToString(serializedDocument);

        //Assert that the serialized output contains expected values
        assertTrue(serializedString.contains("<value>10")); // Check for element value
        assertTrue(serializedString.contains("<value>20")); // Check for element value
        assertTrue(serializedString.contains("<value>30")); // Check for element value
    }

    @Test
    public void testSerializeArrayOfObjects() {
        // Create an ArrayOfObjects
        int arraySize = 2; // Set the array size for testing
        ArrayOfObjects arrayOfObjects = new ArrayOfObjects(arraySize);

        // Create and configure CustomObject instances
        CustomObject customObject1 = new CustomObject();
        customObject1.setIntValue(42);

        CustomObject customObject2 = new CustomObject();
        customObject2.setIntValue(23);

        // Set the objects in the ArrayOfObjects
        arrayOfObjects.setObjectAtIndex(0, customObject1);
        arrayOfObjects.setObjectAtIndex(1, customObject2);

        // Serialize the ArrayOfObjects
        Document serializedDocument = serializer.serialize(arrayOfObjects);

        String serializedString = convertDocumentToString(serializedDocument);

        //Assert that the serialized output contains expected values
        assertTrue(serializedString.contains("<value>42")); // Check for element value
        assertTrue(serializedString.contains("<value>23")); // Check for element value
        assertTrue(serializedString.contains("<reference>1")); // Check for element value
        assertTrue(serializedString.contains("<reference>2")); // Check for element value
    }

    @Test
    public void testSerializeObjectCollection() {
        // Create an ObjectCollection
        ObjectCollection collection = new ObjectCollection();

        // Create and configure CustomObject instances
        int numObjects = 2; // Set the number of objects for testing

        for (int i = 0; i < numObjects; i++) {
            CustomObject customObject = new CustomObject();
            customObject.setIntValue(42 + i); // Setting unique values for testing
            collection.addObject(customObject);
        }

        // Serialize the ObjectCollection
        Document serializedDocument = serializer.serialize(collection);

        //Convert the Document to a string (you may need to adjust this based on your serialization format)
        String serializedString = convertDocumentToString(serializedDocument);

        // Example: Assert that the serialized output contains expected values
        assertTrue(serializedString.contains("<value>42")); // Check for element value
        assertTrue(serializedString.contains("<value>43")); // Check for element value
        assertTrue(serializedString.contains("<reference>1")); // Check for element value
        assertTrue(serializedString.contains("<reference>2")); // Check for element value
    }

    // Helper method to convert a Document to a string
    private String convertDocumentToString(Document document) {
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        return xmlOutputter.outputString(document);
    }

}