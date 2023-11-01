import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.ByteArrayOutputStream;

public class Serializer {
    private IdentityHashMap<Object, Integer> objectIds = new IdentityHashMap<>();
    private int currentId = 0;

    String serverAddress = "localhost"; // Change this to the server's IP address
    int serverPort = 1234; // Change this to the server's port
    
    // Used to store references
    private Map<Object, Element> references = new IdentityHashMap<>();

    public Document serialize(Object obj) {
        Element root = new Element("serialized");
        Document document = new Document(root);

        // Serialize the object and add it to the XML document
        serializeObject(obj, root);
        try {
        // Output the XML to the console
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        System.out.println(xmlOutputter.outputString(document));

        // Create a socket to connect to the server
        Socket socket = new Socket(serverAddress, serverPort);

        // Get the output stream of the socket
        OutputStream outputStream = socket.getOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);


        // Convert the JDOM Document to bytes
       // XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            xmlOutputter.output(document, byteArrayOutputStream);
        byte[] documentBytes = byteArrayOutputStream.toByteArray();

        // Send the JDOM Document bytes to the server
        bufferedOutputStream.write(documentBytes);
        
        
        // Close the streams and socket
        bufferedOutputStream.flush();
        socket.close();

        System.out.println("XML file sent successfully.");

        }catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    private void serializeObject(Object obj, Element parent) {
        if (obj == null) {
            return;
        }
    
        Element objectElement = createObjectElement(obj);
        parent.addContent(objectElement);
    
        Integer objectId = getObjectIdentifier(obj);
        if (objectId == null) {
            objectId = assignNewIdentifier(obj);
        }
    
        objectElement.setAttribute("id", String.valueOf(objectId));
        storeObjectReference(obj, objectElement);
        serializeObjectProperties(obj, objectElement);
    }
    
    private Element createObjectElement(Object obj) {
        Element objectElement = new Element("object");
        objectElement.setAttribute("class", obj.getClass().getName());
        return objectElement;
    }
    
    private Integer getObjectIdentifier(Object obj) {
        return objectIds.get(obj);
    }
    
    private Integer assignNewIdentifier(Object obj) {
        Integer objectId = currentId;
        objectIds.put(obj, objectId);
        currentId++;
        return objectId;
    }
    
    private void storeObjectReference(Object obj, Element objectElement) {
        references.put(obj, objectElement);
    }
    
    private void serializeObjectProperties(Object obj, Element objectElement) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            Element fieldElement = new Element("field");
            fieldElement.setAttribute("name", field.getName());
            fieldElement.setAttribute("declaringClass", field.getDeclaringClass().getName());
    
            try {
                Object value = field.get(obj);
                if (value != null) {
                    if (fieldType.isArray()) {
                        serializeArrayField(fieldElement, value, fieldType);
                    } else if (Collection.class.isAssignableFrom(fieldType)) {
                        serializeCollectionField(fieldElement, value);
                    } else if (!fieldType.isPrimitive()) {
                        serializeReferenceField(fieldElement, value);
                    } else {
                        serializePrimitiveField(fieldElement, value);
                    }
                } else {
                    fieldElement.addContent(new Element("null"));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
    
            objectElement.addContent(fieldElement);
        }
    }
    
    private void serializeArrayField(Element fieldElement, Object value, Class<?> fieldType) {
        if (fieldType.getComponentType().isPrimitive()) {
            // Array of primitives
            Element arrayElement = new Element("object");
            arrayElement.setAttribute("class", fieldType.getName());
            arrayElement.setAttribute("length", String.valueOf(Array.getLength(value)));
    
            for (int i = 0; i < Array.getLength(value); i++) {
                Element valueElement = new Element("value");
                valueElement.setText(Array.get(value, i).toString());
                arrayElement.addContent(valueElement);
            }
    
            fieldElement.addContent(arrayElement);
        } else {
            // Array of objects
            Element arrayElementObj = new Element("object");
            arrayElementObj.setAttribute("class", fieldType.getName());
            arrayElementObj.setAttribute("length", String.valueOf(Array.getLength(value)));
    
            for (int i = 0; i < Array.getLength(value); i++) {
                Object arrayElement = Array.get(value, i);
                if (arrayElement != null) {
                    Element arrayElementElement = new Element("reference");
                    Integer referenceId = objectIds.get(arrayElement);
                    if (referenceId != null) {
                        arrayElementElement.setText(String.valueOf(referenceId));
                    } else {
                        serializeObject(arrayElement, arrayElementObj); // Pass arrayElementObj as the parent
                    }
                    arrayElementObj.addContent(arrayElementElement);
                } else {
                    arrayElementObj.addContent(new Element("null"));
                }
            }
            fieldElement.addContent(arrayElementObj);
        }
    }
    
    private void serializeCollectionField(Element fieldElement, Object value) {
        // Handle collections
        Collection<?> collectionValue = (Collection<?>) value;
        Element collectionElement = new Element("object");
        collectionElement.setAttribute("class", value.getClass().getName());
        collectionElement.setAttribute("size", String.valueOf(collectionValue.size()));
    
        for (Object collectionElementValue : collectionValue) {
            Element collectionItemElement = new Element("reference");
            Integer referenceId = objectIds.get(collectionElementValue);
            if (referenceId != null) {
                collectionItemElement.setText(String.valueOf(referenceId));
            } else {
                serializeObject(collectionElementValue, collectionElement); // Pass collectionElement as the parent
            }
            collectionElement.addContent(collectionItemElement);
        }
    
        fieldElement.addContent(collectionElement);
    }
    
    private void serializeReferenceField(Element fieldElement, Object value) {
        // Serialize reference fields
        Element valueElement = new Element("reference");
        Integer referenceId = objectIds.get(value);
        if (referenceId != null) {
            valueElement.setText(String.valueOf(referenceId));
        } else {
            serializeObject(value, fieldElement); // Pass fieldElement as the parent
        }
        fieldElement.addContent(valueElement);
    }
    
    private void serializePrimitiveField(Element fieldElement, Object value) {
        // Serialize primitive fields
        Element valueElement = new Element("value");
        valueElement.setText(value.toString());
        fieldElement.addContent(valueElement);
    }
}





