package main.dao;

import com.lis.qr_back.additional.QR.QRCreator;
import com.lis.qr_back.mapper.QrImageMapper;
import com.lis.qr_back.model.QrImage;
import lombok.extern.java.Log;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Log
@Component
public class QrImageDAO implements QrImageMapper {
    private SqlSession sqlSession;
    private static final String QR_IMAGE_MAPPER = "com.lis.qr_back.main.mapper.QrImageMapper";

    public QrImageDAO(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int ifExistsById(Integer qr_id) {
        return sqlSession.selectOne(QR_IMAGE_MAPPER + ".ifTpExistsById", qr_id);
    }

    @Override
    public int insert(QrImage QrImage) {
        return sqlSession.insert(QR_IMAGE_MAPPER + ".insert", QrImage);
    }

    @Override
    public int insertWithEquipmentId(QrImage QrImage) {
        return sqlSession.insert(QR_IMAGE_MAPPER + ".insertWithEquipmentId", QrImage);    }

    @Override
    public QrImage getById(int qr_img_id) {
        return sqlSession.selectOne(QR_IMAGE_MAPPER + ".getById", qr_img_id);
    }

    @Override
    public QrImage getByInventoryNum(String inventory_num) {
        return sqlSession.selectOne(QR_IMAGE_MAPPER + ".getByInventoryNum", inventory_num);
    }

    @Override
    public List<QrImage> getByRoom(String room) {
        return sqlSession.selectList(QR_IMAGE_MAPPER + ".getByRoom", room);
    }

    @Override
    public QrImage getByEquipmentId(int equipment_id) {
        return sqlSession.selectOne(QR_IMAGE_MAPPER + ".getByEquipmentId", equipment_id);
    }

    //----Complicated---//

    //create Qr image from the given string, returns the id
    public int insertFromString(String stringToConvert, String inventory_num) {
        QrImage qrImage = QRCreator.stringToQrImage(stringToConvert, inventory_num);
        insert(qrImage);

        return qrImage.getId();
    }

    //create Qr image from the given string, returns the id
    public int insertFromStringWithId(String stringToConvert, String inventory_num, int equipment_id) {
        QrImage qrImage = QRCreator.stringToQrImage(stringToConvert, inventory_num);
        qrImage.setEquipment_id(equipment_id);
        insertWithEquipmentId(qrImage);

        return qrImage.getId();
    }

    /*pathToSave like .../one/two/.. */
    public String saveQrFromString(String stringToConvert, String pathToSave) throws IOException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String fileName = dateFormat.format(new Date())+".png";

        File file = new File(pathToSave+fileName);

        QRCreator.stringToBuffImageSavePng(stringToConvert, file);

        return file.toString();
    }


    public String saveQrFromString(String stringToConvert, String pathToSave, String pictureName) throws IOException {

        String fileName = pictureName+".png";

        File file = new File(pathToSave+fileName);

        QRCreator.stringToBuffImageSavePng(stringToConvert, file);

        return file.toString();
    }

}
