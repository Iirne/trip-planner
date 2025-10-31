package dat.controllers;

import dat.config.HibernateConfig;
import dat.daos.GuideDAO;
import dat.daos.TripDAO;
import dat.dtos.GuidePriceOutPutDTO;
import dat.dtos.TripOutputDTO;
import dat.dtos.TripInputDTO;
import dat.entities.Trip;
import dat.enums.Category;
import dat.services.FetchTools;
import dat.services.ItemService;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TripsController implements IController<TripInputDTO> {
    private static TripsController Instance;
    private TripDAO tripDao;
    private GuideDAO guideDao;
    private EntityManagerFactory emf;
    private ItemService itemService;

    private TripsController(){}

    public static TripsController getInstance(){
        if (Instance == null){
            Instance = new TripsController();
        }
        return Instance;
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
        this.tripDao = new TripDAO(emf);
        this.guideDao = new GuideDAO(emf);
        itemService = new ItemService(new FetchTools());
    }

    @Override
    public void read(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();

        Optional<Trip> entitytrip = tripDao.read(id);
        if (entitytrip.isEmpty()) {
            throw new EntityNotFoundException();
        }

        TripOutputDTO tripOutputDTO = new TripOutputDTO(entitytrip.get());
        ctx.status(200)
                .json(tripOutputDTO, TripOutputDTO.class);
    }

    @Override
    public void readAll(Context ctx) {
        List<TripOutputDTO> tripOutputDTOs = tripDao.readAll().stream().map(TripOutputDTO::new).toList();

        String q = ctx.queryParam("category");
        if (q != null && !q.isBlank()) {
            tripOutputDTOs = tripOutputDTOs.stream().filter(tripOutputDTO -> tripOutputDTO.getCategory().name().toLowerCase() == q.toLowerCase()).toList();
        }
        ctx.status(200)
                .json(tripOutputDTOs, TripOutputDTO.class);
    }

    @Override
    public void create(Context ctx) {
        TripInputDTO req = validateInput(ctx);

        Trip saved = tripDao.create(new Trip(req));

        TripOutputDTO output = new TripOutputDTO(saved);

        ctx.status(201).json(output, TripOutputDTO.class);
    }

    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        TripInputDTO input = validateInput(ctx);
        Trip trip = new Trip(input);
        trip.setId(id);
        Trip saved = tripDao.update(id,trip );

        TripOutputDTO output = new TripOutputDTO(saved);

        ctx.status(200).json(output, TripOutputDTO.class);
    }

    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        tripDao.delete(id);
        ctx.status(204);
    }

    public void addGuideToTrip(Context ctx) {
        int tripid = ctx.pathParamAsClass("tripId", Integer.class).get();
        int guideid = ctx.pathParamAsClass("guideId", Integer.class).get();
        tripDao.addGuideToTrip(guideid, tripid);
        ctx.status(204);
    }

    public void getTripItems(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Optional<Trip> entitytrip = tripDao.read(id);
        if (entitytrip.isEmpty()) {
            throw new EntityNotFoundException();
        }

        ItemService.responseDTO response = itemService.getItems(entitytrip.get().getCategory().name().toLowerCase());

        ctx.json(response);
        ctx.status(200);
    }

    public void getTripItemWeights(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Optional<Trip> entitytrip = tripDao.read(id);
        if (entitytrip.isEmpty()) {
            throw new EntityNotFoundException();
        }

        ItemService.responseDTO response = itemService.getItems(entitytrip.get().getCategory().name().toLowerCase());
        float weight = 0;

        for (ItemService.itemDTO item : response.getItems()) {
            weight += item.getWeightInGrams();
        }

        ctx.json(weight);
        ctx.status(200);
    }


    public void getGuidePrice(Context ctx) {
        List<GuidePriceOutPutDTO> priceOutPutDTOS = guideDao.readAll()
                .stream().map(guideDao::FullLoad).map(guide -> {
                    return new GuidePriceOutPutDTO(
                            guide.getId(),
                            guide.getTrips().stream().collect(Collectors.summingDouble(Trip::getPrice)).floatValue());
                }).toList();


        ctx.status(200)
                .json(priceOutPutDTOS, GuidePriceOutPutDTO.class);
    }


    @Override
    public TripInputDTO validateInput(Context ctx) {
        return ctx.bodyValidator(TripInputDTO.class)
                .check(Input -> Input.getName() != null && Input.getName().length() <= 10000, "name too long")
                .check(Input -> Input.getCordinates() != null && Input.getCordinates().length == 2, "too many cordinates")
                .check(Input -> Input.getCategory() != null, "invalid category")
                .get();
    }
}
