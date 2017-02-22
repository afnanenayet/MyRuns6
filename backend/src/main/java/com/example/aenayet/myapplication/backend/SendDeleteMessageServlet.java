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
 * Servlet that sends a deletion request to a device
 */
public class SendDeleteMessageServlet extends HttpServlet {
    Logger logger = Logger.getLogger(SendDeleteMessageServlet.class.getName());

    /**
     * Sends a GCM message to device with the ID of the entry to be deleted
     * @param id the ID of the entry to be deleted
     */
     private void deleteEntry(long id) {
        MessagingEndpoint messagingEndpoint = new MessagingEndpoint();

        // Attempting to send ID of entry to be deleted to device through GCM
        try {
            String idString = Long.toString(id);
            messagingEndpoint.sendMessage(idString);
            logger.log(Level.INFO, "Sent id " + idString + " to device");

            // Delete entry with ID from datastore
            EntryDatastore entryDatastore = new EntryDatastore();
            entryDatastore.deleteEntry(id);
        } catch (IOException e) {
           e.printStackTrace();
        }
    }

    /**
     * Takes an ID and then sends a message to device prompting for deletion of entry with that id
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idString = req.getParameter(WebEntry.Properties.idColumn);

        // Sending delete entry message with GCM
        deleteEntry(Long.parseLong(idString));

        // Redirect/refresh main page
        resp.sendRedirect("/myrunsappengine.do");
    }

    /**
     * Redirects POST request to GET request
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
