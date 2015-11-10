package org.gsafeproject.audit.client;

/*
 * #%L
 * audit-client
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

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gsafeproject.audit.domain.Event;
import org.gsafeproject.audit.domain.EventBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.http.HTTPContainerFactory;

public class TestAuditClient {

    protected JerseyTest jersey;
    protected final static String SERVICE_URL = "http://localhost:9998";
    private AuditClient client;

    private final static Logger COM_SUN_JERSEY_LOGGER = Logger.getLogger("com.sun.jersey");
    static {
        COM_SUN_JERSEY_LOGGER.setLevel(Level.WARNING);
    }

    @Before
    public void beforeClass() throws Exception {
        jersey = new JerseyTest(new HTTPContainerFactory()) {
            @Override
            protected AppDescriptor configure() {
                return new WebAppDescriptor.Builder("org.gsafeproject.audit.client").servletClass(com.sun.jersey.spi.container.servlet.ServletContainer.class)
                        .initParam("com.sun.jersey.api.json.POJOMappingFeature", "true").contextPath("/").build();
            }
        };
        jersey.setUp();
        client = Mockito.mock(AuditClient.class);
    }

    @After
    public void after() throws Exception {
        jersey.tearDown();
    }

    @Test
    public void shouldCreateAuditClient() throws MalformedURLException {
        new AuditClient("http://novapost.fr");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldRaiseException_ifMalformedUrl() throws MalformedURLException {
        new AuditClient("invalid url");
    }

    @Test
    public void shouldNotRaiseAnException_IfServiceIsNotActive() throws MalformedURLException {
        new AuditClient("invalid url", false).postEvents((Event) null);
    }

    @Test
    public void shouldNotRaiseAnException_IfNoEventToPost() {
        client.postEvents((Event) null);
    }

    @Test
    public void shouldNotPostEvent_ifInactive() {
        Event event = new EventBuilder("actor", "action").build();
        Mockito.when(client.isActivate()).thenReturn(false);
        new AuditClient("", false).postEvents(event);
    }

    @Test
    public void shouldPostEvent() {
        Event event = new EventBuilder("actor", "action").build();
        AuditClient client = new AuditClient(SERVICE_URL, true);
        assertEquals(client, client.postEvents(event));
    }

    @Test
    public void shouldNotPostNullEvents() {
        AuditClient client = new AuditClient(SERVICE_URL, true);
        Event[] events = null;
        assertEquals(client, client.postEvents(events));
    }

    @Test
    public void shouldNotPostEmptyEvents() {
        AuditClient client = new AuditClient(SERVICE_URL, true);
        Event[] events = new Event[0];
        assertEquals(client, client.postEvents(events));
    }

    @Test
    public void shouldNotRaiseException_IfNotSent() {
        Client client = Mockito.mock(Client.class);
        Mockito.when(client.resource(Mockito.anyString())).thenThrow(new ClientHandlerException());
        AuditClient auditClient = new AuditClient("http://novapost.fr");
        auditClient.client = client;
        auditClient.postEvents(new EventBuilder("actor", "action").build());
    }

}
