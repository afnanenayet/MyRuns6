package com.example.aenayet.myapplication.backend;

/**
 * Created by aenayet on 2/20/17.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that serves the HTML app engine
 */
public class MyRunsAppEngineServlet extends HttpServlet {

    // Serves webpage with all Entry data
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException{

        // Getting list of {@link WebEntry} objects
        EntryDatastore entryDatastore = new EntryDatastore();
        ArrayList<WebEntry> result = entryDatastore.query(-1L);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Write top of webpage
        out.write("<html>\n" +
                "<head>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n" +
                "<title>MyRuns Web View</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>MyRuns Entries</h1>\n");

        // Write header for table
        out.write("<center>\n" +
                "<table>\n" +
                "<tr>\n" +
                    "<th>ID</th>" +
                "<th>Input Type</th>" +
                "<th>Activity Type</th>" +
                "<th>Date Time</th>" +
                "<th>Duration</th>" +
                "<th>Distance</th>" +
                "<th>Average Speed</th>" +
                "<th>Calories</th>" +
                "<th>Climb</th>" +
                "<th>Heart Rate</th>" +
                "<th>Comments</th>\n" +
                "<th></th>\n" +
                "</tr>\n");

        if(result != null){
            for(int i=0; i<result.size(); i++) {
                WebEntry entry = result.get(i);

                // Write table row with information from entry
                out.write("<tr>\n" +
                        "<td>" + Long.toString(entry.getId()) + "</td>\n" +
                        "<td>" + entry.getInputType() + "</td>\n" +
                        "<td>" + entry.getActivityType() + "</td>\n" +
                        "<td>" + entry.getDateTime() + "</td>\n" +
                        "<td>" + entry.getDuration() + "</td>\n" +
                        "<td>" + entry.getDistance() + " miles</td>\n" +
                        "<td>" + entry.getAvgSpeed() + " mph</td>\n" +
                        "<td>" + entry.getCalories() + "</td>\n" +
                        "<td>" + entry.getClimb() + " feet</td>\n" +
                        "<td>" + entry.getHeartRate() + " bpm</td>\n" +
                        "<td>" + entry.getComments() + "</td>\n" +
                        "<td>" + "<input type=\"button\" onclick=\"location.href='/senddeletemessage.do?"
                        + WebEntry.Properties.idColumn + "=" + Long.toString(entry.getId()) +
                                "'\" value= \"Delete \"> </td>\n" +
                        "</tr>"
                );
            }
        }

        out.write("</table></body></html>");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }
}
