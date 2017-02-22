package com.example.aenayet.myapplication.backend;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by aenayet on 2/19/17.
 */

/**
 * A datastore that holds {@link WebEntry} objects in the AppEngine
 */
public class EntryDatastore {
    public Logger logger;
    private DatastoreService datastore;

    public EntryDatastore() {
        logger = Logger.getLogger(WebEntry.class.getName());
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    /**
     * Finds the parent key for the datastore from the Entry
     * @return the parent key as a {@link Key object}
     */
    public Key getParentKey() {
        Key key = KeyFactory.createKey(WebEntry.PARENT_KIND, WebEntry.PARENT_IDENTIFIER);
        return key;
    }

    /**
     * Gets a web entry from the ID in the AppEngine
     * @param id The id of the Entry to retrieve
     * @return The entry corresponding to the ID sent
     */
    public WebEntry getEntryById(long id) {
        Entity entity = null;
        Key key = KeyFactory.createKey(getParentKey(), WebEntry.ENTRY_ENTITY_KIND, id);

        // Retrieve entity
        try {
            entity = datastore.get(key);
        } catch(Exception e) {
            logger.log(Level.INFO, "Entity with key : " + ((Long) id).toString()
                    + " not present in datastore");
        }

        // If entity was able to be retrieved then return, otherwise return null
        if (entity != null) {
            return entityToEntry(entity);
        } else {
            return null;
        }
    }


    /**
     * Converts an {@link Entity} to a {@link WebEntry}
     * @param entity The entity to convert to an entry
     * @return an entry converted from the entity
     */
    private WebEntry entityToEntry(Entity entity) {
        WebEntry entry = new WebEntry();

        // Setting properties for entity
        entry.setId((long) entity.getProperty(WebEntry.Properties.idColumn));
        entry.setActivityType((String) entity.getProperty(WebEntry.Properties.activityTypeColumn));
        entry.setAvgSpeed(((Double) entity.getProperty(WebEntry.Properties.avgSpeedColumn))
                .floatValue());
        entry.setCalories(((Long) entity.getProperty(WebEntry.Properties.caloriesColumn))
                .intValue());
        entry.setClimb(((Double) entity.getProperty(WebEntry.Properties.climbColumn))
                .floatValue());
        entry.setComments((String) entity.getProperty(WebEntry.Properties.commentColumn));
        entry.setDateTime((String) entity.getProperty(WebEntry.Properties.dateTimeColumn));
        entry.setDuration((String) entity.getProperty(WebEntry.Properties.durationColumn));
        entry.setDistance(((Double) entity.getProperty(WebEntry.Properties.distanceColumn))
                .floatValue());
        entry.setInputType((String) entity.getProperty(WebEntry.Properties.inputTypeColumn));
        entry.setHeartRate(((Long) entity.getProperty(WebEntry.Properties.heartRateColumn))
                .intValue());

        return entry;
    }

    /**
     * Converts Entry to an Entity
     * @param entry the entry to convert to an entity
     * @return The converted entity
     */
    private Entity entryToEntity(WebEntry entry) {
        Entity entity = new Entity(WebEntry.ENTRY_ENTITY_KIND, entry.getId(), getParentKey());

        // Setting properties for entity
        entity.setProperty(WebEntry.Properties.idColumn, entry.getId());
        entity.setProperty(WebEntry.Properties.activityTypeColumn, entry.getActivityType());
        entity.setProperty(WebEntry.Properties.avgSpeedColumn, entry.getAvgSpeed());
        entity.setProperty(WebEntry.Properties.caloriesColumn, entry.getCalories());
        entity.setProperty(WebEntry.Properties.climbColumn, entry.getClimb());
        entity.setProperty(WebEntry.Properties.commentColumn, entry.getComments());
        entity.setProperty(WebEntry.Properties.dateTimeColumn, entry.getDateTime());
        entity.setProperty(WebEntry.Properties.durationColumn, entry.getDuration());
        entity.setProperty(WebEntry.Properties.distanceColumn, entry.getDistance());
        entity.setProperty(WebEntry.Properties.inputTypeColumn, entry.getInputType());
        entity.setProperty(WebEntry.Properties.heartRateColumn, entry.getHeartRate());

        return entity;
    }

    /**
     * Adds an entry to the datastore
     * @param entry The entry to the datastore
     * @return if the entry exists
     */
    public boolean addEntryToDatastore(WebEntry entry) {

        // Checks to see if entry is already in the database
        if(getEntryById(entry.getId()) != null) {
            logger.log(Level.INFO, "Entry already exists");
            return false;
        } else {
            // Commit entity to db
            datastore.put(entryToEntity(entry));
            logger.log(Level.INFO, "Inserted entry");
            return true;
        }
    }

    /**
     * Deletes an entry from the datastore
     * @param id the id entry to delete
     * @return whether the entry was able to be deleted
     */
    public boolean deleteEntry(Long id) {
        // Preparing query to find entry to delete
        Query.Filter filter = new Query.FilterPredicate(WebEntry.Properties.idColumn,
                Query.FilterOperator.EQUAL, id);
        Query query = new Query(WebEntry.ENTRY_ENTITY_KIND);
        query.setFilter(filter);
        query.setAncestor(getParentKey());
        PreparedQuery preparedQuery = datastore.prepare(query);
        Entity entity = preparedQuery.asSingleEntity();

        // Checking to see if entity was able to be found. If so, then delete entry, if not,
        // do nothing and return false
        if (entity != null) {
            datastore.delete(entity.getKey());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Deletes every entry in the datastore
     */
    public void deleteAllEntries() {
        // Preparing query to find entry to delete
        Query query = new Query(WebEntry.ENTRY_ENTITY_KIND);
        query.setAncestor(getParentKey());
        PreparedQuery preparedQuery = datastore.prepare(query);
        Iterator<Entity> iter = preparedQuery.asIterator();

        // Looping through all entries and deleting
        while(iter.hasNext()) {
            datastore.delete(iter.next().getKey());
        }
    }

    /**
     * Submits a query for an entry with a particular ID, or retrieves all IDs
     * @param id the ID of a query. Set -1 if you want to query all Entries
     * @return A list of entries which match the parameters of the query
     */
    public ArrayList<WebEntry> query(Long id) {
        ArrayList<WebEntry> list = new ArrayList<>();

        // Query all
        if (id == -1) {
            // Preparing query
            Query query = new Query(WebEntry.ENTRY_ENTITY_KIND);
            query.setFilter(null);
            query.setAncestor(getParentKey());
            PreparedQuery preparedQuery = datastore.prepare(query);
            Iterator<Entity> iter = preparedQuery.asIterator();

            // Iterating through query results and adding query results to return list
            while (iter.hasNext()) {
                Entity entity = iter.next();

                if (entity != null)
                    list.add(entityToEntry(entity));
            }
        }

        // Query for a particular id
        else {
            WebEntry entry = getEntryById(id);

            // If entry exists in database
            if (entry != null)
                list.add(entry);
        }

        return list;
    }
}
