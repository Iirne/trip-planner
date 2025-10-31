package dat.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode

@Entity
@Table(name = "guides")
public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String Email;
    private String phone;
    private LocalDateTime startedWorking;

    @ManyToMany (mappedBy = "guides")
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private Set<Trip> trips = new HashSet<>();

    public boolean Addtrip(Trip trip) {
        if (trips.add(trip)) trip.addGuide(this);
        return true;
    }

    public boolean removeTrip(Trip trip){
        if (trips.remove(trip)) trip.removeGuide(this);
        return true;
    }
}
