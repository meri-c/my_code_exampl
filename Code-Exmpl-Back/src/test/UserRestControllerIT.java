package test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lis.qr_back.mapper.UserMapper;
import com.lis.qr_back.model.PersonalData;
import com.lis.qr_back.model.PhoneNumber;
import com.lis.qr_back.model.User;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//tests for a user only, without any relations

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) //свой порт в конфигах
//@WebAppConfiguration //если используем моки
public class UserRestControllerIT {
    private static String[] lettersForList = {"a", "b", "c", "d", "e"};
    private EmbeddedDatabase db;
    private TestRestTemplate restTemplate = new TestRestTemplate();
    private String jsonMimeType = "application/json";


    //working with the created datasource (from app.prop)
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private UserMapper userMapper;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    UserRestController userRestController;


    //or will use mysql working db
 /*   @Before
    public void setUp() {
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("data.sql")
                .build();
    }*/
    @Test
    public void testAddUserPDataPNumber() throws Exception {
        User user = new User("Bi", "Bi");
        User user2 = new User("Bim", "Bim");
        PersonalData personalData = new PersonalData("Bi", "Bi", "Bi", "Bi", "Bi");
        PhoneNumber phoneNumber =  new PhoneNumber("3333-333-3322");
        personalData.getPhoneNumbers().add(phoneNumber);

       // assertNull("User already exists", userMapper.getByUsername(user.getUsername()));

        Map<String, Object> resultMap = userRestController.addUserPDataPNumber(user2, personalData, personalData.getPhoneNumbers());

        boolean result = (boolean) resultMap.get("result");
        String message = (String) resultMap.get("message");

        System.out.println(result);
        assertTrue(result);
        System.out.println(message);
    }


    @Test //rest template example
    public void testGetListOfUsers() throws Exception {
        //preparation
        doSmthWithList("delete", userMapper);
        doSmthWithList("insert", userMapper);

        //testing
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange
                ("http://localhost:8090/users", HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<User>>() {
                        });

        List<User> actual = responseEntity.getBody();

        /*List<String> actualNames = actual.stream()
                .map(User::getUsername)
                .collect(toList());*/

        //validate
        assertThat(actual, notNullValue());
      //  assertThat(actualNames, hasItems("b", "a"));

        doSmthWithList("delete", userMapper);
    }

    @Test //http client example
    public void testGetListOfUserUsingHttpClient() throws URISyntaxException, IOException {
        //preparation
        doSmthWithList("delete", userMapper);
        doSmthWithList("insert", userMapper);

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        ClientHttpRequest request = factory.createRequest(
                new URI("http", "//localhost:8090/users", null), HttpMethod.GET);

        //Testing
        ClientHttpResponse response = request.execute();
        String mimeType = response.getHeaders().getContentType().toString().split(";")[0].trim();

        //validation
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(mimeType, equalTo(jsonMimeType));

        doSmthWithList("delete", userMapper);
    }

    @Test
    public void testGetOneUserUsingResponseEntityStringAndJsonParser() throws URISyntaxException, IOException {
        //testing
        String userName = "Get2";
       // userMapper.insert(new User(userName, "get2", "get2"));
        ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:8090/users/" + userName, String.class);

        HttpStatus statusCode = entity.getStatusCode();

        //мапер для чтения формата json
        ObjectMapper mapper = new ObjectMapper();

        //возвращает json с нулевой позиции, можно еще вернуть длинну или массив
        JsonNode root = mapper.readTree(entity.getBody());
        JsonNode username = root.get("username");

        //validation
        assertThat(username.asText(), equalTo(userName));
        assertTrue(entity.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON));
        assertThat(statusCode, equalTo(HttpStatus.OK));

       // userMapper.delete(userName);
    }

    @Test
    public void testGetOneUserUsingResponseEntityUser() {
        //preparation
        String userName = "Get";
     //   userMapper.insert(new User(userName, "get", "get"));

        String mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE;
        System.out.println(mediaType);

        //testing
        ResponseEntity<User> entity = restTemplate.getForEntity("http://localhost:8090/users/" + userName, User.class);

        //validation
        assertNotNull(entity);
        assertThat(entity.getHeaders().getContentType().toString(), is(mediaType));
      //  assertThat(entity.getBody().getUsername(), is(userName));

     //   userMapper.delete(userName);
    }


}
