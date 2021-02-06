package sandeep.sagar.Covid19Tracker.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sandeep.sagar.Covid19Tracker.models.LocationStats;

import javax.annotation.PostConstruct;
import javax.xml.stream.Location;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

//Makes it a spring service
@Service
public class CoronaVirusDataService {

    /*Get the data and parse
     *  fetchVirusData() Makes http call for the VIRUS_DATA_URL
     *  Create new instance of location stats like array -> List<LocationStats>
     */
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    /*Tells spring-  When you construct instance of the service execute this method
     *Spring creates instance of this class and spring will execute this post finishing
     * @Scheduled Runs the method on daily basis -> cron="sec min hr * * *" -> it basically runs at 1st hour of every day
     */
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {

        //Due to concurrency reasons. There a lot of people accessing this service. So we dont want them to get errors
         List<LocationStats> newstats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        //This httpRequest basically asks where do I need to access the data
       HttpRequest request= HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
       /*Synchronous send. Tell what to do with the body
        * Catches the exception
        */
      HttpResponse<String> httpResponse= client.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        for (CSVRecord record : records) {
                LocationStats locationStat = new LocationStats();
                locationStat.setState(record.get("Province/State"));
                locationStat.setCountry(record.get("Country/Region"));
            //To get the last column i.e the latest day
                int latestCases = Integer.parseInt(record.get(record.size()-1));
                int prevDayCases =Integer.parseInt(record.get(record.size()-2));
                locationStat.setLatestTotalCases(latestCases);
                locationStat.setDifferenceFromPreviousDay(latestCases-prevDayCases);
            //Set this to the list
            newstats.add(locationStat);
        }
        this.allStats = newstats;

    }
}
