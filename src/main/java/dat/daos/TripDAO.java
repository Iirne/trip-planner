package dat.daos;

import dat.entities.Guide;
import dat.entities.Trip;
import dat.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

public class TripDAO extends AbstractDAO<Trip,Integer>{

    public TripDAO(EntityManagerFactory emf) {
        super(emf, "SELECT t FROM Trip t ORDER BY t.name ASC");
    }

    @Override
    public boolean delete(Integer i) {
        try (EntityManager em = super.getEmf().createEntityManager()) {
            try {
                em.getTransaction().begin();
                Trip existing = em.find(persistenceClass(), i);
                if (existing == null) {
                    throw new EntityNotFoundException("No " + persistenceClass().getName() + "was found with this id");
                }
                existing.getGuides().clear();
                em.merge(existing);
                em.remove(existing);
                em.getTransaction().commit();
                return true;
            } catch (Exception e) {
                em.getTransaction().rollback();
                super.getLOGGER().error(e.getMessage());
                throw new DatabaseException(500, e.getMessage());
            }
        }
    }

    public void addGuideToTrip(Integer guideID, Integer tripID){
        try (EntityManager em = super.getEmf().createEntityManager()) {
            try {
                em.getTransaction().begin();
                //validation
                Guide guide = em.find(Guide.class, guideID.intValue());
                if (guide == null) {
                    throw new EntityNotFoundException("Entity \"guide\" not found");
                }

                Trip trip = em.find(persistenceClass(), tripID.intValue());
                if (trip == null) {
                    throw new EntityNotFoundException("Entity \"trip\" not found");
                }
                guide.Addtrip(trip);
                em.merge(guide);
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                super.getLOGGER().error(e.getMessage());
                throw new DatabaseException(500, e.getMessage());
            }
        }
    }

    @Override
    Class<Trip> persistenceClass() {
        return Trip.class;
    }
}
