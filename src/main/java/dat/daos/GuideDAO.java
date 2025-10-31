package dat.daos;

import dat.entities.Guide;
import dat.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.query.NativeQuery;

import java.util.Optional;

public class GuideDAO extends AbstractDAO<Guide, Integer>{
    public GuideDAO(EntityManagerFactory emf) {
        super(emf, "SELECT g FROM Guide g ORDER BY g.name ASC");
    }

    public Guide FullLoad(Guide guide){
        try (EntityManager em = super.getEmf().createEntityManager()) {
            em.getTransaction().begin();
            TypedQuery<Guide> q = em.createQuery(
                    "select g from Guide g left join fetch g.trips where g.id = :value", persistenceClass()
            )
                    .setParameter("value",guide.getId());
            em.getTransaction().commit();

            return q.getSingleResult();
        } catch (Exception e) {
            System.out.println(guide.getId());
            super.getLOGGER().error(e.getMessage());
            throw new DatabaseException(500, e.getMessage());
        }

    }

    @Override
    Class<Guide> persistenceClass() {
        return Guide.class;
    }
}
