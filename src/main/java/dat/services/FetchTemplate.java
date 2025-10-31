package dat.services;

public class FetchTemplate {
    private final FetchTools fetchTools;

    public FetchTemplate(FetchTools fetchTools) { this.fetchTools = fetchTools; }

    //public example getexample(String sample) {
    //    return fetchTools.getFromApi(Uri(sample), example.class);
    //}

    private static String Uri(String sample){
        return "https://packingapi.cphbusinessapps.dk/packinglist/" +  sample;
    }
}
