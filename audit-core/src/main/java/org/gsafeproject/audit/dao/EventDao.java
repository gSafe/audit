package org.gsafeproject.audit.dao;

/*
 * #%L
 * audit-core
 * %%
 * Copyright (C) 2013 gSafe
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.bson.types.ObjectId;
import org.gsafeproject.audit.domain.Event;
import org.gsafeproject.audit.domain.EventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class EventDao {

    public static final String COLLECTION_NAME = "event";
    private static final Logger LOGGER = LoggerFactory.getLogger("EventDao");

    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");
    private static MongoDBConfig config;
    private static MongoClient mongoClient;
    private static boolean initialized;

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

    private DB db;

    public static void init(MongoDBConfig config) throws UnknownHostException {
        EventDao.config = config;
        EventDao.mongoClient = new MongoClient(config.host);
        initialized = true;
    }

    public EventDao() {
        if (!initialized) {
            throw new IllegalStateException("EventDao has not been initialized!");
        }
        dateFormat.setTimeZone(TIME_ZONE);
        db = mongoClient.getDB(config.dbName);
    }

    public Event[] save(Event... events) {
        if (events == null || events.length <= 0) {
            return events;
        }

        for (Event event : events) {
            if (event == null) {
                continue;
            }
            DBCollection coll = db.getCollection(COLLECTION_NAME);
            BasicDBObject doc = new BasicDBObject("action", event.getAction())//
                    .append("actor", event.getActor())//
                    .append("timestamp", event.getDate())//
                    .append("resource", event.getResource())//
                    .append("description", event.getDescription())//
                    .append("metadata", event.getMetadata());
            coll.insert(doc);
            ObjectId id = (ObjectId) doc.get("_id");
            event.setId(id.toString());
        }
        return events;
    }

    public Event load(String id) throws ParseException {
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
        DBCollection coll = db.getCollection(COLLECTION_NAME);
        DBObject finded = coll.findOne(query);
        return buildEvent(finded);
    }

    public List<Event> find(String actor) {
        return find(actor, null, null, null);
    }

    public List<Event> find(String actor, String action) {
        return find(actor, action, null, null);
    }

    public List<Event> find(String actor, String action, Map<String, String> resources) {
        return find(actor, action, resources, null);
    }

    public List<Event> find(String actor, String action, Map<String, String> resources, Date from) {
        return find(actor, action, resources, from, null);
    }

    public List<Event> find(String actor, String action, Map<String, String> resources, Date from, Date to) {
        List<Event> events = new LinkedList<Event>();
        DBCollection coll = db.getCollection(COLLECTION_NAME);

        BasicDBObject query = new QueryBuilder()//
                .addCriterion("actor", actor)//
                .addCriterion("action", action)//
                .addCriterion("resource", resources)//
                .addBetween("timestamp", from, to).build();

        try (DBCursor cursor = coll.find(query);) {
            while (cursor.hasNext()) {
                try {
                    events.add(buildEvent(cursor.next()));
                } catch (ParseException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return events;
    }

    private Event buildEvent(DBObject finded) throws ParseException {
        return new EventBuilder()//
                .id(finded.get("_id").toString())//
                .action((String) finded.get("action"))//
                .actor((String) finded.get("actor"))//
                .date((Date) finded.get("timestamp"))//
                .description((String) finded.get("description"))//
                .resource((Map<String, String>) finded.get("resource"))//
                .metadata((Map<String, String>) finded.get("metadata"))//
                .build();
    }
}
