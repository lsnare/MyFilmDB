package com.lsnare.film.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lsnare.film.dao.FilmDAO;
import com.lsnare.film.model.Actor;
import com.lsnare.film.model.Film;
import com.lsnare.film.service.HTTPService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lucian on 9/8/15.
 */
public class FilmUtils {

    static Log log = LogFactory.getLog(FilmUtils.class);

    public static String addFilmToDatabase(String filmTitle){
        try{
            String res = HTTPService.sendGet("http://www.myapifilms.com/imdb?title=" + filmTitle + "&format=JSON&lang=en-us&actors=S");
            Gson gson = new GsonBuilder().create();
            Film film = gson.fromJson(res.substring(1, res.length()-1), Film.class);
            ApplicationContext context =
                    new ClassPathXmlApplicationContext("Spring-Module.xml");
            FilmDAO filmDAO = (FilmDAO) context.getBean("filmDAO");
            filmDAO.insert(film);

        } catch(Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }

        return "<b>Film added to the database successfully!</b>";

    }

    public static List<Film> searchFilmByTitle(String filmTitle){
        List<Film> films = new ArrayList<>();
        try{
            ApplicationContext context =
                    new ClassPathXmlApplicationContext("Spring-Module.xml");
            FilmDAO filmDAO = (FilmDAO) context.getBean("filmDAO");
            films = filmDAO.selectFilmsByTitle(filmTitle);
            log.info("Found " + films.size() + " films when searching");
        } catch(Exception e){
            System.out.println("HTTPService error: " + e);
        }
        return films;
    }

    public static Map<String, Object> buildFilmSearchResults(List<Film> films){
        String filmData = "";
        Map<String, Object> attributes = new HashMap();

        attributes.put("searchResultsHeader", "<h3>Search Results</h3>");

        if(films.size() > 0) {
            String shortPlot = "";
            String longPlot = "";
            String tdShort = "";
            String tdLong="";
            int count = 0;

            filmData += "<table border=1> <col width=\"80\"> <col width=\"100\"> <col width=\"50\"> <col width=\"500\"> <col width=\"250\">"
                    + "<tr>"
                    + "<th>IMDB ID</th> <th>Title</th> <th>Year</th> <th>Plot</th> "; // <th>Director</th> <th>Actors</th></tr> ";

            for (Film film : films) {
                //Get the first full sentence for the short plot
                shortPlot = film.getPlot().split("\\.", 25)[0] + "...";
                longPlot = film.getPlot();

                String rowId = "row_" + count;
                filmData += "<tr id = \"" + rowId + "\"><td>" + film.getIdIMDB() + "</td>"
                        + "<td>" + film.getTitle() + "</td>"
                        + "<td>" + film.getYear() + "</td>"
                        //Create hidden td tag to hold the longer plot description
                        + "<td>" + shortPlot
                            + "<a href =\"#\" onclick=\"showLongPlot(\'" + rowId + "\')\"> More </a>"
                        + "</td>"
                        + "<td style=\"display: none;\">" + longPlot
                            + "<a href=\"#\" onclick=\"showShortPlot(\'" + rowId + "\')\"> Less </a>"
                        + "</td>"
                        + "<td>" + /*film.getDirectors().get(0) + */ "</td> <td>";
                        for (Actor actor : film.getActors()){
                            filmData += actor.getActorName() + "<br>";
                        }
                        filmData += "</tr>";

                count ++;
            }
            filmData += "</table>";
        } else {
            filmData = "<b>No results found!</b>";
        }
        attributes.put("filmData", filmData);

        return attributes;
    }

    public static Map<String, String> searchRolesForActorByName(String actorName){
        Map<String, String> rolesForActor = new HashMap();
        try{
            ApplicationContext context =
                    new ClassPathXmlApplicationContext("Spring-Module.xml");
            FilmDAO filmDAO = (FilmDAO) context.getBean("filmDAO");
            rolesForActor = filmDAO.selectRolesForActor(actorName);
            log.info("Found " + rolesForActor.size() + " roles when searching");
        } catch(Exception e){
            System.out.println("HTTPService error: " + e);
        }
        return rolesForActor;
    }

    public static Map<String, Object> buildActorRolesSearchResults(String actorName, Map<String, String> roles){
        String actorData = "";
        Map<String, Object> attributes = new HashMap();

        attributes.put("searchResultsHeader", "<h3>Search Results</h3>");
        actorData += "<ul><li>" + actorName + "</li>";
        for (String role : roles.keySet()){
            actorData += "<ul><li>" + role + "&nbsp <i>" + roles.get(role) + "</i></li>";
        }
        actorData += "</ul>";
        attributes.put("actorData", actorData);

        return attributes;
    }

}
