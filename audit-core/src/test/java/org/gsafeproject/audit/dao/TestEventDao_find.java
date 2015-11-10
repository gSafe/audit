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

import static org.junit.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.gsafeproject.audit.domain.EventBuilder;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

public class TestEventDao_find {

    private EventDao dao;
    private MongodExecutable mongodExecutable;

    @Before
    public void before() throws Exception {
        IMongodConfig mongodConfig = new MongodConfigBuilder()//
                .version(Version.Main.PRODUCTION)//
                .net(new Net(27017, Network.localhostIsIPv6())).build();
        MongodStarter runtime = MongodStarter.getDefaultInstance();
        mongodExecutable = runtime.prepare(mongodConfig);
        mongodExecutable.start();

        MongoDBConfig config = new MongoDBConfig("localhost", "audit");
        EventDao.init(config);
        dao = new EventDao();

        populate(config);
    }

    @After
    public void after() {
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }

    @Test
    public void shoulFindAll() {
        assertEquals(6, dao.find(null, null, null, null).size());
    }

    @Test
    public void shoulFindByActor() {
        assertEquals(2, dao.find("Tyrion").size());
        assertEquals(0, dao.find("Ser Jaime Lannister").size());
    }

    @Test
    public void shoulFindByAction() {
        assertEquals(2, dao.find(null, "live").size());
        assertEquals(0, dao.find(null, "knit").size());
    }

    @Test
    public void shoulFindByResource() {
        Map<String, String> resources = new HashMap<String, String>();
        resources.put("what", "a dwarf");
        assertEquals(2, dao.find(null, null, resources).size());

        resources.clear();
        resources.put("at", "Fort Boyard");
        resources.put("kingdom", "Charente Maritime");
        assertEquals(1, dao.find(null, null, resources).size());

        resources.clear();
        resources.put("at", "Fort Alamo");
        assertEquals(0, dao.find(null, null, resources).size());
    }

    @Test
    public void shoulFindByDates() {
        Date from = new DateTime(2005, 3, 5, 12, 0, 0, 0).toDate();
        Date to = new DateTime(2005, 3, 6, 12, 0, 0, 0).toDate();
        assertEquals(2, dao.find(null, null, null, from).size());
        assertEquals(5, dao.find(null, null, null, null, to).size());
        assertEquals(1, dao.find(null, null, null, from, to).size());
    }

    private void populate(MongoDBConfig config) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(config.host);
        DB db = mongoClient.getDB(config.dbName);
        DBCollection coll = db.getCollection(EventDao.COLLECTION_NAME);
        coll.remove(new BasicDBObject());

        dao.save(new EventBuilder().actor("Tyrion").action("is").resource("what", "a Lannister")
                .date(new DateTime(2005, 3, 1, 12, 0, 0, 0).toDate()).build());
        dao.save(new EventBuilder().actor("Cersei").action("is").resource("what", "a Lannister")
                .date(new DateTime(2005, 3, 2, 12, 0, 0, 0).toDate()).build());
        dao.save(new EventBuilder().actor("Tyrion").action("is").resource("what", "a dwarf")
                .date(new DateTime(2005, 3, 3, 12, 0, 0, 0).toDate()).build());
        dao.save(new EventBuilder().actor("Passe-Partout").action("is").resource("what", "a dwarf")
                .date(new DateTime(2005, 3, 4, 12, 0, 0, 0).toDate()).build());
        dao.save(new EventBuilder().actor("Cersei").action("live").resource("at", "Castral Roc")
                .date(new DateTime(2005, 3, 5, 12, 0, 0, 0).toDate()).build());
        dao.save(new EventBuilder().actor("Passe-Partout").action("live")
                .resource("at", "Fort Boyard").date(new DateTime(2005, 3, 6, 12, 0, 0, 0).toDate())
                .resource("kingdom", "Charente Maritime").build());
    }
}
