package test;

import com.lis.qr_back.additional.QR.QRCreator;
import com.lis.qr_back.dao.QrImageDAO;
import com.lis.qr_back.model.QrImage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QrImageMapperTest {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    QrImageMapper qrImageMapper;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    QrImageDAO qrImageDAO;


    @Test
    public void insert() throws Exception {
        //preparing
        String stringToConvert = "Test string to convert ";
        byte[] bytesImage = QRCreator.stringToBuffImageToBytes(stringToConvert);
        QrImage qrImage = new QrImage("file.png", bytesImage);

        //testing
        int res = qrImageMapper.insert(qrImage);
        //System.out.println(qrImage.getId());


       // int ress = qrImageDAO.insertFromString(stringToConvert);

      //  System.out.println(ress);

        //validation
       // assertEquals("Must be 1 ", 1, res);
    }

    @Test
    public void getById() throws Exception {
        //preparing
        int qr_pic_id = 3;
        String pathToSave = "./src/test/resources/qr_pics/somePicName.png";

        //testing
        QrImage qrImage = qrImageMapper.getById(qr_pic_id);

       byte[] byteImg = qrImage.getData();

        ByteArrayInputStream bais = new ByteArrayInputStream(byteImg);
        BufferedImage bufferedImage = ImageIO.read(bais);


        //validation
        assertEquals(5, bufferedImage.getType());
        assertTrue(ImageIO.write(bufferedImage, "png", new File(pathToSave)));

    }

    @Test
    public void getByInventoryNum() throws Exception {

        //preparing
        String inventory_num = "102400001";

        //testing
        QrImage qrImage = qrImageMapper.getByInventoryNum(inventory_num);
        byte[] byteImg = qrImage.getData();

        ByteArrayInputStream bais = new ByteArrayInputStream(byteImg);
        BufferedImage bufferedImage = ImageIO.read(bais);


        //validation
       assertTrue(qrImage != null);
    }

    @Test
    public void saveQrFromString(){
       // String pathToSave="C:\\Z\\alice\\work\\pf\\for_qr_project\\QR_back\\src\\main\\resources\\qr\\inventory\\" + 33 + "\\";
        String pathToSave="./src/main/resources/qr/inventory/" + 33 + "/";


        new File(pathToSave).mkdirs();


        String stringToConvert = "{\n" +
                "        \"id\": null,\n" +
                "        \"type\": \"Видеокарта\",\n" +
                "        \"vendor\": \"Nvidia\",\n" +
                "        \"model\": \"GeForce GTX\",\n" +
                "        \"series\": \"970\",\n" +
                "        \"inventory_num\": \"33211155\",\n" +
                "        \"attributes\": null,\n" +
                "        \"serial_num\": null,\n" +
                "        \"room\": null,\n" +
                "        \"id_asDetailIn\": null,\n" +
                "        \"id_tp\": null,\n" +
                "        \"id_user\": null,\n" +
                "        \"user_info\": null\n" +
                "    }";

        String stringToConvert2 = " {\n" +
                "        \"id\": null,\n" +
                "        \"type\": \"Видеокарта\",\n" +
                "        \"vendor\": \"Nvidia\",\n" +
                "        \"model\": \"GeForce GTX\",\n" +
                "        \"series\": \"970\",\n" +
                "        \"inventory_num\": \"33211155\"}";
        String pictureName = "qr_test";


        try {
            qrImageDAO.saveQrFromString(stringToConvert, pathToSave, pictureName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
