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

import java.text.ParseException;

import org.gsafeproject.audit.domain.Event;
import org.gsafeproject.audit.domain.EventBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

public class TestEventDao_save {

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

        EventDao.init(new MongoDBConfig("localhost", "audit"));
        dao = new EventDao();
    }

    @After
    public void after() {
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }

    @Test
    public void save_shouldPass_ifNullEvents() {
        dao.save((Event) null);
    }

    @Test
    public void save_shouldPass_ifEmptyEvents() {
        Event[] events = new Event[0];
        dao.save(events);
    }

    @Test
    public void shouldSaveEvent() throws ParseException {
        Event event = new EventBuilder()//
                .actor("Toto")//
                .does("DEPOSIT")//
                .on("VAULT", "qwerty13")//
                .on("bucket", "asdfgh13")//
                .description("desciption")//
                .meta("meta1", "value1").meta("meta2", "value2").build();
        String id = dao.save(event)[0].getId();
        event.setId(id);
        assertEquals(event.toString(), dao.load(id).toString());
    }

}
