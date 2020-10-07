package main.rest;

import com.lis.qr_back.additional.Utility;
import com.lis.qr_back.dao.AddressDAO;
import com.lis.qr_back.dao.InventoryDAO;
import com.lis.qr_back.dao.RoomDAO;
import com.lis.qr_back.model.Inventory;
import com.lis.qr_back.property.FileUploadProperties;
import com.sun.net.httpserver.Headers;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

@RestController
@RequestMapping(value = "/api/uploadFile")
@Log
public class FileRestController {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    private final Path fileLocation;
    private AddressDAO addressDAO;
    private RoomDAO roomDAO;
    private InventoryDAO inventoryDAO;

    Utility utility = new Utility();

    @Autowired
    public FileRestController(FileUploadProperties fileUploadProperties, AddressDAO addressDAO, RoomDAO roomDAO, InventoryDAO inventoryDAO) {
        this.fileLocation = Paths.get(fileUploadProperties.getUploadDir()).toAbsolutePath().normalize();
        this.addressDAO = addressDAO;
        this.roomDAO = roomDAO;
        this.inventoryDAO = inventoryDAO;
        fileLocation.toFile().mkdir();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile multipartFile) {

        if (multipartFile != null) {
            String fileName = "\\" + multipartFile.getOriginalFilename();
            Headers headers = new Headers();
            try {
                File myFile = new File(fileLocation + fileName);
                multipartFile.transferTo(myFile);

                headers.add("Now stored in: ", myFile.getAbsolutePath());
                log.logp(Level.WARNING, "FileRestController", "uploadFile",
                        "File was successfully uploaded: " + myFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ResponseEntity(headers, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/check_inventory")
    public ResponseEntity<Map<String, Boolean>> checkInventoryNums(@RequestParam("file") MultipartFile multipartFile) {

        //-------------Parse file----------------//


        if (multipartFile != null) {

            List<String> inventory_nums = new ArrayList<>();

            try {

                InputStream in = new BufferedInputStream(multipartFile.getInputStream());
                inventory_nums = utility.xmlOneColumnToListParser(in);


//-----check-----
                log.logp(Level.WARNING, "FileRestController", "checkInventoryNums",
                        "The file has been read: " + inventory_nums.size() + " inventory_nums");
            } catch (ParserConfigurationException | XPathExpressionException | SAXException | IOException e) {
                e.printStackTrace();
                log.logp(Level.WARNING, "FileRestController", "checkInventoryNums",
                        "Exception occurred while file parsing: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
//--------

        // -------------Make a Map <inventory_num, ifExistEquipment>----------------//


            if(inventory_nums != null){

                Map<String, Boolean> isInventoryNumHasEquipment = new HashMap<>();
                int check_result;

                //check all the inventory_nums

                for (String inventory_num: inventory_nums){

                    check_result = inventoryDAO.checkIfInventoryNumHasEquipment(inventory_num);

                    if(check_result == 0){
                        isInventoryNumHasEquipment.put(inventory_num, false);
                    }else{
                        isInventoryNumHasEquipment.put(inventory_num, true);
                    }
                }
                log.logp(Level.WARNING, "FileRestController", "checkInventoryNums",
                        "Return map with results. Size: "+isInventoryNumHasEquipment.size());
                return new ResponseEntity<>(isInventoryNumHasEquipment, HttpStatus.OK);

            }

        }
        log.info("No file was found, please send again");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/{address_id}/{room}")
    public ResponseEntity uploadXmlFileToParse(@RequestParam("file") MultipartFile multipartFile, @PathVariable("room") int room,
                                               @PathVariable("address_id") int address_id) {


//-------check--------

        //check the tp_id
        int address_check = addressDAO.ifAddressExistsById(address_id);
        if (address_check == 0) {
            log.logp(Level.WARNING, "FileRestController", "uploadXmlFileToParse",
                    "Bad request, the addressString with number: " + address_id + " was not found");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //check the room
        if (room != 8888) {
            int room_check = addressDAO.ifRoomExistsByAddressId(room, address_id);
            if (room_check == 0) {
                log.logp(Level.WARNING, "FileRestController", "uploadXmlFileToParse",
                        "Bad request, the room with number: " + room + " was not found");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
//-------------


        if (multipartFile != null) {
            List<Inventory> inventories;
            try {

                InputStream in = new BufferedInputStream(multipartFile.getInputStream());
                inventories = utility.xmlToInventoryParser(in, room, address_id);


//-----check-----
                log.logp(Level.WARNING, "FileRestController", "uploadXmlFileToParse",
                        "The file has been read: " + inventories.size() + " inventory object");
            } catch (ParserConfigurationException | XPathExpressionException | SAXException | IOException e) {
                e.printStackTrace();
                log.logp(Level.WARNING, "FileRestController", "uploadXmlFileToParse",
                        "Exception occurred while file parsing: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
//--------

            if (inventories != null) {
                String[] insert_result;
                try {
                    insert_result = inventoryDAO.insertListInventory(inventories);
                } catch (Exception e) {
                    log.logp(Level.WARNING, "FileRestController", "uploadXmlFileToParse",
                            "Exception while file loading " + e.getMessage() + e.getCause());
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

                }

                //-----check-----
                //is there is no mistaken values in the array - its ok
                if (insert_result != null && insert_result[0] == null) {
                    log.logp(Level.WARNING, "FileRestController", "uploadXmlFileToParse",
                            "File was successfully inserted to db");
                    return new ResponseEntity(HttpStatus.OK);
                } else {
                    log.logp(Level.WARNING, "FileRestController", "uploadXmlFileToParse",
                            "Some inventories wasn't load: ");

                    for (int i = 0; i < insert_result.length; i++) {
                        log.logp(Level.WARNING, "FileRestController", "uploadXmlFileToParse",
                                "----" + insert_result[i]);
//--------
                    }
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }

            } else {
                log.logp(Level.WARNING, "FileRestController", "uploadXmlFileToParse",
                        "Parsed list is empty");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            log.logp(Level.WARNING, "FileRestController", "uploadXmlFileToParse",
                    "Bad request, the file is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
