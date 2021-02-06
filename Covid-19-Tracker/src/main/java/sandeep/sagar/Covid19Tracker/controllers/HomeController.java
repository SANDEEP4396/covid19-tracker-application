package sandeep.sagar.Covid19Tracker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sandeep.sagar.Covid19Tracker.models.LocationStats;
import sandeep.sagar.Covid19Tracker.services.CoronaVirusDataService;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    /*Thymleaf is used control this.
     * Since location stat value is in service. We can autowire into controller.
     */
    @GetMapping("/")
    public String home(Model model){
        //To access the attributes in Home.html
        //We just need the total cases calculation to display in the home page so declare in home controller method
        List<LocationStats> allStats =coronaVirusDataService.getAllStats();
        int totalCases = allStats.stream().mapToInt(stat ->stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat ->stat.getDifferenceFromPreviousDay()).sum();
        model.addAttribute( "locationStats" ,allStats );
        model.addAttribute( "totalReportedCases" ,totalCases);
        model.addAttribute( "totalNewCases" ,totalNewCases);
        return "home";
    }
}
