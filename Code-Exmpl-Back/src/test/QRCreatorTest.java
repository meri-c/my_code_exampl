package test;

import com.lis.qr_back.dao.QrImageDAO;
import com.lis.qr_back.dao.UserDAO;
import com.lis.qr_back.model.QrImage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//otherwise mybatis will create his own emb db

public class QRCreatorTest {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public QrImageDAO qrImageDAO;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public UserDAO userDAO;

    @Test
    public void someTest(){
        List bla = null;
        System.out.print(bla.isEmpty());
    }

    @Test
    public void qrPojoToBuffImageSavePng() throws Exception {
        int id = 7;
        String pathToSave = "./src/test/resources/qr_pics/";
        QrImage qr_Image = qrImageDAO.getById(id);
    QRCreator.qrPojoToBuffImageSavePng(qr_Image, pathToSave);
    }

    @Test
    @Rollback(value = false)
    public void stringToBuffImageToBytes() throws Exception {
        //preparing
        String stringToConvert = "Test string to convert ";
        byte[] bytesImage = QRCreator.stringToBuffImageToBytes(stringToConvert);

        QrImage qrImage = new QrImage("file.png", bytesImage);

        System.out.println(qrImage.toString());
    }

    @Test
    public void stringToQrImg() throws Exception {
        //preparing
        String stringToConvert = "Test string to convert";
        String fileName = "test";
        String pathToSave = "./src/test/resources/qr_pics/";
        File file = new File(pathToSave + fileName + ".png");

        BufferedImage bufferedImage;
        Files.deleteIfExists(Paths.get(file.getPath()));

        //testing
        boolean result = QRCreator.stringToBuffImageSavePng(stringToConvert, file);
        bufferedImage = ImageIO.read(file);

        System.out.println(bufferedImage.getData());
        //validation
        assertTrue(result);
        assertEquals(5, bufferedImage.getType());
    }

    @Test
    public void stringToQrImage() throws Exception {
        QrImage image = QRCreator.stringToQrImage("Blalalala", "123123123");
        System.out.println(image.getName());
    }

    @Test
    public void saveQrFromTheEquipment() throws Exception {
    }

}
