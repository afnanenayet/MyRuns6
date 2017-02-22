package com.example.aenayet.myapplication.backend;

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

/**
 * Endpoint to delete servlet from datastore
 */
public class DeleteServlet extends HttpServlet {
    Logger logger = Logger.getLogger(DeleteServlet.class.getName());

    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException, ServletException {
        logger.log(Level.INFO, "Received get request");

        Long id = 0L;
        boolean deleteAll = false;
        // Get ID from HTTP request
        try {
            id = Long.getLong(httpServletRequest.getParameter(WebEntry.Properties.idColumn));
            logger.log(Level.INFO, "ID to delete: " + Long.toString(id));
        } catch (NullPointerException e) {
            logger.log(Level.INFO, "No valid id received");
            deleteAll = true;
        }

        // Delete entry with ID from datastore
        EntryDatastore entryDatastore = new EntryDatastore();

        if (!deleteAll) {
            logger.log(Level.INFO, "Deleting an entry");
            entryDatastore.deleteEntry(id);
        } else {
            logger.log(Level.INFO, "Deleting all entries");
            entryDatastore.deleteAllEntries();
        }

        httpServletResponse.sendRedirect("/myrunsappengine.do");
    }

    /**
     * Forward POST request to GET
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }
}
