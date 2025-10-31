package dat.dtos;

import dat.entities.Trip;
import dat.enums.Category;
import dat.utils.TimeMapper;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripOutputDTO {
    private Integer id;
    private String name;
    private String start;
    private String end;
    private float[] cordinates;
    private float price;
    private Category category;

    public TripOutputDTO(Trip trip) {
        this.id = trip.getId();
        this.name = trip.getName();
        this.start = TimeMapper.LocalDateTimeToString(trip.getStart());
        this.end = TimeMapper.LocalDateTimeToString(trip.getEnd());;
        this.cordinates = new float[]{trip.getLongitude(), trip.getLatitude()};
        this.price = trip.getPrice();
        this.category = trip.getCategory();
    }
}
