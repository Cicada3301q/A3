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
    
        Element objectElement = new Element("object");
        objectElement.setAttribute("class", obj.getClass().getName());
    
        // Check if the object has already been assigned an ID
        Integer objectId = objectIds.get(obj);
        if (objectId == null) {
            // Assign a new unique ID
            objectId = currentId;
            objectIds.put(obj, objectId);
            currentId++;
        }
    
        objectElement.setAttribute("id", String.valueOf(objectId));
        parent.addContent(objectElement);
    
        // Store a reference to this object for possible later use
        references.put(obj, objectElement);
    
        // Add logic to serialize object properties
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            //field.setAccessible(true);
            Class<?> fieldType = field.getType();
            Element fieldElement = new Element("field");
            fieldElement.setAttribute("name", field.getName());
            fieldElement.setAttribute("declaringClass", field.getDeclaringClass().getName());
    
            try {
                // Serialize the field's value
                Object value = field.get(obj);
                if (value != null) {
                    if(fieldType.isArray()){
                        if (fieldType.getComponentType().isPrimitive()) {
                            // Array of primitives
                            serializeArrayOfPrimitives(fieldElement, value, objectElement, fieldType);
                        }
                        else {
                            // Array of objects
                            serializeArrayOfObjects(fieldElement, value, objectElement, fieldType);
                        }
                    }else if (Collection.class.isAssignableFrom(fieldType)) {
                    // Handle collections
                    serializeCollectionField(fieldElement, value, fieldType);
                    }
                    else if (!field.getType().isPrimitive()) {
                        serializeReferenceField(fieldElement, value, objectElement);
                    } else {
                        // For primitive fields, just store the value as text
                        Element valueElement = new Element("value");
                        valueElement.setText(value.toString());
                        fieldElement.addContent(valueElement);
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
    private void serializeCollectionField(Element fieldElement, Object value, Class<?> fieldType) {
        // Handle collections
        Collection<?> collectionValue = (Collection<?>) value;
    
        Element collectionElement = new Element("object");
        collectionElement.setAttribute("class", fieldType.getName());
        collectionElement.setAttribute("size", String.valueOf(collectionValue.size()));
    
        for (Object collectionElementValue : collectionValue) {
            Integer referenceId = objectIds.get(collectionElementValue);
            if (referenceId != null) {
                // Use the existing identifier for the reference field
                Element collectionItemElement = new Element("reference");
                collectionItemElement.setText(String.valueOf(referenceId));
                fieldElement.addContent(collectionItemElement);
            } else {
                // If not already registered, register the object and serialize it
                referenceId = currentId;
                objectIds.put(collectionElementValue, referenceId);
                currentId++;
                Element collectionItemElement = new Element("reference");
                collectionItemElement.setText(String.valueOf(referenceId));
                fieldElement.addContent(collectionItemElement);
                serializeObject(collectionElementValue, collectionElement);
            }
        }
    
        fieldElement.addContent(collectionElement);
    }
       private void serializeReferenceField(Element fieldElement, Object value, Element objectElement){
                // If the field is an object reference, recursively serialize it
            Element valueElement = new Element("reference");
            Integer referenceId = objectIds.get(value); // Check if the object has an ID
            if (referenceId != null) {
                valueElement.setText(String.valueOf(referenceId));
                fieldElement.addContent(valueElement);
            } else {
                // If the referenced object has not been serialized yet, serialize it
                serializeObject(value, objectElement);
            }}

            private void serializeArrayOfObjects(Element fieldElement, Object value, Element objectElement, Class<?> fieldType) {
                // Array of objects
                Element arrayElementObj = new Element("object");
                arrayElementObj.setAttribute("class", fieldType.getName());
                arrayElementObj.setAttribute("length", String.valueOf(Array.getLength(value)));
            
                for (int i = 0; i < Array.getLength(value); i++) {
                    Object arrayElement = Array.get(value, i);
                    if (arrayElement != null) {
                        Integer referenceId = objectIds.get(arrayElement);
                        if (referenceId != null) {
                            // Use the existing identifier for the reference field
                            Element arrayElementElement = new Element("reference");
                            arrayElementElement.setText(String.valueOf(referenceId));
                            fieldElement.addContent(arrayElementElement);
                        } else {
                            // If not already registered, register the object and serialize it
                            referenceId = currentId;
                            objectIds.put(arrayElement, referenceId);
                            currentId++;
                            Element arrayElementElement = new Element("reference");
                            arrayElementElement.setText(String.valueOf(referenceId));
                            fieldElement.addContent(arrayElementElement);
                            serializeObject(arrayElement, objectElement);
                        }
                    } else {
                        arrayElementObj.addContent(new Element("null"));
                    }
                }
            
                fieldElement.addContent(arrayElementObj);
            }
            private void serializeArrayOfPrimitives(Element fieldElement, Object value, Element objectElement, Class<?> fieldType){
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
            }
}





