package core.configmanager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private String workingDir;
    private String xmlPath;
    private ConfigModel configObject;

    public ConfigManager() {
        this.workingDir = System.getProperty("user.dir");
        this.xmlPath = workingDir + "\\config.xml";
        this.configObject = new ConfigModel();
        if (this.checkConfigExist()) {
            this.readConfig();
        } else {
            this.writeConfig();
        }
    }

    private boolean checkConfigExist() {
        File xmlFileDir = new File(this.xmlPath);
        boolean fileExist = xmlFileDir.exists();
        return fileExist;
    }

    private void readConfig() {
        try {
            //Get Document Builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            //Build Document
            Document document = builder.parse(new File(this.xmlPath));

            //Normalize the XML Structure; It's just too important !!
            document.getDocumentElement().normalize();

            //Here comes the root node
            Element root = document.getDocumentElement();
            System.out.println(root.getNodeName());

            Node apkToolNode = root.getElementsByTagName("apktool").item(0);
            String tempApktool = apkToolNode.getTextContent();
            if (tempApktool != null)
                this.configObject.setApkToolDirectory(apkToolNode.getTextContent());
            else
                this.configObject.setApkToolDirectory("");

            System.out.println("============================");
            System.out.println("apktoolDir: "+apkToolNode.getTextContent());
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException tfe) {
            tfe.printStackTrace();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }
    }

    public void writeConfig() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("configuration");
            doc.appendChild(rootElement);

            // Insert config elements here
            // apktool element
            Element apktool = doc.createElement("apktool");
            rootElement.appendChild(apktool);
            if (this.configObject.getApkToolDirectory().length() > 0) {
                apktool.appendChild(doc.createTextNode(this.configObject.getApkToolDirectory()));
            } else {
                apktool.appendChild(doc.createTextNode(""));
            }
            // set attribute to staff element
            //Attr attr = doc.createAttribute("id");
            //attr.setValue("1");
            //staff.setAttributeNode(attr);

            // shorten way
            // staff.setAttribute("id", "1");

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlPath));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);
            System.out.println("Configuration file saved!");
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public ConfigModel getConfigObject() {
        return configObject;
    }

    public void setConfigObject(ConfigModel configObject) {
        this.configObject = configObject;
    }
}
