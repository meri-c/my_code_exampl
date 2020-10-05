package main.mapper;

import com.lis.qr_back.additional.SqlProvider;
import com.lis.qr_back.model.Inventory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InventoryMapper {
String SELECT ="select * from inventory";
String SELECT_SHORT = "select name, inventory_num from inventory";


    @Select(SELECT)
    List<Inventory> getAll();

    @Select(SELECT +" WHERE room=#{room}")
    List<Inventory> getByRoom(int room);

    @Select("select * from inventory where address_id=#{address_id};")
    List<Inventory> getByAddressId(int address_id);


    @Select("select count(I.inventory_num) from inventory as I ," +
            " equipment as E, qr_image as QR where I.inventory_num = E.inventory_num\n" +
            " AND E.id = QR.equipment_id AND I.inventory_num =#{inventory_num};")
    int checkIfInventoryNumHasEquipment(String inventory_num);

    @Select(SELECT_SHORT+" where room=#{room}")
    List<Inventory> getShortInventoryByRoom(int room);

    @Insert("insert into inventory(name, inventory_num, room, address_id) VALUES (#{name}," +
            "#{inventory_num}, #{room}, #{address_id});")
    int insertInventory(Inventory inventory);

    @InsertProvider(type = SqlProvider.class, method = "loadCsvToFinishInventory")
    int loadCsvToFinishInventory(String filename);

}
