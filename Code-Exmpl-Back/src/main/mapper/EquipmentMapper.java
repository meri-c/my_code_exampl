package main.mapper;


import com.lis.qr_back.additional.type_handler.InfoNameTypeHandler;
import com.lis.qr_back.additional.type_handler.JsonTypeHandler;
import com.lis.qr_back.model.Address;
import com.lis.qr_back.model.Equipment;
import com.lis.qr_back.model.FilterEquipment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper

public interface EquipmentMapper {
    String ADDRESS_MAPPER = "com.lis.qr_back.main.mapper.AddressMapper";

    String SELECT_IF_EXISTS_ID = "select count(id) from ";

    String SELECT_DRY = "select * from equipment ";


    String FULL_SELECT = "SELECT E.id, E.title, T.type, V.vendor, M.main.model, MS.series, E.serial_num, E.inventory_num,\n" +
            " E.attributes, E.room, E.id_asDetailIn, E.id_tp, E.id_pd, PD.surname, PD.name, PD.patronymic from equipment as E\n" +
            " LEFT JOIN type as T on E.id_type = T.id\n" +
            " left JOIN model_series as MS on E.id_model_series = MS.id\n" +
            " left JOIN main.model as M on MS.model_id = M.id\n" +
            " LEFT JOIN  vendor as V on M.vendor_id = V.id\n" +
            " LEFT JOIN personal_data as PD on E.id_pd = PD.id ";

    String SHORT_SELECT ="SELECT T.type, concat(V.vendor, \" \" , M.main.model, \" \" , MS.series) as name, E.inventory_num\n" +
            "            from equipment as E  LEFT join type as T on E.id_type = T.id\n" +
            "                                 left JOIN model_series as MS on E.id_model_series = MS.id\n" +
            "                                 left JOIN main.model as M on MS.model_id = M.id\n" +
            "                                 LEFT JOIN  vendor as V on M.vendor_id = V.id ";

    //-----Checkers-------
    @Select(SELECT_IF_EXISTS_ID+"equipment where id=#{id};")
    int ifExistsById(int id);

    @Select(SELECT_IF_EXISTS_ID+"type where id=#{id};")
    int ifTypeExistsById(int id);

    @Select(SELECT_IF_EXISTS_ID+"model_series where id=#{id};")
    int ifModelSeriesExistsById(int id);

    @Select(SELECT_IF_EXISTS_ID+"equipment where serial_num=#{serial_num};")
    Integer ifSerialExistsById(String serial_num);

    @Select(SELECT_IF_EXISTS_ID+"equipment where inventory_num=#{inventory_num};")
    Integer ifInventoryExistsById(String inventory_num);



    //------Checkers By Value--------

    @Select(SELECT_IF_EXISTS_ID+"type where type=#{type};")
    int ifTypeExistsByName(String type);

    @Select(SELECT_IF_EXISTS_ID+"main.model where main.model=#{main.model};")
    int ifModelExistsByName(String model);

    @Select(SELECT_IF_EXISTS_ID+"model_series where series=#{series};")
    int ifSeriesExistsByName(String series);

    @Select(SELECT_IF_EXISTS_ID+"vendor where vendor=#{vendor};")
    int ifVendorExistsByName(String vendor);



    //----------All-----------

    @Select(FULL_SELECT)
    @Results({
            @Result(property = "attributes", column = "attributes", javaType = String.class,
                    typeHandler = JsonTypeHandler.class),
            @Result(property = "user_info", column = "surname", javaType = String.class,
                    typeHandler = InfoNameTypeHandler.class),
            @Result(property = "address", column = "id_tp", javaType = Address.class,
                    one = @One(select = ADDRESS_MAPPER+".getFullByTechPlatformId"))
    })
    List<Equipment> getFullAll();

    @Select(SELECT_DRY)
    @Results({
            @Result(property = "attributes", column = "attributes", javaType = String.class,
                    typeHandler = JsonTypeHandler.class)
    })
    List<Map<String, Object>> getDryAll();

    @Select(SHORT_SELECT + ";")
    List<Equipment> getShortAll();


    @Select("select inventory_num from equipment")
    List<String> getAllInventoryNums();

    @Select("select inventory_num from qr_project.equipment " +
            "join qr_project.qr_image qi on equipment.id = qi.equipment_id;\n")
    List<String> getInventoryNumsWithQrCodes();



    @Select("select serial_num from equipment")
    List<String> getAllSerialNums();

    @Select("select distinct technical_platform.id_address from equipment," +
            " technical_platform where technical_platform.id = equipment.id_tp;")
    List<String> getAllAddressId();

    @Select("select distinct room from equipment where room is not null")
    List<String> getAllRooms();


    //----------Small separate requests-------------
    @Select("select * from type;")
    List<Map<String, Object>> getTypes();

    @Select("select * from vendor;")
    List<Map<String, Object>> getVendors();

    @Select("select id, main.model from main.model where vendor_id=#{vendor_id};")
    List<Map<String, Object>> getModelsByVendorId(int vendor_id);

