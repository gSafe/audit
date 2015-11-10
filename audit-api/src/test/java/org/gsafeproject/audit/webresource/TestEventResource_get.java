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
import javax.ws.rs.core.MultivaluedMap;

import org.gsafeproject.audit.service.EventService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.yammer.dropwizard.testing.ResourceTest;

public class TestEventResource_get extends ResourceTest {

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
    public void badRequest_IfAllparametersAreMissing() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        ClientResponse response = client().resource("http://localhost:9998/events/").queryParams(params).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertEquals(400, response.getStatus());
        assertEquals("One query param must be filled!", response.getEntity(String.class));
    }

    @Test
    public void badRequest_IfDatesIsEmpty() {
        ClientResponse response = client().resource("http://localhost:9998/events/")//
                .queryParam("actor", "Anne Onyme")//
                .queryParam("date_from", "")//
                .type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void badRequest_IfDatesIsNotValid() {
        ClientResponse response = client().resource("http://localhost:9998/events/")//
                .queryParam("actor", "Anne Onyme")//
                .queryParam("date_from", "invalid")//
                .type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertEquals(400, response.getStatus());
        assertEquals("invalid is not a valid date! (YYYY/MM/dd)", response.getEntity(String.class));
        response = client().resource("http://localhost:9998/events/")//
                .queryParam("actor", "Anne Onyme")//
                .queryParam("date_to", "invalid")//
                .type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void notFound_IfPageIsNAN() {
        ClientResponse response = client().resource("http://localhost:9998/events/")//
                .queryParam("page", "quatre")//
                .queryParam("actor", "Anne Onyme")//
                .type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void shouldSearch() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        ClientResponse response = client().resource("http://localhost:9998/events/")//
                .queryParam("page", "2")//
                .queryParam("actor", "Anne Onyme")//
                .queryParam("date_from", "2012/12/30")//
                .queryParam("date_to", "2013/12/30")//
                .type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockService).search(argument.capture(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
        assertEquals("Anne Onyme", argument.getValue());
    }
}
