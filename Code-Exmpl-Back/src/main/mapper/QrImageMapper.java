package main.mapper;

import com.lis.qr_back.model.QrImage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import java.util.List;

@Mapper
public interface QrImageMapper {
    String SELECT_IF_EXISTS = "select count(id) from qr_image where id=#{id};";
    String FROM_QR_THROUGH_EQUIPMENT_BY_INV_NUM = "from qr_image, equipment where qr_image.equipment_id = " +
            "equipment.id and equipment.inventory_num=#{inventory_num}";
    String FROM_QR_THROUGH_EQUIPMENT_BY_ROOM = "from qr_image, equipment where qr_image.equipment_id =" +
            " equipment.id and equipment.room=#{room}";

    //---Existance---

    @Select(SELECT_IF_EXISTS)
    int ifExistsById(Integer qr_id);

    //----Get by val---

    //@Results(@Result(property = "data", column = "data", javaType=byte [], jdbcType = JdbcType.BLOB))
    @Select("select * from qr_image where id=#{id}")
    QrImage getById(int qr_img_id);

    @Select("select qr_image.* " + FROM_QR_THROUGH_EQUIPMENT_BY_INV_NUM)
    QrImage getByInventoryNum(String inventory_num);

    @Select("select qr_image.* " + FROM_QR_THROUGH_EQUIPMENT_BY_ROOM)
    List<QrImage> getByRoom(String room);

    @Select("select qr_image.* from qr_image where equipment_id=#{equipment_id}")
    QrImage getByEquipmentId(int equipment_id);

    //----Inserts---

    @Insert("insert into qr_image (name, data) values (#{name}, #{data, jdbcType = BLOB})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", keyColumn = "id", before = false,
            resultType = int.class)
        //@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insert(QrImage QrImage);

    @Insert("insert into qr_image (name, data, equipment_id) values (#{name}, #{data, jdbcType = BLOB}," +
            " #{equipment_id})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", keyColumn = "id", before = false,
            resultType = int.class)
        //@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertWithEquipmentId(QrImage QrImage);
}
