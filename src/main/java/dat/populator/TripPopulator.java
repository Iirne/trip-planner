package dat.populator;

import dat.daos.TripDAO;
import dat.entities.Trip;
import dat.enums.Category;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TripPopulator{

    private static EntityManagerFactory EMF;
    private static TripDAO tripDAO;
    private static List<Trip> data = new ArrayList<>();


    public static void setEMF(EntityManagerFactory emf){
        if(EMF != null && EMF.equals(emf)){
            return;
        }
        EMF = emf;
        tripDAO = new TripDAO(emf);
    }
    //should be ran after GuidePopulator
    public static void populate() {
        data = new ArrayList<>();
        data.add(Trip.builder().name("trip1").start(LocalDateTime.of(1000,1,1,1,1,1)).end(LocalDateTime.of(1000,10,10,1,1,1)).longitude(1f).latitude(1f).price(1f).category(Category.BEACH).build());
        data.add(Trip.builder().name("trip2").start(LocalDateTime.of(2000,2,2,2,2,2)).end(LocalDateTime.of(2000,2,2,2,2,2)).longitude(2f).latitude(2f).price(2f).category(Category.CITY).build());
        data.add(Trip.builder().name("trip3").start(LocalDateTime.of(3000,3,3,3,3,3)).end(LocalDateTime.of(3000,3,3,3,3,3)).longitude(3f).latitude(3f).price(3f).category(Category.BEACH).build());

        data.forEach(tripDAO::create);
        if (GuidePopulator.isHasPopulated()){
            tripDAO.addGuideToTrip(1,1);
            tripDAO.addGuideToTrip(2,1);
            tripDAO.addGuideToTrip(2,2);
            tripDAO.addGuideToTrip(2,3);
            tripDAO.addGuideToTrip(3,2);
            tripDAO.addGuideToTrip(3,3);
        }
    }

    public static List<Trip> fetch() {
        return tripDAO.readAll();
    }
}