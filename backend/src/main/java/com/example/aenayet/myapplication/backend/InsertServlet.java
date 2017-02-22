package com.example.aenayet.myapplication.backend;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by aenayet on 2/20/17.
 */

public class InsertServlet extends HttpServlet {
    private Logger logger = Logger.getLogger(WebEntry.class.getName());

    /**
     * Inserts an Entry from a JSON object
     */
    @Override
    public void doGet(HttpServletRequest httpServletRequest,
                      HttpServletResponse httpServletResponse)
            throws IOException, ServletException{
        // Insertions are done through post, forward request
        doPost(httpServletRequest, httpServletResponse);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
            IOException, ServletException {
        // Parsing string from post
        String jsonString = request.getParameter(JsonUtil.JSON_STRING_REQUEST_KEY);
        logger.log(Level.INFO, "JSON string: " + jsonString);

        // Posting entry to datastore
        try {
            // Parsing JSON object from request
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
            WebEntry entry = JsonUtil.jsonToEntry(jsonObject);

            // Adding entry to the cloud
            EntryDatastore entryDatastore = new EntryDatastore();
            entryDatastore.addEntryToDatastore(entry);
            response.sendRedirect("/myrunsappengine.do");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
