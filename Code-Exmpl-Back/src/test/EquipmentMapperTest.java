package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lis.qr_back.dao.EquipmentDAO;
import com.lis.qr_back.model.Equipment;
import com.lis.qr_back.model.FilterEquipment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EquipmentMapperTest {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private EquipmentMapper equipmentMapper;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private EquipmentDAO equipmentDAO;


    @Test
    public void getFullByInventoryNumAddressString() throws Exception {
        String inventory_num = "102460145";

        Equipment equipment = equipmentDAO.getFullByInventoryNumAddressString(inventory_num);
        assertNotNull(equipment);
        // assertEquals(String.class, equipment.get("address").getClass());
        System.out.println(equipment.toString());
    }

    @Test
    public void getFullByInventoryNum() throws Exception {

        String inventory_num = "102460145";
        //Map<String, Object> equipment = equipmentDAO.getFullByInventoryNumAddressString(inventory_num);
        Equipment equipment = equipmentDAO.getFullByInventoryNumAddressString(inventory_num);
        assertNotNull(equipment);
        // assertEquals(String.class, equipment.get("address").getClass());
        System.out.println(equipment.toString());

    }


    @Test
    public void getShortById() throws Exception {
        Map<String, Object> equipment = equipmentDAO.getShortById(2);
        System.out.println(equipment.toString());

    }

    @Test
    public void getAll() throws Exception {
        List<Equipment> equipments = equipmentDAO.getShortAll();

        assertNotNull(equipments);
        equipments.forEach(System.out::println);

    }

    @Test
    public void getFullAll() throws Exception {
        List<Equipment> equipments = equipmentDAO.getFullAll();

        assertNotNull(equipments);
        equipments.forEach(System.out::println);


    }

    @Test
    public void getByInventory() throws Exception {
        String inventory_num = new String("123123123");

        Equipment equipment = equipmentDAO.getShortByInventoryNum(inventory_num);

        assertNotNull(equipment);
        System.out.println(equipment);
        assertEquals(inventory_num, equipment.getInventory_num());
    }

    @Test
    public void getByRoomId() throws Exception {
        int id_room = 33;

        List<Equipment> equipments = equipmentDAO.getShortByRoomId(id_room);
        List<Map<String, Object>> equipmentMapList = new ArrayList<>();

        assertNotNull(equipments);
        for (Equipment equipment : equipments) {
            equipmentMapList.add(new ObjectMapper().convertValue(equipment, Map.class));
        }


        for (Map<String, Object> equipment : equipmentMapList) {
            System.out.println("-------------");
            for (Map.Entry<String, Object> map : equipment.entrySet()) {
                System.out.println(map.getKey() + " " + map.getValue());
            }
        }

        //equipments.forEach(System.out::println);
        //equipments.forEach(equipment -> assertSame(id_room, equipment.getRoom()));


    }

    @Test
    public void getDryById() throws Exception {
        int id = 2;

        Map<String, Object> equipment = equipmentDAO.getDryById(id);

        assertNotNull(equipment);
        System.out.println(equipment.toString());
        assertSame(id, equipment.get("id"));
    }

    @Test
    public void insertMapEquipment() throws Exception {
        Map<String, Object> equipment = new HashMap<>();
        equipment.put("result_id", null);
        equipment.put("id_type", 1);
        equipment.put("id_model_series", 3);
        equipment.put("serial_num", 1);
        equipment.put("inventory_num", 1);
        equipment.put("room", 33);
        equipment.put("id_asDetailIn", 2);
        equipment.put("id_tp", 14);
        equipment.put("id_user", 89);

        int insertResult = equipmentDAO.insertMapEquipment(equipment);

        System.out.println(equipment.get("result_id"));
        assertEquals(1, insertResult);
        assertNotEquals(0, equipment.get("result_id"));

    }

    //@Rollback(false)
    @Test
    public void insertType() throws Exception {
        HashMap<String, Object> type_map = new HashMap<>();
        type_map.put("result_id", 0);
        type_map.put("type", "New Type");

        int daoRes = equipmentDAO.insertType(type_map);
        int type_id = (int) type_map.get("result_id");

        System.out.println(type_id);
        System.out.println(daoRes);
        assertTrue(type_id > 0);
        assertEquals(1, daoRes);
    }

    @Test
    public void insertVendor() throws Exception {
        HashMap<String, Object> vendor_map = new HashMap<>();
        vendor_map.put("result_id", 0);
        vendor_map.put("vendor", "New Vendor");

        int daoRes = equipmentDAO.insertVendor(vendor_map);
        int vendor_id = (int) vendor_map.get("result_id");

        System.out.println(vendor_id);
        System.out.println(daoRes);
        assertTrue(vendor_id > 0);
        assertEquals(1, daoRes);
    }

    @Test
    public void insertModel() throws Exception {
        int vendor_id = 1;
        HashMap<String, Object> model_map = new HashMap<>();
        model_map.put("result_id", 0);
        model_map.put("model", "New Model");
        model_map.put("series", "New series");
        model_map.put("vendor_id", vendor_id);

        int daoRes = equipmentDAO.insertModel(model_map);
        int model_id = (int) model_map.get("result_id");

        System.out.println(model_id);
        System.out.println(daoRes);
        assertTrue(model_id > 0);
        assertEquals(1, daoRes);
    }

    @Test
    public void justTest() {
        Map<String, Object> old_equipment = equipmentDAO.getDryById(2);
        Object serial_num = old_equipment.get("serial_num");
        boolean old_new_seraials_equals = old_equipment.get("serial_num").equals(serial_num);

        System.out.println(old_new_seraials_equals);
        System.out.println(old_equipment);

    }

    @Test
    public void getFilteredEquipment() throws IOException {
        //empty array test
        /*FilterEquipment equipment = new FilterEquipment(new String[]{"3"}, new String[]{}, new String[]{},
                new String[]{}, new String[]{}, new String[]{});

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(equipment);
        System.out.println(json);*/

        //null array test
        String json = "{\"address\":[\"3\"]}";

        FilterEquipment equipment = new ObjectMapper().readValue(json, FilterEquipment.class);

        String json2 = new ObjectMapper().writeValueAsString(equipment);

        System.out.println(json2);

        List<Equipment> result = equipmentDAO.getFilteredEquipment(equipment);
        assertNotNull(result);
        result.forEach(System.out::println);
    }
}
