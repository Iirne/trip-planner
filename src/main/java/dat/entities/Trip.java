package dat.entities;

import dat.dtos.TripInputDTO;
import dat.enums.Category;
import dat.utils.TimeMapper;
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
@Table(name = "trips")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Integer id;

    private String name;
    @Column(name="start_time")
    private LocalDateTime start;
    @Column(name="end_time")
    private LocalDateTime end;
    private float longitude;
    private float latitude;
    private float price;
    private Category category;
    @ManyToMany
    @JoinTable(
            name = "guides_trips",
            joinColumns = @JoinColumn(name = "guide_id"),
            inverseJoinColumns = @JoinColumn(name = "trips_id", referencedColumnName = "id")
    )
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Guide> guides = new HashSet<>();

    public Trip(TripInputDTO tripInputDTO) {
        this.name = tripInputDTO.getName();
        this.start = TimeMapper.StringToLocalDateTime(tripInputDTO.getStart());
        this.end = TimeMapper.StringToLocalDateTime(tripInputDTO.getEnd());
        this.longitude = tripInputDTO.getCordinates()[0];
        this.latitude = tripInputDTO.getCordinates()[1];
        this.price = tripInputDTO.getPrice();
        this.category = Category.valueOf(tripInputDTO.getCategory());


    }

    public boolean addGuide(Guide guide){
        if (guides.add(guide)) guide.Addtrip(this);
        return true;
    }

    public boolean removeGuide(Guide guide){
        if (guides.remove(guide)) guide.removeTrip(this);
        return true;
    }
}
