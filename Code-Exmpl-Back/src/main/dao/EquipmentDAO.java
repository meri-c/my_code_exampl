package main.dao;

import com.lis.qr_back.mapper.EquipmentMapper;
import com.lis.qr_back.model.AddressSingleTechPlatform;
import com.lis.qr_back.model.Equipment;
import com.lis.qr_back.model.FilterEquipment;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class EquipmentDAO implements EquipmentMapper {
    private SqlSession sqlSession;
    private static final String EQUIPMENT_MAPPER = "com.lis.qr_back.main.mapper.EquipmentMapper";

    public EquipmentDAO(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    //---------Checkers----------
    @Override
    public int ifExistsById(int id) {
        return sqlSession.selectOne(EQUIPMENT_MAPPER + ".ifExistsById", id);
    }

    @Override
    public int ifTypeExistsById(int id) {
        return sqlSession.selectOne(EQUIPMENT_MAPPER + ".ifTypeExistsById", id);
    }

    @Override
    public int ifModelSeriesExistsById(int id) {
        return sqlSession.selectOne(EQUIPMENT_MAPPER + ".ifModelSeriesExistsById", id);
    }

    @Override
    public Integer ifSerialExistsById(String serial_num) {
        return sqlSession.selectOne(EQUIPMENT_MAPPER + ".ifSerialExistsById", serial_num);
    }

    @Override
    public Integer ifInventoryExistsById(String inventory_num) {
        return sqlSession.selectOne(EQUIPMENT_MAPPER + ".ifInventoryExistsById", inventory_num);
    }



    //---------Checkers by value----------
    @Override
    public int ifTypeExistsByName(String type) {
        return sqlSession.selectOne(EQUIPMENT_MAPPER + ".ifTypeExistsByName", type);
    }

    @Override
    public int ifVendorExistsByName(String vendor) {
        return sqlSession.selectOne(EQUIPMENT_MAPPER + ".ifVendorExistsByName", vendor);
    }

    @Override
    public int ifModelExistsByName(String model) {
        return sqlSession.selectOne(EQUIPMENT_MAPPER + ".ifModelExistsByName", model);
    }

    @Override
    public int ifSeriesExistsByName(String series) {
        return sqlSession.selectOne(EQUIPMENT_MAPPER + ".ifSeriesExistsByName", series);
    }


    //-----------All------------

    @Override
    public List<Map<String, Object>> getDryAll() {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getDryAll");
    }

    @Override
    public List<Equipment> getFullAll() {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getFullAll");
    }

    @Override
    public List<Equipment> getShortAll() {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getShortAll");
    }

    @Override
    public List<String> getAllAddressId() {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getAllAddressId");
    }

    @Override
    public List<String> getAllSerialNums() {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getAllSerialNums");
    }

    @Override
    public List<String> getAllRooms() {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getAllRooms");
    }

    @Override
    public List<String> getAllInventoryNums() {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getAllInventoryNums");
    }

    @Override
    public List<String> getInventoryNumsWithQrCodes() {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getInventoryNumsWithQrCodes");
    }

    //---------Small separate requests-----------

    @Override
    public List<Map<String, Object>> getTypes() {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getTypes");
    }

    @Override
    public List<Map<String, Object>> getVendors() {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getVendors");
    }

    @Override
    public List<Map<String, Object>> getModelsByVendorId(int vendor_id) {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getModelsByVendorId", vendor_id);
    }

    @Override
    public List<Map<String, Object>> getSeriesByModelId(int model_id) {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getSeriesByModelId", model_id);
    }

    @Override
    public List<String> getEquipmentTitles() {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getEquipmentTitles");
    }

    // --By value-----------


    @Override
    public List<Equipment> getFilteredEquipment(FilterEquipment params) {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getFilteredEquipment", params);
    }

    @Override
    public Map<String, Object> getShortById(int id) {
        return sqlSession.selectOne(EQUIPMENT_MAPPER + ".getShortById", id);
    }

    @Override
    public Equipment getShortByInventoryNum(String inventory_num) {
        return sqlSession.selectOne(EQUIPMENT_MAPPER + ".getShortByInventoryNum", inventory_num);

    }

    @Override
    public List<Equipment> getShortByRoomId(int room) {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getShortByRoomId", room);

    }

    @Override
    public List<Equipment> getFullByTitle(String title) {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getFullByTitle", title);
    }

    @Override
    public Map<String, Object> getFullByInventoryNum(String inventory_num) {
        Map<String, Object> equipment = sqlSession.selectOne(EQUIPMENT_MAPPER + ".getFullByInventoryNum", inventory_num);
        AddressSingleTechPlatform address = (AddressSingleTechPlatform) equipment.get("address");
        StringBuilder sb = new StringBuilder();

        sb.append(address.getCity())
                .append(" ")
                .append(address.getStreet())
                .append(" №")
                .append(address.getNumber())
                .append("/")
                .append(address.getRoom())
                .append(" ");

        equipment.replace("address", sb.toString());

        return equipment;
    }

    @Override
    public Equipment getFullByInventoryNumAddressString(String inventory_num) {
        Equipment equipment = sqlSession.selectOne(EQUIPMENT_MAPPER + ".getFullByInventoryNumAddressString", inventory_num);

        if (equipment == null) {
            return null;
        }
        AddressSingleTechPlatform address = (AddressSingleTechPlatform) equipment.getAddress();
        StringBuilder sb = new StringBuilder();

        sb.append(address.getCity())
                .append(" ")
                .append(address.getStreet())
                .append(" №")
                .append(address.getNumber())
                .append("/")
                .append(address.getRoom())
                .append(" ");

        equipment.setAddress(sb.toString());
        equipment.setId_tp(address.getId());

        return equipment;
    }

    @Override
    public List<Equipment> getFullByRoomId(int room) {
        return sqlSession.selectList(EQUIPMENT_MAPPER + ".getFullByRoomId", room);
    }

    @Override
    public Map<String, Object> getDryById(int id) {
        return sqlSession.selectOne(EQUIPMENT_MAPPER + ".getDryById", id);
    }


    //------Insert-------

    @Override
    public int insertMapEquipment(Map<String, Object> equipment) {
        return sqlSession.insert(EQUIPMENT_MAPPER + ".insertMapEquipment", equipment);

    }

    @Override
    public int updateEquipment(Map<String, Object> equipment_map) {
        return sqlSession.insert(EQUIPMENT_MAPPER + ".updateEquipment", equipment_map);
    }

    //-----Insert separate---


    @Override
    public int insertType(Map<String, Object> type) {
        return sqlSession.insert(EQUIPMENT_MAPPER + ".insertType", type);
    }

    @Override
    public int insertVendor(Map<String, Object> vendor) {
        return sqlSession.insert(EQUIPMENT_MAPPER + ".insertVendor", vendor);
    }

    @Override
    public int insertModel(Map<String, Object> model) {
        return sqlSession.insert(EQUIPMENT_MAPPER + ".insertModel", model);
    }

    public void attributeToJson(Equipment equipment) {

    }
}
