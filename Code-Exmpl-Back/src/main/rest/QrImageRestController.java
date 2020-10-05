package main.rest;

import com.lis.qr_back.additional.QR.DocxCreator;
import com.lis.qr_back.additional.QR.QRCreator;
import com.lis.qr_back.additional.Utility;
import com.lis.qr_back.dao.EquipmentDAO;
import com.lis.qr_back.dao.InventoryDAO;
import com.lis.qr_back.dao.QrImageDAO;
import com.lis.qr_back.model.Equipment;
import com.lis.qr_back.model.Inventory;
import com.lis.qr_back.model.QrImage;
import lombok.extern.java.Log;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/qr")
@Log
public class QrImageRestController {

    private QrImageDAO qrImageDAO;
    private EquipmentDAO equipmentDAO;
    private InventoryDAO inventoryDAO;
    private QRCreator qrCreator;
    private Utility utility = new Utility();

    @SuppressWarnings("SpringJavaAutowiringInspection")
    public QrImageRestController(QrImageDAO qrImageDAO, EquipmentDAO equipmentDAO,
                                 InventoryDAO inventoryDAO, QRCreator qrCreator) {
        this.qrImageDAO = qrImageDAO;
        this.equipmentDAO = equipmentDAO;
        this.inventoryDAO = inventoryDAO;
        this.qrCreator = qrCreator;
    }


    @GetMapping("/{id}")
    public ResponseEntity<QrImage> getQrImageById(@PathVariable("id") int id) {
        QrImage qrImage = qrImageDAO.getById(id);

        return new ResponseEntity<>(qrImage, HttpStatus.OK);
    }


