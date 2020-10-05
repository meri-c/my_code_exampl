package main.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lis.qr_back.dao.*;
import com.lis.qr_back.model.Equipment;
import com.lis.qr_back.model.FilterEquipment;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@RestController
@RequestMapping(value = "/api/equipment")
@Log
public class EquipmentRestController {
    private EquipmentDAO equipmentDAO;
    private UserDAO userDAO;
    private PersonalDataDAO personalDataDAO;
    private AddressDAO addressDAO;
    private RoomDAO roomDAO;
    private QrImageDAO qrImageDAO;


    public EquipmentRestController(EquipmentDAO equipmentDAO, UserDAO userDAO, AddressDAO addressDAO,
                                   RoomDAO roomDAO, QrImageDAO qrImageDAO, PersonalDataDAO personalDataDAO) {
        this.equipmentDAO = equipmentDAO;
        this.userDAO = userDAO;
        this.personalDataDAO = personalDataDAO;
        this.addressDAO = addressDAO;
        this.roomDAO = roomDAO;
        this.qrImageDAO = qrImageDAO;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")

    //---------GET ALL----------
    @GetMapping("/all")
    public ResponseEntity<List<Equipment>> getAll() {


        List<Equipment> equipments = equipmentDAO.getShortAll();

        if (equipments == null) {
            log.logp(Level.INFO, "EquipmentRestController", "getShortAll",
                    "Mistake occurred during the getShortAll() execution, list is null");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.logp(Level.INFO, "EquipmentRestController", "getShortAll",
                "Returning all the equipments");

        return new ResponseEntity<>(equipments, HttpStatus.OK);
    }

    //---------Search---------


    @GetMapping
    public ResponseEntity<List> getEquipmentsByName(@RequestParam("name") String name){

        List<Equipment> equipments = equipmentDAO.getFullByTitle(name);

        if(equipments == null || equipments.size() == 0){
            log.info("Equipment with the name "+name+ " was not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.info("Found equipment with the name "+name);

        return new ResponseEntity<>(equipments, HttpStatus.OK);
    }


 @GetMapping("/name")
    public ResponseEntity<List> getEquipmentsNames(){

        List<String> titles = equipmentDAO.getEquipmentTitles();

        if(titles == null || titles.size() == 0){
            log.info("Equipments' names ware not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.info("Found titles' names. Size: "+titles.size());

        return new ResponseEntity<>(titles, HttpStatus.OK);
    }

    //------------------------

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEquipmentById(@PathVariable("id") int id) {


       Map<String, Object> equipment = equipmentDAO.getDryById(id);

        if (equipment == null) {
            log.logp(Level.INFO, "EquipmentRestController", "getEquipmentById",
                    "Mistake occurred during the getEquipmentById() execution, equipment is null");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.logp(Level.INFO, "EquipmentRestController", "getEquipmentById",
                "Returning the equipment with id "+ id);

        return new ResponseEntity<>(equipment, HttpStatus.OK);
    }




    @GetMapping("/full")
    public ResponseEntity<List<Equipment>> getFullAll() {
        List<Equipment> equipments = equipmentDAO.getFullAll();

        if (equipments == null) {
            log.logp(Level.INFO, "EquipmentRestController", "getFullAll",
                    "Mistake occurred during the getFullAll() execution, list is null");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        log.logp(Level.INFO, "EquipmentRestController", "getFullAll",
                "Returning all the equipments");

        return new ResponseEntity<>(equipments, HttpStatus.OK);
    }

    @GetMapping("/dry")
    public ResponseEntity<List<Map<String, Object>>> getDryAll() {
        List<Map<String, Object>> equipments = equipmentDAO.getDryAll();

        if (equipments == null) {
            log.logp(Level.INFO, "EquipmentRestController", "getDryAll",
                    "Mistake occurred during the getDryAll() execution, list is null");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.logp(Level.INFO, "EquipmentRestController", "getDryAll",
                "Returning all the equipments");

        return new ResponseEntity<>(equipments, HttpStatus.OK);
    }



    //---------Get checkers-------------


    @GetMapping("/check_serial")
    public ResponseEntity checkSerialNumber(@RequestParam("serial_num") String serial_num) {
        log.info(serial_num);
        Integer serial_check_result = equipmentDAO.ifSerialExistsById(serial_num);

        if (serial_check_result == 0) {
            log.logp(Level.INFO, "EquipmentRestController", "checkSerialNumber",
                    "Checked. No such serial number");
            return new ResponseEntity<>(HttpStatus.OK);
        }
        log.logp(Level.INFO, "EquipmentRestController", "getTypes",
                "Serial number " + serial_num + " already exists");

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/check_inventory")
    public ResponseEntity checkInventoryNumber(@RequestParam("inventory_num") String inventory_num) {
        Integer inventory_check_result = equipmentDAO.ifInventoryExistsById(inventory_num);

        if (inventory_check_result == 0) {
            log.logp(Level.INFO, "EquipmentRestController", "checkSerialNumber",
                    "Checked. No such inventory number");
            return new ResponseEntity<>(HttpStatus.OK);
        }
        log.logp(Level.INFO, "EquipmentRestController", "getTypes",
                "Inventory number " + inventory_num + " already exists");

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    //---------GET SMALL REQUESTS--------

    @GetMapping("/address")
    public ResponseEntity<List> getAllAddress() {

        List<String> addresses_id = equipmentDAO.getAllAddressId();
        List<Map<String, Object>> addresses = new ArrayList<>();

        if (addresses_id != null) {

            for (String address_id : addresses_id) {
                addresses.add(addressDAO.getPlainAddressById(Integer.parseInt(address_id)));
            }
            if (addresses != null && addresses.size() > 0) {

                log.info("Success, returning all addresses for equipment: " + addresses.size());
                return new ResponseEntity<>(addresses, HttpStatus.OK);

            } else {
                log.info("No addresses with ids: " + addresses_id.toString() + " were found");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }

        log.info("No addresses ids were found");
        return new ResponseEntity<>(addresses, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/room")
    public ResponseEntity<List> getAllRoom() {
        List<String> rooms = equipmentDAO.getAllRooms();

        if(rooms ==null || rooms.size() == 0){
            log.info("No rooms were found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.info("Rooms were found! "+rooms.size());
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/type")
    public ResponseEntity<List<Map<String, Object>>> getTypes() {
        List<Map<String, Object>> types = equipmentDAO.getTypes();

        if (types == null || types.size() == 0) {
            log.logp(Level.INFO, "EquipmentRestController", "getTypes",
                    "Mistake occurred during the getTypes() execution, no types were found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.logp(Level.INFO, "EquipmentRestController", "getTypes",
                "Returning all the types: " + types.size());

        return new ResponseEntity<>(types, HttpStatus.OK);
    }



    @GetMapping("/vendor")
    public ResponseEntity<List<Map<String, Object>>> getVendors() {
        List<Map<String, Object>> vendors = equipmentDAO.getVendors();

        if (vendors == null || vendors.size() == 0) {
            log.logp(Level.INFO, "EquipmentRestController", "getVendors",
                    "Mistake occurred during the getVendors() execution, no vendors were found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.logp(Level.INFO, "EquipmentRestController", "getVendors",
                "Returning all the vendors: " + vendors.size());

        return new ResponseEntity<>(vendors, HttpStatus.OK);
    }


    @GetMapping("/inventory_num")
    public ResponseEntity<List<String>> getAllInventoryNums() {
        List<String> equipments = equipmentDAO.getInventoryNumsWithQrCodes();

        if (equipments == null || equipments.size() == 0) {
            log.logp(Level.INFO, "EquipmentRestController", "getAllInventoryNums",
                    "Mistake occurred during the getAllInventoryNums() execution, no inventories were found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.logp(Level.INFO, "EquipmentRestController", "getAllInventoryNums",
                "Returning all the equipments' inventories: " + equipments.size());

        return new ResponseEntity<>(equipments, HttpStatus.OK);
    }


 @GetMapping("/serial_num")
    public ResponseEntity<List<String>> getAllSerialNums() {
        List<String> equipments = equipmentDAO.getAllSerialNums();

        if (equipments == null || equipments.size() == 0) {
            log.logp(Level.INFO, "EquipmentRestController", "getAllSerialNums",
                    "Mistake occurred during the getAllSerialNums() execution, no inventories were found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.logp(Level.INFO, "EquipmentRestController", "getAllSerialNums",
                "Returning all the equipments' serial_nums: " + equipments.size());

        return new ResponseEntity<>(equipments, HttpStatus.OK);
    }



    @GetMapping("/main/model/{vendor_id}")
    public ResponseEntity<List<Map<String, Object>>> getModelByVendorId(@PathVariable("vendor_id") int vendor_id) {
        List<Map<String, Object>> models = equipmentDAO.getModelsByVendorId(vendor_id);

        if (models == null || models.size() == 0) {
            log.logp(Level.INFO, "EquipmentRestController", "getModelByVendorId",
                    "Mistake occurred during the getModelByVendorId() execution, no models were found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.logp(Level.INFO, "EquipmentRestController", "getModelByVendorId",
                "Returning all the models: " + models.size());

        return new ResponseEntity<>(models, HttpStatus.OK);
    }


    @GetMapping("/series/{model_id}")
    public ResponseEntity<List<Map<String, Object>>> getSeriesByModelId(@PathVariable("model_id") int model_id) {
        List<Map<String, Object>> series = equipmentDAO.getSeriesByModelId(model_id);

        if (series == null || series.size() == 0) {
            log.logp(Level.INFO, "EquipmentRestController", "getSeriesByModelId",
                    "Mistake occurred during the getSeriesByModelId() execution, no series were found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.logp(Level.INFO, "EquipmentRestController", "getSeriesByModelId",
                "Returning all the series: " + series.size());

        return new ResponseEntity<>(series, HttpStatus.OK);
    }








    //---------GET SEPARATE--------

    @GetMapping("/inventory_num/full/{num_value}")
    public ResponseEntity<Equipment> getFullByInventoryNum(@PathVariable("num_value") String num_value) {
        Equipment equipment = equipmentDAO.getFullByInventoryNumAddressString(num_value);

        if (equipment == null) {
            log.logp(Level.INFO, "EquipmentRestController", "getFullByInventoryNum",
                    "Can't get equipment with inventory number: " + num_value);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.logp(Level.INFO, "EquipmentRestController", "getFullByInventoryNum",
                "Equipment with inventory number " + num_value + "was successfully found");

        return new ResponseEntity<>(equipment, HttpStatus.OK);
    }

    @GetMapping("/room/{id}")
    public ResponseEntity<List<Equipment>> getFullByRoomId(@PathVariable("id") int id_room) {
        List<Equipment> equipments = equipmentDAO.getFullByRoomId(id_room);

        if (equipments == null) {
            log.logp(Level.INFO, "EquipmentRestController", "getByRoomId",
                    "Can't get equipments with room id " + id_room);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        log.logp(Level.INFO, "EquipmentRestController", "getByRoomId",
                "Equipment (Full ver) with room id " + id_room + " was successfully found");

        return new ResponseEntity<>(equipments, HttpStatus.OK);
    }


    //---------POST----------

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createEquipment(@RequestBody Map<String, Object> equipmentMap, UriComponentsBuilder ucBuilder) {

        Map<String, Object> check_map = equipmentMapCheck(equipmentMap, true);

        //-----check------
        if (check_map == null || check_map.get("error") != null) {
            log.logp(Level.WARNING, "EquipmentRestController", "createEquipment",
                    "Failed to create equipment. Cause: " + check_map.get("error").toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List warning_list = (List) check_map.get("warning");
        if (warning_list != null) {
            warning_list.forEach(warning ->
                    log.logp(Level.WARNING, "EquipmentRestController", "createEquipment",
                            "WARNING DURING CREATION: " + warning)
            );
        }
        //----------------

        int equipment_insert_result = equipmentDAO.insertMapEquipment(check_map);


        //-----check------
        if (equipment_insert_result == 0) {
            log.logp(Level.WARNING, "EquipmentRestController", "createEquipment",
                    "Failed to create addressString");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        //-------QR generation & saving-------

        Integer equipment_id = ((BigInteger) check_map.get("result_id")).intValue();
        String inventory_num = (String) check_map.get("inventory_num");
        insertQrImgForEquipment(equipment_id, inventory_num);

        //--------------

        log.logp(Level.INFO, "EquipmentRestController", "createEquipment",
                "Equipment with id " + equipment_id + " has successfully been created!");

        /*HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("api/qr/equipment/{equipment_id}").buildAndExpand(equipment_id).toUri());*/

        return new ResponseEntity<>(equipment_id, HttpStatus.CREATED);
    }


    @PostMapping("/filter")
    public ResponseEntity<List<Equipment>> findEquipmentByFilter(@RequestBody String json){
        log.info("data "+json);

        /*transform to real obj*/
        FilterEquipment equipment = null;
        try {
            equipment = new ObjectMapper().readValue(json, FilterEquipment.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*check if null*/
        if(equipment == null){
            log.info("Equipment filter obj is null, smth went wrong");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Equipment> filtered = equipmentDAO.getFilteredEquipment(equipment);

        if(filtered == null || filtered.size() == 0){
            log.info("Filtered result is null, nothing match your filter");

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.info("Match found: "+filtered.size()+" amount");

        return new ResponseEntity<>(filtered, HttpStatus.OK);
    }


    //-----POST SEPARATE-----


    @RequestMapping(method = RequestMethod.POST, value = "/type")
    public ResponseEntity createType(@RequestBody Map<String, Object> typeMap) {

        //-----check------
        if (typeMap == null || typeMap.get("type") == null) {
            log.logp(Level.WARNING, "EquipmentRestController", "createType",
                    "Failed to create type. Type is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //------check if exists------
        int type_exists = equipmentDAO.ifTypeExistsByName((String) typeMap.get("type"));
        if (type_exists == 1) {
            log.logp(Level.WARNING, "EquipmentRestController", "createType",
                    "Failed to create type. Duplicate value. Type with this name already exists");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        /*insert type*/
        int type_insert_result = equipmentDAO.insertType(typeMap);


        //-----check------
        if (type_insert_result == 0) {
            log.logp(Level.WARNING, "EquipmentRestController", "createType",
                    "Failed to create type");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //-------Log-------
        log.logp(Level.INFO, "EquipmentRestController", "createType",
                "Type with id " + type_insert_result + " has successfully been created!");

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/vendor")
    public ResponseEntity createVendor(@RequestBody Map<String, Object> vendorMap) {
        String vendor_string = "vendor";

        //-----check------
        if (vendorMap == null || vendorMap.get(vendor_string) == null) {
            log.logp(Level.WARNING, "EquipmentRestController", "createVendor",
                    "Failed to create vendor. Vendor is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //------check if exists------
        int vendor_exists = equipmentDAO.ifVendorExistsByName((String) vendorMap.get(vendor_string));
        if (vendor_exists == 1) {
            log.logp(Level.WARNING, "EquipmentRestController", "createVendor",
                    "Failed to create vendor. Duplicate value. Vendor with this name already exists");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        /*insert type*/
        int vendor_insert_result = equipmentDAO.insertVendor(vendorMap);


        //-----check------
        if (vendor_insert_result == 0) {
            log.logp(Level.WARNING, "EquipmentRestController", "createVendor",
                    "Failed to create vendor");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //-------Log-------
        log.logp(Level.INFO, "EquipmentRestController", "createVendor",
                "Vendor with id " + vendor_insert_result + " has successfully been created!");

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/main/model")
    public ResponseEntity createModel(@RequestBody Map<String, Object> modelMap) {

        //-----check------
        //TODO:series id questionable, may be null
        if (modelMap == null || modelMap.get("main/model") == null || modelMap.get("series") == null
                || modelMap.get("vendor_id") == null) {
            log.logp(Level.WARNING, "EquipmentRestController", "createModel",
                    "Failed to create main.model. Model or some part of it is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //------check if exists------
        int model_exists = equipmentDAO.ifModelExistsByName((String) modelMap.get("main/model"));
        int series_exists = equipmentDAO.ifSeriesExistsByName((String) modelMap.get("series"));

        if (model_exists == 1 || series_exists == 1) {
            log.logp(Level.WARNING, "EquipmentRestController", "createModel",
                    "Failed to create main.model. Duplicate value. Model or series with the given name already exists");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        /*insert type*/
        int model_insert_result = equipmentDAO.insertModel(modelMap);


        //-----check------
        if (model_insert_result == 0) {
            log.logp(Level.WARNING, "EquipmentRestController", "createModel",
                    "Failed to create main.model");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //-------Log-------
        log.logp(Level.INFO, "EquipmentRestController", "createModel",
                "Model with id " + model_insert_result + " has successfully been created!");

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //---------PUT----------

    @RequestMapping(method = RequestMethod.PUT, value = "/")
    public ResponseEntity updateEquipmentWithExistingData(@RequestBody Map<String, Object> equipmentMap) {

        //-----check------
        if (equipmentMap == null) {
            log.logp(Level.WARNING, "EquipmentRestController", "updateEquipment",
                    "Failed to update equipment. Equipment map is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        Map<String, Object> check_map = equipmentMapCheck(equipmentMap, false);

        //-----check------
        if (check_map == null || check_map.get("error") != null) {
            log.logp(Level.WARNING, "EquipmentRestController", "updateEquipment",
                    "Failed to update equipment. Cause: " + check_map.get("error").toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        log.info(check_map.keySet().toString() + "---------------");
        log.info(check_map.values().toString() + "---------------");

        List warning_list = (List) check_map.get("warning");
        if (warning_list != null) {
            warning_list.forEach(warning ->
                    log.logp(Level.WARNING, "EquipmentRestController", "updateEquipment",
                            "WARNING DURING UPDATING: " + warning)
            );
        }
        //----------------

        /*update equipment*/
        int equipment_update_result = equipmentDAO.updateEquipment(check_map);

        //-----check------
        if (equipment_update_result == 0) {
            log.logp(Level.WARNING, "EquipmentRestController", "updateEquipment",
                    "Failed to update equipment");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        Integer equipment_id = (int) check_map.get("id");

        //-------QR generation & saving-------
        Object isUpdateQr = check_map.get("isUpdateQr");
        String inventory_num = (String) check_map.get("inventory_num");

        if (isUpdateQr != null && (boolean) isUpdateQr) {

            insertQrImgForEquipment(equipment_id, inventory_num);
        }

        //-------Log-------
        log.logp(Level.INFO, "EquipmentRestController", "updateEquipment",
                "Equipment with id " + equipment_id + " has successfully been updated!");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //-----METHODS--------

    /**
     * Big ungly method
     * <p>
     * parse equipment map; check if transferred data is correct and exists in db;
     * depends on create or an update returns modified, correct map for insert
     */
    private Map<String, Object> equipmentMapCheck(Map<String, Object> equipmentMap, boolean isCreate) {

        if (equipmentMap == null) {
            equipmentMap.put("error", "The map is null");
            return equipmentMap;
        }

        /*is there any update for qr_pic*/
        boolean isUpdateQr = false;
        Map<String, Object> old_equipment = new HashMap<>();
        equipmentMap.put("result_id", null);
        Object id = equipmentMap.get("id");
        Object id_type = equipmentMap.get("id_type");
        Object id_model_series = equipmentMap.get("id_model_series");
        Object serial_num = equipmentMap.get("serial_num");
        Object inventory_num = equipmentMap.get("inventory_num");
        Object room = equipmentMap.get("room");
        Object id_asDetailIn = equipmentMap.get("id_asDetailIn");
        Object id_tp = equipmentMap.get("id_tp");
        Object id_pd = equipmentMap.get("id_pd");
        Object attributes = equipmentMap.get("attributes");
        String error_msg;
        List<String> warning_list = new ArrayList<>();

        //-------Checkers-------

        //TODO:add parts creation on the fly

        //---------------------------

        /*id
        *
        * Set id null on creation and,
        * if found, put in map on update
        * else error;
        *
        * */


        if (id != null) {

            if (isCreate) {
                equipmentMap.put("id", null);
            } else {
                old_equipment = equipmentDAO.getDryById((int) id);

                if (old_equipment == null) {
                    error_msg = "Can't find the equipment with id " + id;
                    equipmentMap.put("error", error_msg);
                    return equipmentMap;
                }
            }
        } else if (!isCreate) {
            error_msg = "Id can't be null ";
            equipmentMap.put("error", error_msg);
            return equipmentMap;
        }

        //-------------don't save: id_type, id_model, serial_num, inventory_num---------------

        /*
        * On Update: null -> set null
        * On Create: null -> error
        *
        * On Update: not found -> error
        * On Create: not found -> error
        *
        * On Update: found -> set to old_equipment
        * On Create: found -> do nothing
        *
        * */


        /*id_type*/
        {
            if (id_type != null) {

                int check_result = equipmentDAO.ifTypeExistsById((Integer) id_type);

                if (check_result == 0) {
                    error_msg = "Can't find the type with id " + id_type;
                    equipmentMap.put("error", error_msg);
                    return equipmentMap;
                } else if (!isCreate) {
                    boolean old_new_Id_tp_equals = old_equipment.get("id_type").equals(id_type);
                    if (!old_new_Id_tp_equals) {
                        old_equipment.put("id_type", id_type);
                        isUpdateQr = true;
                    }
                }
            } else if (isCreate) {
                error_msg = "The type can't be null";
                equipmentMap.put("error", error_msg);
                return equipmentMap;

            } else if (!isCreate) {
                old_equipment.put("id_type", null);
                isUpdateQr = true;
            }
        }

        /*id_model*/
        if (id_model_series != null) {
            int check_result = equipmentDAO.ifModelSeriesExistsById((Integer) id_model_series);

            if (check_result == 0) {
                error_msg = "Can't find the main.model with id " + id_model_series;
                equipmentMap.put("error", error_msg);
                return equipmentMap;
            } else if (!isCreate) {
                boolean old_new_Id_model_equals = old_equipment.get("id_model_series").equals(id_model_series);
                if (!old_new_Id_model_equals) {
                    old_equipment.put("id_model_series", id_model_series);
                    isUpdateQr = true;
                }
            }
        } else if (isCreate) {
            error_msg = "The main.model can't be null";
            equipmentMap.put("error", error_msg);
            return equipmentMap;

        } else if (!isCreate) {
            old_equipment.put("id_model_series", null);
            isUpdateQr = true;
        }


        //-------------don't save: serial_num, inventory_num---------------

        /*
        * On Update: null -> set null
        * On Create: null -> error
        *
        * On Update: not found -> error
        * On Create: not found -> error
        *
        * On Update: found -> if match old value -> Ok, if just duplicate -> error
        * On Create: found -> error, duplicate value
        *
        * */

        /*serial_num*/
        if (serial_num != null) {
            boolean old_new_seraials_equals = false;
            if (!isCreate) {
                old_new_seraials_equals = old_equipment.get("serial_num").equals(serial_num);
            }

            int check_result = equipmentDAO.ifSerialExistsById((String) serial_num);

            if (check_result == 1) {
                if (!isCreate && old_new_seraials_equals) {

                } else {
                    error_msg = "Equipment with serial number " + serial_num + " already exists";
                    equipmentMap.put("error", error_msg);
                    return equipmentMap;
                }
            } else if (!isCreate) {
                old_equipment.put("serial_num", serial_num);
            }
        } else {
            error_msg = "Equipment serial number can't be null";
            equipmentMap.put("error", error_msg);
            return equipmentMap;
        }

        /*inventory_num*/
        if (inventory_num != null) {
            boolean old_new_inventory_equals = false;
            if (!isCreate) {
                old_new_inventory_equals = old_equipment.get("inventory_num").equals(inventory_num);
            }

            int check_result = equipmentDAO.ifInventoryExistsById((String) inventory_num);

            if (check_result == 1) {
                if (!isCreate && old_new_inventory_equals) {

                } else {
                    error_msg = "Equipment with inventory number " + inventory_num + " already exists";
                    equipmentMap.put("error", error_msg);
                    return equipmentMap;
                }
            } else if (!isCreate) {
                old_equipment.put("inventory_num", inventory_num);
                isUpdateQr = true;
            }
        } else {
            error_msg = "Equipment serial number can't be null";
            equipmentMap.put("error", error_msg);
            return equipmentMap;
        }


        //---------attributes, id_user, id_tp, room, id_adDetailIn-------------

        /*
        *
        * On Update: null -> set null/ else json->string, replace in array
        * On Create: null -> set null/ else json->string, replace in array
        *
        * On Update: null -> set null
        * On Create: null -> set null
        *
        * On Update: not found -> do nothing
        * On Create: not found -> warning
        *
        * On Update: found -> set to old_equipment
        * On Create: found -> do nothing
        *
        * */


        /*attributes*/
        if (attributes == null) {
            if (!isCreate) {
                old_equipment.put("attributes", null);
            } else {
                equipmentMap.put("attributes", null);
                warning_list.add("Attributes are empty\n");
            }
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            String string_attributes = null;

            try {
                string_attributes = objectMapper.writeValueAsString(attributes);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                warning_list.add("Failed parse attributes. Cause: " + e.getMessage() + "\n");
            }

            // string_attributes = attributes.toString();

            if (isCreate) {
                equipmentMap.put("attributes", string_attributes);
            } else {
                old_equipment.put("attributes", string_attributes);
            }
        }


        /*id_tp*/
        if (id_tp != null) {
            int check_result = addressDAO.ifTpExistsById((Integer) id_tp);

            if (check_result == 0) {
                equipmentMap.put("id_tp", null);
                warning_list.add("Technical platform with id " + id_tp + " is not found. Technical platform wasn't set!\n");

            } else if (!isCreate) {

                old_equipment.put("id_tp", id_tp);
            }
        } else if (!isCreate) {
            old_equipment.put("id_tp", null);
        }

        /*id_pd*/
        if (id_pd != null) {
            int check_result = personalDataDAO.ifPersonalDataExists((Integer) id_pd);

            if (check_result == 0) {
                equipmentMap.put("id_pd", null);
                warning_list.add("Personal data with id " + id_pd + " is not found. Personal data wasn't set!\n");

            } else if (!isCreate) {

                old_equipment.put("id_pd", id_pd);
            }
        } else if (!isCreate) {
            old_equipment.put("id_pd", null);
        }

        /*room*/
        if (room != null) {
            int check_result = roomDAO.ifExistsById((Integer) room);

            if (check_result == 0) {
                equipmentMap.put("room", null);
                warning_list.add("Room with number " + room + " is not found. Room wasn't set!\n");
            } else if (!isCreate) {
                old_equipment.put("room", room);
            }
        } else if (!isCreate) {
            old_equipment.put("room", null);
        }

        /*id_asDetailIn*/
        if (id_asDetailIn != null) {
            int check_result = equipmentDAO.ifExistsById((Integer) id_asDetailIn);

            if (check_result == 0) {
                equipmentMap.put("id_asDetailIn", null);
                warning_list.add("Id of an element where the equipment as detail, with number " + id_asDetailIn + " " +
                        "is not found. id_adDetailIn wasn't set!\n");
            } else if (!isCreate) {
                old_equipment.put("id_asDetailIn", id_asDetailIn);
            }
        } else if (!isCreate) {
            old_equipment.put("id_asDetailIn", null);
        }


        equipmentMap.put("warning", warning_list);
        old_equipment.put("warning", warning_list);

        if (isCreate) {
            return equipmentMap;
        } else {
            old_equipment.put("isUpdateQr", isUpdateQr);
            return old_equipment;
        }
    }





    private void insertQrImgForEquipment(int equipment_id, String inventory_num) {
        Map<String, Object> equipment = equipmentDAO.getShortById(equipment_id);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String temp_json = objectMapper.writeValueAsString(equipment);

            Integer qrImage_insert_id = qrImageDAO.insertFromStringWithId(temp_json, inventory_num, equipment_id);
//-----log-----
            if (qrImage_insert_id == 0) {
                log.logp(Level.WARNING, "EquipmentRestController", "createEquipment",
                        "ERROR: Failed to create QR code from the equipment with the id " + equipment_id);
            } else {
                log.logp(Level.WARNING, "EquipmentRestController", "createEquipment",
                        "QR pic and id were successfully added for the equipment " + equipment_id);
            }
//------------
        } catch (JsonProcessingException e) {
            e.getMessage();
            e.printStackTrace();
            log.logp(Level.WARNING, "EquipmentRestController", "createEquipment",
                    "ERROR during creation a QR code");
        }
    }
}
