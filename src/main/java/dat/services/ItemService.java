package dat.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

public class ItemService {
    private final FetchTools fetchTools;

    public ItemService(FetchTools fetchTools) { this.fetchTools = fetchTools; }

    public responseDTO getItems(String category) {
        return fetchTools.getFromApi(Uri(category), responseDTO.class);
    }

    private static String Uri(String category){
        return "https://packingapi.cphbusinessapps.dk/packinglist/" +  category;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class responseDTO{
        private itemDTO[] items;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class itemDTO{
        private String name;
        private float weightInGrams;
        private int quantity;
        private String description;
        private String category;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
        private buyingOptionDTO[] buyingOptions;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class buyingOptionDTO{
        private String shopName;
        private String shopUrl;
        private float price;
    }
}