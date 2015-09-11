package com.lsnare.film.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lsnare.film.dao.FilmDAO;
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

    public static String postTest(String filmTitle){
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

    public static List<Film> searchTest(String filmTitle){
        List<Film> films = new ArrayList<>();
        try{
            ApplicationContext context =
                    new ClassPathXmlApplicationContext("Spring-Module.xml");
            FilmDAO filmDAO = (FilmDAO) context.getBean("filmDAO");
            films = filmDAO.selectFilms(filmTitle);
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

        for (Film film : films){
            filmData += "<tr><td>" + film.getIdIMDB() + "</td>"
                    + "<td>" + film.getTitle() + "</td>"
                    + "<td>" + film.getYear() + "</td>"
                    + "<td>" + film.getPlot() + "</td></tr>";
        }
        attributes.put("filmData", filmData);

        return attributes;
    }

}
