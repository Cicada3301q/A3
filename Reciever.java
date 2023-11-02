import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
//Code taken from Kimiya's tutorial on socket connections

public class Reciever {

    public static void main(String[] args) {
        int serverPort = 1234; // Change this to the port you want to use for the server

        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(serverPort);

            System.out.println("Waiting for a connection...");

            // Accept a connection from a client
            Socket socket = serverSocket.accept();

            System.out.println("Connection established.");

            // Get the input stream of the socket
            InputStream inputStream = socket.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            // Build the XML document using JDOM SAXBuilder
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(bufferedInputStream);

            // You can now work with the JDOM Document 'document'
            // For example, you can print the root element
            System.out.println("Root Element: " + document.getRootElement().getName());
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            String xmlString = xmlOutputter.outputString(document);
            // System.out.println(xmlString);

            Deserializer deserializer = new Deserializer();
            Object deserializedObject = deserializer.deserialize(document);
    
            Inspector inspector = new Inspector();
            inspector.inspect(deserializedObject, true);

            // Close the streams and socket
            bufferedInputStream.close();
            socket.close();
            serverSocket.close();

            System.out.println("XML document received and parsed successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
