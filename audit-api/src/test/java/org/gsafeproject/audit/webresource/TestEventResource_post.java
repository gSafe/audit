package org.gsafeproject.audit.webresource;

/*
 * #%L
 * audit-api
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

import javax.ws.rs.core.MediaType;

import org.gsafeproject.audit.domain.Event;
import org.gsafeproject.audit.service.EventService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.yammer.dropwizard.testing.ResourceTest;

public class TestEventResource_post extends ResourceTest {

    private EventService mockService;

    @Override
    protected void setUpResources() throws Exception {
        mockService = Mockito.mock(EventService.class);
        addResource(new EventResource(mockService));
    }

    protected WebResource.Builder resource() {
        return client().resource("http://localhost:9998/events/").type(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testGETInfo() {
        String info = client().resource("http://localhost:9998/events/info").get(String.class);
        assertEquals("Event resource", info);
    }

    @Test
    public void badRequest_IfUserIsMissing() {
        String request = "[{\"action\":\"action\", \"resource\": {\"coffre\": \"mon_coffre\", \"container\": \"id_du_container\"}}]";
        ClientResponse response = resource().post(ClientResponse.class, request);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void badRequest_IfActionIsMissing() {
        String request = "[{\"actor\":\"user\", \"resource\": {\"coffre\": \"mon_coffre\", \"container\": \"id_du_container\"}}]";
        ClientResponse response = resource().post(ClientResponse.class, request);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void badRequest_IfResourceIsMissing() {
        String request = "[{\"actor\":\"user\", \"action\": \"action\"}]";
        ClientResponse response = resource().post(ClientResponse.class, request);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void badRequest_IfResourceIsEmpty() {
        String request = "[{\"actor\":\"user\", \"action\": \"action\", \"resource\": {}}]";
        ClientResponse response = resource().post(ClientResponse.class, request);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void badRequest_IfResourceKeyIsMissing() {
        String request = "[{\"actor\":\"user\", \"action\": \"action\", \"resource\": {\"\": \"mon_coffre\", \"container\": \"id_du_container\"}}]";
        ClientResponse response = resource().post(ClientResponse.class, request);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void badRequest_IfResourceValueIsMissing() {
        String request = "[{\"actor\":\"user\", \"action\": \"action\", \"resource\": {\"coffre\": \"mon_coffre\", \"container\": \"\"}}]";
        ClientResponse response = resource().post(ClientResponse.class, request);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void shouldPost() {
        String request = "[{\"actor\":\"Bob\", \"action\": \"download\", \"description\": \"description de l'événement\", \"resource\": {\"coffre\": \"mon_coffre\", \"container\": \"id_du_container\"}, \"metadata\": {\"meta1\": \"value1\", \"meta2\": \"value2\"}}]";
        ClientResponse response = resource().post(ClientResponse.class, request);
        assertEquals(201, response.getStatus());
        ArgumentCaptor<Event> argument = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(mockService).save(argument.capture());
        assertEquals("Bob", argument.getValue().getActor());
        assertEquals("download", argument.getValue().getAction());
        assertEquals("mon_coffre", argument.getValue().getResource().get("coffre"));
        assertEquals("id_du_container", argument.getValue().getResource().get("container"));
        assertEquals("description de l'événement", argument.getValue().getDescription());
        assertEquals("value1", argument.getValue().getMetadata().get("meta1"));
        assertEquals("value2", argument.getValue().getMetadata().get("meta2"));
    }
}
