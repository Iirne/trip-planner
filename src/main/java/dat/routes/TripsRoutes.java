package dat.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat.config.HibernateConfig;
import dat.controllers.TripsController;
import dat.security.controllers.SecurityController;
import dat.security.enums.Role;
import dat.utils.Utils;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class TripsRoutes {
    private static TripsController controller;

    public static EndpointGroup getRoutes() {
        controller = TripsController.getInstance();
        return () -> {
            get("/{id}", controller::read, Role.ANYONE);
            get("/", controller::readAll, Role.ANYONE);
            post("/",controller::create,Role.USER);
            put("/{id}",controller::update,Role.USER);
            delete("/{id}",controller::delete,Role.USER);

            get("{tripId}/guides/{guideId}",controller::addGuideToTrip,Role.ANYONE);
            get("/guides/totalprice",controller::getGuidePrice,Role.ANYONE);

            get("/{id}/packing", controller::getTripItems, Role.ANYONE);
            get("/{id}/packing/weight", controller::getTripItemWeights, Role.ANYONE);
        };
    }
}
