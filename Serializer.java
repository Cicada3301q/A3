import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.lang.reflect.*;
import java.util.IdentityHashMap;

public class Serializer {
    private IdentityHashMap<Object, Integer> objectIds = new IdentityHashMap<>();
    private int currentId = 0;

    public Document serialize(Object obj) {
        Element root = new Element("serialized");
        Document document = new Document(root);

        // Serialize the object and add it to the XML document
        serializeObject(obj, root);
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
        parent.addContent(objectElement);

        
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            // field.setAccessible(true);
            Element fieldElement = new Element("field");
            fieldElement.setAttribute("name", field.getName());
            fieldElement.setAttribute("declaringClass", field.getDeclaringClass().getName());

            try {
                // Serialize the field's value
                Object value = field.get(obj);
                if (value != null) {
                    Element valueElement = new Element("value");
                    valueElement.setText(value.toString());
                    fieldElement.addContent(valueElement);

                    // If the field is an object reference, recursively serialize it
                    if (!field.getType().isPrimitive()) {
                        serializeObject(value, fieldElement);
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