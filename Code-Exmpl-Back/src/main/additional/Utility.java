package main.additional;

import com.lis.qr_back.model.Equipment;
import com.lis.qr_back.model.Inventory;
import lombok.extern.java.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log
public class Utility {

    /**
     * XML  file parser
     *
     * Turn XML (from excel file with two columns: name, inventory_num) to inventory object
     * return ArrayList<Inventory>;
     */
    public List<Inventory> xmlToInventoryParser(InputStream inputStream, int room, int address_id) throws ParserConfigurationException,
            IOException, SAXException, XPathExpressionException {
        List<Inventory> inventories = new ArrayList<>();

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(inputStream);

        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = xPath.compile("//Row");

        //get rows
        NodeList rows = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
        Inventory inventory;
        NodeList cells;
        Node cell;
        String data;


        for (int i = 0; i < rows.getLength(); i++) {

            inventory = new Inventory();
            //get rows children
            cells = rows.item(i).getChildNodes();

            //*** cells.item(0) and (2) contains null value, idk why

            //get first cell - name
            cell = cells.item(1);
            data = cell.getChildNodes().item(0).getTextContent();
            inventory.setName(data);

            //get third cell - data
            cell = cells.item(3);
            data = cell.getChildNodes().item(0).getTextContent();
            inventory.setInventory_num(data);

            inventory.setRoom(room);
            inventory.setAddress_id(address_id);

            inventories.add(inventory);


        }
        return inventories;
    }

    /**
     * common XML file parser, only for ONE column!!!
     *
     * Parse xml table, send result to console
     */

    public List<String> xmlOneColumnToListParser(InputStream inputStream) throws ParserConfigurationException,
            IOException, SAXException, XPathExpressionException {
        List<String> inventory_nums = new ArrayList<>();

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(inputStream);

        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = xPath.compile("//Row");

        //get rows
        NodeList rows = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
        NodeList cells;
        Node cell;
        String data;


        for (int i = 0; i < rows.getLength(); i++) {
            //get rows children
            cells = rows.item(i).getChildNodes();

            //*** cells.item(0) and (2) contains null value, idk why

            //get first cell - inventory_num or whatever
            cell = cells.item(1);
            data = cell.getChildNodes().item(0).getTextContent();

            inventory_nums.add(data);

        }
        return inventory_nums;
    }

    /**
     * common XML file parser
     *
     * Parse xml table, send result to console
     */

    public void xmlParser(File xmlFile) throws ParserConfigurationException, IOException, SAXException,
            XPathExpressionException {


        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(xmlFile);

        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = xPath.compile("//Row");

        //get rows
        NodeList rows = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
        NodeList cells;
        Node cell;
        Node data = null;


        for (int i = 0; i < rows.getLength(); i++) {

            //get rows children
            cells = rows.item(i).getChildNodes();

            for (int j = 0; j < cells.getLength(); j++) {
                //get cell
                cell = cells.item(j);

                if (cell.getNodeType() != Node.TEXT_NODE){
                    //get cell's child (one cell - one data)
                    data = cell.getChildNodes().item(0);

                    //-----raw data n-size n-elements---
                    if (data != null) {
                        System.out.println(data.getChildNodes().item(0).getTextContent());
                    }

                }

            }

        }
    }


    /**
     * Equipment to qr pic name
     */
    public String objectToName(Equipment equipment) {
        StringBuilder sb = new StringBuilder();

        sb.append("qr");

        if (equipment.getRoom() != null) {
            sb.append("_");
            sb.append(equipment.getRoom());
        }
        if (equipment.getType() != null) {
            sb.append("_");
            sb.append(equipment.getType());

        }
        if (equipment.getModel() != null) {
            sb.append("_");
            sb.append(equipment.getModel());
        }
        if (equipment.getSeries() != null) {
            sb.append("_");
            sb.append(equipment.getSeries());
        }
        if (equipment.getInventory_num() != null) {
            sb.append("_");
            sb.append(equipment.getInventory_num());
        }

        return sb.toString();
    }


    /**
     * Inventory to qr pic name
     */
    public String objectToName(Inventory inventory) {
        StringBuilder sb = new StringBuilder();

        sb.append("qr");

        if (inventory.getInventory_num() != null) {
            sb.append("_");
            sb.append(inventory.getInventory_num());
        }

        return sb.toString();
    }


    /*print map*/
    public static void printMap(Map<String, Object> map){
        for(Map.Entry m: map.entrySet()){
            System.out.println(m.getKey()+" "+m.getValue());
        }

    }
}
