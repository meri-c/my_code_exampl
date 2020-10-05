package test;

import com.lis.qr_back.mapper.AddressMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    public class AddressDAOTest {
    @Autowired
    AddressDAO addressDAO;

    @Autowired
    AddressMapper addressMapper;
    @Test
    public void getPlainAddressById() throws Exception {


        Map< String, Object> address2 = addressDAO.getPlainAddressById(3);

        assertNotNull(address2);
        for(Map.Entry s: address2.entrySet())
        System.out.println(s.getKey()+" "+ s.getValue());

    }

}
