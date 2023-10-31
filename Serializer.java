import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

public class Serializer {
    private IdentityHashMap<Object, Integer> objectIds = new IdentityHashMap<>();
    private int currentId = 0;
    
    // Used to store references
    private Map<Object, Element> references = new IdentityHashMap<>();

    public Document serialize(Object obj) {
        Element root = new Element("serialized");
        Document document = new Document(root);

        // Serialize the object and add it to the XML document
        serializeObject(obj, root);

        // Output the XML to the console
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        System.out.println(xmlOutputter.outputString(document));

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
                        else {
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
                                        serializeObject(arrayElement, objectElement);
                                    }
                                    arrayElementObj.addContent(arrayElementElement);
                                } else {
                                    arrayElementObj.addContent(new Element("null"));
                                }
                            }
                           fieldElement.addContent(arrayElementObj);
                        }
                    }else if (Collection.class.isAssignableFrom(fieldType)) {
                    // Handle collections
                    Element collectionElement = new Element("object");
                    collectionElement.setAttribute("class", fieldType.getName());
                    collectionElement.setAttribute("size", String.valueOf(((Collection<?>) value).size()));

                    for (Object collectionElementValue : (Collection<?>) value) {
                        Element collectionItemElement = new Element("reference");
                        Integer referenceId = objectIds.get(collectionElementValue);
                        if (referenceId != null) {
                            collectionItemElement.setText(String.valueOf(referenceId));
                        } else {
                            serializeObject(collectionElementValue, collectionElement);
                        }
                        collectionElement.addContent(collectionItemElement);
                    }

                    fieldElement.addContent(collectionElement);
                }
                    else if (!field.getType().isPrimitive()) {
                        // If the field is an object reference, recursively serialize it
                        Element valueElement = new Element("reference");
                        Integer referenceId = objectIds.get(value); // Check if the object has an ID
                        if (referenceId != null) {
                            valueElement.setText(String.valueOf(referenceId));
                            fieldElement.addContent(valueElement);
                        } else {
                            // If the referenced object has not been serialized yet, serialize it
                            serializeObject(value, objectElement);
                        }
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
}





