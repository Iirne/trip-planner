package dat.populator;

import dat.daos.GuideDAO;
import dat.entities.Guide;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GuidePopulator{

    private static EntityManagerFactory EMF;
    private static GuideDAO GuideDao;
    private static List<Guide> data = new ArrayList<>();
    @Getter
    private static boolean hasPopulated = false;

    public static void setEMF(EntityManagerFactory emf){
        if(EMF != null && EMF.equals(emf)){
            return;
        }
        EMF = emf;
        GuideDao = new GuideDAO(emf);
        hasPopulated = false;
    }

    public static void populate() {
        data = new ArrayList<>();
        data.add(Guide.builder().name("test 1").Email("test 1").phone("test 1").startedWorking(LocalDateTime.of(1000,1,1,1,1,1)).build());
        data.add(Guide.builder().name("test 2").Email("test 2").phone("test 2").startedWorking(LocalDateTime.of(2000,2,2,2,2,2)).build());
        data.add(Guide.builder().name("test 3").Email("test 3").phone("test 3").startedWorking(LocalDateTime.of(3000,3,3,3,3,3)).build());
        data.add(Guide.builder().name("test 4").Email("test 4").phone("test 4").startedWorking(LocalDateTime.of(4000,4,4,4,4,4)).build());
        data.add(Guide.builder().name("test 5").Email("test 5").phone("test 5").startedWorking(LocalDateTime.of(5000,5,5,5,5,5)).build());

        data.forEach(GuideDao::create);
        hasPopulated = true;
    }

    public static List fetch() {
        return GuideDao.readAll();
    }


}

