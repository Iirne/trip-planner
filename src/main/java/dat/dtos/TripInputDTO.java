package dat.dtos;

import dat.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripInputDTO {
    private String name;
    private String start;
    private String end;
    private float[] cordinates;
    private float price;
    private String category;
}
