package main.rest;

import com.lis.qr_back.dao.InventoryDAO;
import com.lis.qr_back.model.Inventory;
import com.lis.qr_back.property.FileUploadProperties;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

@RestController
@RequestMapping(value = "/inventory")
@Log
public class InventoryRestController {

    private InventoryDAO inventoryDAO;
    private final String mysql_secure_folder;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    public InventoryRestController(FileUploadProperties fileUploadProperties, InventoryDAO inventoryDAO) {
        this.mysql_secure_folder = fileUploadProperties.getMysqlSecureDir();
        this.inventoryDAO = inventoryDAO;
    }

    //---------GET ALL----------
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Inventory>> getAll() {
        List<Inventory> inventories = inventoryDAO.getAll();

        if (inventories == null) {
            log.logp(Level.INFO, "InventoryRestController", "getAll",
                    "Mistake occurred during the getAll() execution, list is null");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        log.logp(Level.INFO, "InventoryRestController", "getAll",
                "Returning all the inventory list");

        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }


    //-----------GET_BY_PARAM------------
    @RequestMapping(method = RequestMethod.GET, value = "/room/{id}")
    public ResponseEntity<List<Inventory>> getByRoom(@PathVariable("id") int id_room) {
        List<Inventory> inventories = inventoryDAO.getByRoom(id_room);

        if (inventories == null) {
            log.logp(Level.INFO, "InventoryRestController", "getByRoom",
                    "Can't get equipments with room id " + id_room);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        log.logp(Level.INFO, "InventoryRestController", "getByRoom",
                "Inventory object(s) with room id " + id_room + " was successfully found");

        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/address/{id}")
    public ResponseEntity<List<Inventory>> getByAddressId(@PathVariable("id") int address_id) {
        List<Inventory> inventories = inventoryDAO.getByAddressId(address_id);

        if (inventories == null) {
            log.logp(Level.INFO, "InventoryRestController", "getByAddressId",
                    "Can't get equipments with address id " + address_id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        log.logp(Level.INFO, "InventoryRestController", "getByAddressId",
                "Inventory object(s) with address id " + address_id + " was successfully found");

        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }


    //----------POST----------------

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addInventory() {
        //get data from request

        //---check addressString
        Integer tp_id;

        //---check room
        //---check inventory_num

        //insert

        //insert_check


        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/load_csv")
    public ResponseEntity loadCsvToFinishInventory() {

        File mysql_dir = new File(mysql_secure_folder);

        /*check if mysql secure dir exists*/
        if (mysql_dir.exists() && mysql_dir.isDirectory()) {

            /*check csv files in there*/
            File[] files_in_directory = mysql_dir.listFiles(file -> {
                log.info("Saving filename :" + file.getName());
                return file.getName().endsWith(".csv");
            });

            /*load all files to the db*/
            if (files_in_directory != null && files_in_directory.length != 0) {

                for (File file : files_in_directory) {

                    String filename = file.getName();

                    /*find file in the datastore, if exists - load to db*/
                    int i = inventoryDAO.loadCsvToFinishInventory(mysql_secure_folder + filename);

                    log.info("Inserted from " + filename + " : " + i);

                }
                return new ResponseEntity(HttpStatus.OK);
            }
            log.info("Directory is empty!");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        log.info("Directory does not exist!");
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/save_csv_to_folder")
    public ResponseEntity uploadCsv(@RequestParam("files") MultipartFile[] multipartFiles) {

        if (multipartFiles != null && multipartFiles.length != 0) {
            log.info("Get multipart files: "+ multipartFiles.length);

            File destination_file;
            for (MultipartFile multipartFile : multipartFiles) {

                String fileName = multipartFile.getOriginalFilename();
                log.info("Uploading filename: " + fileName);

                try {
                    destination_file = new File(mysql_secure_folder + fileName);

                    multipartFile.transferTo(destination_file);

                    log.logp(Level.WARNING, "FileRestController", "uploadFile",
                            "File was successfully uploaded: " + destination_file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*load saved files' data to database and return the result*/
            ResponseEntity response = loadCsvToFinishInventory();

            if (response.getStatusCode() == HttpStatus.OK) {

            /*if data was saved, delete files in mysql directory*/
                File file_to_delete;
                for (MultipartFile multipartFile : multipartFiles) {

                    file_to_delete = new File(mysql_secure_folder + multipartFile.getOriginalFilename());
                    file_to_delete.delete();

                }
                return response;
            }else{
                log.info("Loading csv files are failed");
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        } else {
            log.info("Transferred file(s) is null");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
