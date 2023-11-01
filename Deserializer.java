import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.util.List;

public class Deserializer {
    private Map<Integer, Object> objectMap = new HashMap<>();

    public Object deserialize(Document document) {
        Element root = document.getRootElement();
        return deserializeObject(root.getChild("object"));
    }

    private Object deserializeObject(Element element) {
        String className = element.getAttributeValue("class");
        int objectId = Integer.parseInt(element.getAttributeValue("id"));
    
        if (objectMap.containsKey(objectId)) {
            // Object has already been deserialized, return the cached object
            return objectMap.get(objectId);
        }
    
        // Create an instance of the object
        Object obj = createObject(className);
        objectMap.put(objectId, obj);
    
        // Deserialize the object's fields
        for (Element fieldElement : element.getChildren("field")) {
            String fieldName = fieldElement.getAttributeValue("name");
            Class<?> declaringClass = getClass(fieldElement.getAttributeValue("declaringClass"));
            Element fieldValueElement = fieldElement.getChild("value");
    
            if (fieldValueElement != null) {
                // Deserialize primitive field
                setField(obj, fieldName, declaringClass, fieldValueElement.getText());
            } else {
                // Deserialize reference field
                Element referenceElement = fieldElement.getChild("reference");
                if (referenceElement != null) {
                    int referenceId = Integer.parseInt(referenceElement.getText());
                    Object referencedObject = objectMap.get(referenceId);
                    setField(obj, fieldName, declaringClass, referencedObject);
                }
            }
        }
    
        // Process objects referenced within this object
        for (Element nestedObjectElement : element.getChildren("object")) {
            System.out.println("Element is" + nestedObjectElement);
            deserializeObject(nestedObjectElement);
        }
    
        return obj;
    }

    private Object createObject(String className) {
        try {
            Class<?> objectClass = Class.forName(className);
            return objectClass.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setField(Object obj, String fieldName, Class<?> declaringClass, Object value) {
        try {
            java.lang.reflect.Field field = declaringClass.getDeclaredField(fieldName);
            System.out.println("setfield" + field.getType());
            field.setAccessible(true);
            if (field.getType() == int.class) {
                field.setInt(obj, Integer.parseInt(value.toString()));
            } else if (field.getType() == double.class) {
                field.setDouble(obj, Double.parseDouble(value.toString()));
            } else if (field.getType() == String.class) {
                field.set(obj, value.toString());
            } else {
                // Handle other types or throw an exception if needed
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}