package dat.rest;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.controllers.TripsController;
import dat.daos.IDAO;
import dat.dtos.GuidePriceOutPutDTO;
import dat.dtos.TripOutputDTO;
import dat.enums.Category;
import dat.populator.GuidePopulator;
import dat.populator.PopulatorTemplate;
import dat.populator.TripPopulator;
import dat.services.ItemService;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("IntegrationTest")
class TripsRestTest {
    private static Javalin app;
    static private EntityManagerFactory emf;

    @BeforeAll
    static void setupOnce() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        GuidePopulator.setEMF(emf);
        TripPopulator.setEMF(emf);
        TripsController.getInstance().setEmf(emf);

        app = ApplicationConfig.startServer(7007);

        RestAssured.baseURI = "http://localhost:7007/api/trips";
    }

    @BeforeEach
    void setup() {
        GuidePopulator.populate();
        TripPopulator.populate();
    }

    @AfterEach
        //delete all data
    void teardown() {
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        //delete everything
        em.createNativeQuery("TRUNCATE TABLE guides, trips RESTART IDENTITY CASCADE")
                .executeUpdate();
        em.getTransaction().commit();

        em.close();
    }

    @AfterAll
    static void tearDownOnce() {
        ApplicationConfig.stopServer(app);
        if (emf != null) emf.close();
    }


    @Test
    void read() {
        TripOutputDTO expected = TripPopulator.fetch().stream().map(TripOutputDTO::new).toArray(TripOutputDTO[]::new)[0];

        TripOutputDTO actual = given().
                when()
                .get("/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().body().jsonPath().getObject("$", TripOutputDTO.class);

        assertThat(actual, equalTo(expected));
    }

    @Test
    void readAll() {
        TripOutputDTO[] expected = TripPopulator.fetch().stream().map(TripOutputDTO::new).toArray(TripOutputDTO[]::new);

        List<TripOutputDTO> actual = given().
                when()
                .get("/")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().body().jsonPath().getList("$", TripOutputDTO.class);

        assertThat(actual, containsInAnyOrder(expected));
    }

    @Test
    void create() {

        TripOutputDTO expected = TripOutputDTO.builder().id(4).name("demo").start("2000-02-02 02:02").end("2000-02-02 02:02").cordinates(new float[]{1f,1f}).price(2f).category(Category.BEACH).build();

        String body =
        "{\n" +
        "    \"name\": \"" + expected.getName() + "\",\n" +
        "    \"start\": \"" + expected.getStart() + "\",\n" +
        "    \"end\": \"" + expected.getEnd() + "\",\n" +
        "    \"cordinates\": [\n" +
        "      " + expected.getCordinates()[0] + ",\n" +
        "      " + expected.getCordinates()[1] + "\n" +
        "    ],\n" +
        "    \"price\": " + expected.getPrice() + ",\n" +
        "    \"category\": \"" + expected.getCategory() + "\"\n" +
        "}";

        TripOutputDTO added = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract().body().jsonPath().getObject("$", TripOutputDTO.class);

        assertThat(added,equalTo(expected));

        TripOutputDTO actual = given().
                when()
                .get("/4")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().body().jsonPath().getObject("$", TripOutputDTO.class);

        assertThat(actual, equalTo(expected));
    }

    @Test
    void update() {
        TripOutputDTO expected = TripOutputDTO.builder().id(1).name("demo").start("2000-02-02 02:02").end("2000-02-02 02:02").cordinates(new float[]{1f,1f}).price(2f).category(Category.BEACH).build();

        String body =
                "{\n" +
                        "    \"name\": \"" + expected.getName() + "\",\n" +
                        "    \"start\": \"" + expected.getStart() + "\",\n" +
                        "    \"end\": \"" + expected.getEnd() + "\",\n" +
                        "    \"cordinates\": [\n" +
                        "      " + expected.getCordinates()[0] + ",\n" +
                        "      " + expected.getCordinates()[1] + "\n" +
                        "    ],\n" +
                        "    \"price\": " + expected.getPrice() + ",\n" +
                        "    \"category\": \"" + expected.getCategory() + "\"\n" +
                        "}";

        TripOutputDTO added = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .put("/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().body().jsonPath().getObject("$", TripOutputDTO.class);

        assertThat(added,equalTo(expected));

        TripOutputDTO actual = given().
                when()
                .get("/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().body().jsonPath().getObject("$", TripOutputDTO.class);

        assertThat(actual, equalTo(expected));
    }

    @Test
    void delete() {

        TripOutputDTO deleted = TripPopulator.fetch().stream().map(TripOutputDTO::new).toArray(TripOutputDTO[]::new)[0];

        given().
                when()
                .delete("/1")
                .then()
                .statusCode(204);

        TripOutputDTO[] expected = TripPopulator.fetch().stream().map(TripOutputDTO::new).toArray(TripOutputDTO[]::new);

        List<TripOutputDTO> actual = given().
                when()
                .get("/")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().body().jsonPath().getList("$", TripOutputDTO.class);

        assertThat(actual, containsInAnyOrder(expected));

        assertThat(actual, everyItem(not((deleted))));
    }

    @Test
    void addGuideToTrip() {
    }

    @Test
    void getTripItems() {
        //calls to an external api so you can only confirm that something is
        ItemService.responseDTO actual = given().
                when()
                .get("/1/packing")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().body().jsonPath().getObject("$", ItemService.responseDTO.class);

        assertThat(actual.getItems(), is(instanceOf(ItemService.itemDTO[].class)));
    }

    @Test
    void getTripItemWeights() {
        //calls to an external api so you can only confirm that something is
        Float actual = given().
                when()
                .get("/1/packing/weight")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().body().jsonPath().getObject("$", Float.class);

        assertThat(actual.getClass(), equalTo(Float.class));
    }

    @Test
    void getGuidePrice() {
        List<GuidePriceOutPutDTO> actual = given().
                when()
                .get("/guides/totalprice")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().body().jsonPath().getList("$", GuidePriceOutPutDTO.class);

        assertThat(actual.get(0), equalTo(new GuidePriceOutPutDTO(1, 1f)));
        assertThat(actual.get(1), equalTo(new GuidePriceOutPutDTO(2, 6f)));
        assertThat(actual.get(2), equalTo(new GuidePriceOutPutDTO(3, 5f)));
        assertThat(actual.get(3), equalTo(new GuidePriceOutPutDTO(4, 0f)));
        assertThat(actual.get(4), equalTo(new GuidePriceOutPutDTO(5, 0f)));
    }
}