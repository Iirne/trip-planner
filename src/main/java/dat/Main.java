package dat;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.controllers.TripsController;
import dat.populator.GuidePopulator;
import dat.populator.TripPopulator;
import dat.security.controllers.AccessController;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        GuidePopulator.setEMF(emf);
        TripPopulator.setEMF(emf);
        TripsController.getInstance().setEmf(emf);

        GuidePopulator.populate();
        TripPopulator.populate();

        Javalin app = ApplicationConfig.startServer(7070);
        AccessController accessController = new AccessController();
        app.beforeMatched(accessController::accessHandler);

    }
}