    @GetMapping("/equipment/{equipment_id}")
    public ResponseEntity<QrImage> getQrImageByEquipmentId(@PathVariable("equipment_id") int equipment_id) {
        QrImage qrImage = qrImageDAO.getByEquipmentId(equipment_id);

        if (qrImage == null) {
            log.logp(Level.WARNING, "QrImageRestController", "getQrImageByInventoryNum",
                    "Couldn't find any qr-img or equipment with following equipment id");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        log.info("Equipment and qr-image with equipment id " + equipment_id + " was found. Returning the image");
        return new ResponseEntity<>(qrImage, HttpStatus.OK);
    }


    @GetMapping("/inventory/{inventory_num}")
    public ResponseEntity<QrImage> getQrImageByInventoryNum(@PathVariable("inventory_num") String inventory_num) {
        QrImage qrImage = qrImageDAO.getByInventoryNum(inventory_num);

        if (qrImage == null) {
            log.logp(Level.WARNING, "QrImageRestController", "getQrImageByInventoryNum",
                    "Couldn't find any qr-img or equipment with following inventory_num");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        log.info("Equipment anfd qr-image with inventory number " + inventory_num + " was found. Returning the image");
        return new ResponseEntity<>(qrImage, HttpStatus.OK);
    }

    @GetMapping("/qr_file")
    public ResponseEntity<Resource>  getQrFileByURI(@RequestHeader("Location") String file_location) {
        log.info("File is here " + file_location);

        if (file_location != null) {

            File file = new File(file_location);

            if (file == null) {
                log.logp(Level.WARNING, "QrImageRestController", "getQrFileByPath",
                        "Can't find the file by the following uri");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }


            try {
                Path path = Paths.get(file.getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                return ResponseEntity.ok()
                        .contentLength(file.length())
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .body(resource);

            } catch (IOException e) {
                e.printStackTrace();
                log.logp(Level.WARNING, "QrImageRestController", "getQrFileByPath",
                        "Internal error " + e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }

        log.logp(Level.WARNING, "QrImageRestController", "getQrFileByPath",
                "The header \"file-location\" in null. Can't load the file.");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    //TODO: Determine what kind of json is going to be converted to QrImage

    @PostMapping()
    // JSON -> Stored QrImage
    public ResponseEntity<QrImage> addQrImage(@RequestBody String stringToConvert) {
        int insertedImgId = qrImageDAO.insertFromString(stringToConvert, "test");

        if (insertedImgId == 0) {
            log.logp(Level.WARNING, "QrImageRestController", "addQrImage", "Cannot create and store the image ");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //TODO: Replace with where its located info or error message
        QrImage qrImage = qrImageDAO.getById(insertedImgId);
        return new ResponseEntity<>(qrImage, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/save")
    // JSON -> Stored QrImage
    public ResponseEntity<String> saveQrImage(@RequestBody String stringToConvert) {
        String pathToSave = "./src/main/resources/qr/";

        String pictureDir;
        try {
            pictureDir = qrImageDAO.saveQrFromString(stringToConvert, pathToSave);
        } catch (IOException e) {
            e.printStackTrace();
            log.logp(Level.WARNING, "QrImageRestController", "Cannot create and save the image ",
                    "saveQrImage");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (pictureDir == null) {
            log.logp(Level.WARNING, "QrImageRestController", "saveQrImage",
                    "Cannot create and save the image ");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

        log.logp(Level.WARNING, "QrImageRestController", "saveQrImage",
                "Image was successfully saves in " + pictureDir);
        return new ResponseEntity<>(pictureDir, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/save_to_local/{id}")
    // JSON -> Stored QrImage
    public ResponseEntity<String> saveQrImageToLocal(@PathVariable("id") int id) {
        String pathToSave = "./src/main/resources/qr/";

        new File(pathToSave).mkdir();

        QrImage image = qrImageDAO.getById(id);
        if (image == null) {
            log.logp(Level.WARNING, "QrImageRestController", "saveQrImageToLocal", "Cannot find the image with id " + id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            QRCreator.qrPojoToBuffImageSavePng(image, pathToSave);
        } catch (IOException e) {
            log.logp(Level.WARNING, "QrImageRestController", "saveQrImageToLocal", "Cannot save the image ");
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        log.logp(Level.WARNING, "QrImageRestController", "saveQrImageToLocal", "Image was successfully saves in " + pathToSave);
        return new ResponseEntity<>(pathToSave, HttpStatus.OK);
    }


    /**
     * Creates QR-code picture for the equipments in the passed room and store it follow the given path
     */

    @RequestMapping(method = RequestMethod.POST, value = "/save_equipment/{id_room}")
    public ResponseEntity<String> saveAllEquipmentToQr(@PathVariable("id_room") Integer id_room) {
        String pathToSave = "./src/main/resources/qr/equipment/" + id_room + "/";

        /*create new folder for the pathToSave*/
        new File(pathToSave).mkdirs();

        /*get the equipment by room_id*/
        List<Equipment> equipments = equipmentDAO.getShortByRoomId(id_room);


        //----check-------
        if (equipments == null) {
            log.logp(Level.INFO, "EquipmentRestController", "getShortByRoomId",
                    "Can't get equipments with room id " + id_room);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        if (equipments.size() == 0) {
            log.logp(Level.WARNING, "QrImageRestController", "saveQrImage",
                    "The room " + id_room + " is empty. There is nothing to save...");
            return new ResponseEntity<>(pathToSave, HttpStatus.NO_CONTENT);
        }


        //save list to qr pictures
        saveListToQr(equipments, pathToSave);


        log.logp(Level.WARNING, "QrImageRestController", "saveQrImage",
                "Equipment QRImages from room " + id_room + " was successfully saves in " + pathToSave);
        return new ResponseEntity<>(pathToSave, HttpStatus.OK);
    }


    /**
     * ----!!!---TEMP---!!!----
     * <p>
     * Creates QR-code picture for the inventory in the passed room and store it follow the given path
     */

    @RequestMapping(method = RequestMethod.POST, value = "/save_inventory/{id_room}")
    public ResponseEntity saveAllInventoryToQr(@PathVariable("id_room") Integer id_room) {
        String pathToSave = "./src/main/resources/qr/inventory/" + id_room + "/";

        /*create new folder for the pathToSave*/
        new File(pathToSave).mkdirs();

        /*get the equipment by room_id*/
        List<Inventory> inventories = inventoryDAO.getShortInventoryByRoom(id_room);


        //----check-------
        if (inventories == null) {
            log.logp(Level.INFO, "EquipmentRestController", "getShortByRoomId",
                    "Can't get equipments with room id " + id_room);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        if (inventories.size() == 0) {
            log.logp(Level.WARNING, "QrImageRestController", "saveQrImage",
                    "The room " + id_room + " is empty. There is nothing to save...");
            return new ResponseEntity<>(pathToSave, HttpStatus.NO_CONTENT);
        }

        //save list to qr pictures
        saveListToQr(inventories, pathToSave);

        log.logp(Level.WARNING, "QrImageRestController", "saveQrImage",
                "Equipment QRImages from room " + id_room + " was successfully saves in " + pathToSave);
        return new ResponseEntity<>(pathToSave, HttpStatus.OK);
    }


    @PostMapping(value = "/form_doc")
    public ResponseEntity formQrDocForInventoryNums(@RequestBody List<String> inventory_nums) {
        String pathToSave = "./src/main/resources/qr/doc/";

        /*create new folder for the pathToSave*/
        new File(pathToSave).mkdirs();

//------check--------
        if (inventory_nums == null || inventory_nums.size() == 0) {
            log.logp(Level.WARNING, "QrImageRestController", "formQrDocForInventoryNums",
                    "The passed list of inventories is empty.");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
//-------------------

        List<QrImage> qrImages = new ArrayList<>();

        inventory_nums.forEach(inventory_num -> {
            QrImage temp_qr = qrImageDAO.getByInventoryNum(inventory_num);

            if (temp_qr != null) {
                qrImages.add(temp_qr);
                temp_qr = null;
            }
        });
//------check--------
        if (qrImages.size() == 0) {
            log.logp(Level.WARNING, "QrImageRestController", "formQrDocForInventoryNums",
                    "No qrImages to put into the doc");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
//-------------------


        log.info("There are " + qrImages.size() + " images to send");
        DocxCreator docxCreator = new DocxCreator();

        try {
            //get path to file
            File file = docxCreator.createDocx(qrImages);
            log.logp(Level.WARNING, "QrImageRestController", "formQrDocForInventoryNums",
                    "Doc created, returning the path");

            //return new ResponseEntity(headers, CREATED);
            //TODO: check this trick with uri at linux
            HttpHeaders headers = new HttpHeaders();
            headers.set("Location", file.getAbsolutePath());
            return new ResponseEntity(headers, CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            log.logp(Level.WARNING, "QrImageRestController", "formQrDocForInventoryNums",
                    "Error occurred, during the file creation");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



@PostMapping(value = "/for_room")
    public ResponseEntity formQrDocForRoom(@RequestBody String room) {

        log.info("Room "+room);
        String pathToSave = "./src/main/resources/qr/doc/";

        /*create new folder for the pathToSave*/
        new File(pathToSave).mkdirs();

//------check--------
        if (room == null ) {
            log.logp(Level.WARNING, "QrImageRestController", "formQrDocForRoom",
                    "The passed room is null.");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
//-------------------



     List<QrImage> qrImages = qrImageDAO.getByRoom(room);


//------check--------
        if (qrImages.size() == 0) {
            log.logp(Level.WARNING, "QrImageRestController", "formQrDocForRoom",
                    "No qrImages to put into the doc");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
//-------------------


        log.info("There are " + qrImages.size() + " images to send");
        DocxCreator docxCreator = new DocxCreator();

        try {
            //get path to file
            File file = docxCreator.createDocx(qrImages);
            log.logp(Level.WARNING, "QrImageRestController", "formQrDocForRoom",
                    "Doc created, returning the path");

            //return new ResponseEntity(headers, CREATED);
            //TODO: check this trick with uri at linux
            HttpHeaders headers = new HttpHeaders();
            headers.set("Location", file.getAbsolutePath());
            return new ResponseEntity(headers, CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            log.logp(Level.WARNING, "QrImageRestController", "formQrDocForRoom",
                    "Error occurred, during the file creation");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));

    //---------------------------------------------------------------//


    /**
     * log and save list to qr pictures with the given path
     */
    void saveListToQr(List<?> list, String pathToSave) {
        log.info("-------------------------Loaded " + list.size() + " :----------------------------------");
        log.info("-------" + list.toString() + "-------");


        for (Object object : list) {
            try {

                qrCreator.saveQrFromTheObject(object, pathToSave);

            } catch (IOException e) {
                e.getMessage();
                e.printStackTrace();
            }
        }
    }

}