    @Select("select id, series from model_series where model_id=#{model_id};")
    List<Map<String, Object>> getSeriesByModelId(int model_id);

    @Select("select distinct equipment.title from equipment where equipment.title is not null")
    List<String> getEquipmentTitles();

    //-----------By value------------
    @Select(SHORT_SELECT + "where E.id=#{id};")
    Map<String, Object> getShortById(int id);


    @Select(SELECT_DRY + "where id=#{id};")
    Map<String, Object> getDryById(int id);


    @Select(SHORT_SELECT + "where E.inventory_num=#{inventory_num};")
    Equipment getShortByInventoryNum(String inventory_num);


    @Select(SHORT_SELECT + "where E.room=#{room};")
    List<Equipment> getShortByRoomId(int room);


    @Select(FULL_SELECT + "where E.inventory_num=#{inventory_num};")
    @Results({
            @Result(property = "attributes", column = "attributes", javaType = String.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "user_info", column = "surname", javaType = String.class, typeHandler = InfoNameTypeHandler.class),
            @Result(property = "address", column = "id_tp", javaType = Address.class,
                    one = @One(select = ADDRESS_MAPPER+".getFullByTechPlatformId")),
    })
    Map<String, Object> getFullByInventoryNum(String inventory_num);

    @Select(FULL_SELECT + "where E.title=#{title};")
    @Results({
            @Result(property = "attributes", column = "attributes", javaType = String.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "user_info", column = "surname", javaType = String.class, typeHandler = InfoNameTypeHandler.class),
            @Result(property = "address", column = "id_tp", javaType = Address.class,
                    one = @One(select = ADDRESS_MAPPER+".getFullByTechPlatformId")),
    })
    List<Equipment> getFullByTitle(String title);



    @Select(FULL_SELECT + "where E.room=#{room};")
    @Results({
            @Result(property = "attributes", column = "attributes", javaType = String.class,
                    typeHandler = JsonTypeHandler.class),
            @Result(property = "user_info", column = "surname", javaType = String.class,
                    typeHandler = InfoNameTypeHandler.class)
    })
    List<Equipment> getFullByRoomId(int room);



    @Select(FULL_SELECT + "where E.inventory_num=#{inventory_num};")
    @Results({
            @Result(property = "attributes", column = "attributes", javaType = String.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "user_info", column = "surname", javaType = String.class, typeHandler = InfoNameTypeHandler.class),
            @Result(property = "address", column = "id_tp", javaType = Address.class,
                    one = @One(select = ADDRESS_MAPPER+".getFullByTechPlatformId")),
    })
    Equipment getFullByInventoryNumAddressString(String inventory_num);


    @SelectProvider(type=com.lis.qr_back.additional.SqlBuilder.class, method="equipmentFilter")
    @Results({
            @Result(property = "attributes", column = "attributes", javaType = String.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "user_info", column = "surname", javaType = String.class, typeHandler = InfoNameTypeHandler.class),
            @Result(property = "address", column = "id_tp", javaType = Address.class,
                    one = @One(select = ADDRESS_MAPPER+".getFullByTechPlatformId")),
    })
    List<Equipment> getFilteredEquipment(FilterEquipment params);

    //-------insert--------

    @Insert("INSERT INTO equipment (id_type, id_model_series, serial_num,\n" +
            "inventory_num, attributes, room, id_asDetailIn, id_pd, id_tp) VALUES (#{id_type}, #{id_model_series}, " +
            "#{serial_num}, #{inventory_num}, #{attributes}, #{room}, #{id_asDetailIn}, " +
            "#{id_pd}, #{id_tp});")
    //@SelectKey(statement = "select last_insert_id()",keyColumn = "id", keyProperty = "id", before = false, resultType = int.class)
    @Options(useGeneratedKeys = true, keyProperty = "result_id", keyColumn = "id")
    int insertMapEquipment(Map<String, Object> equipment);


    //-------Insert separate-----

    @Insert("insert into type(type) values (#{type})")
    @Options(useGeneratedKeys = true, keyProperty = "result_id", keyColumn = "id")
    int insertType(Map<String, Object> type);

    @Insert("insert into vendor(vendor) values (#{vendor})")
    @Options(useGeneratedKeys = true, keyProperty = "result_id", keyColumn = "id")
    int insertVendor(Map<String, Object> vendor);

    @Insert("insert into main.model(series, main.model, vendor_id) values (#{series}, #{main.model}, #{vendor_id})")
    @Options(useGeneratedKeys = true, keyProperty = "result_id", keyColumn = "id")
    int insertModel(Map<String, Object> model);
    //----------Update---------

    @Update("update equipment set id_type=#{id_type}, id_model=#{id_model}, serial_num=#{serial_num}, " +
            "inventory_num=#{inventory_num},room=#{room}, id_asDetailIn=#{id_asDetailIn}, id_tp=#{id_tp}, " +
            "id_user=#{id_user}attributes = #{attributes} WHERE id=#{id};")
    int updateEquipment(Map<String, Object> equipment_map);

}
