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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gsafeproject.audit.domain.Event;
import org.gsafeproject.audit.service.EventService;
import org.gsafeproject.audit.webresource.exception.BadRequest;

import com.google.common.base.Strings;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {

    private EventService service;

    public EventResource(EventService service) {
        this.service = service;
    }

    @GET
    @Path("info")
    public String info() {
        return "Event resource";
    }

    @POST
    public Response post(Event... events) {
        checkEvent(events);
        service.save(events);
        return Response.status(201).type(MediaType.TEXT_PLAIN).build();
    }

    @GET
    public List<Event> search(@QueryParam("actor") String actor,
            @QueryParam("action") String action, @QueryParam("resource") String resource,
            @QueryParam("date_from") String dateFrom, @QueryParam("date_to") String dateTo,
            @DefaultValue("1") @QueryParam("page") int page) {
        if (Strings.isNullOrEmpty(actor) && Strings.isNullOrEmpty(action)
                && Strings.isNullOrEmpty(resource) && Strings.isNullOrEmpty(dateFrom)
                && Strings.isNullOrEmpty(dateTo)) {
            throw new BadRequest("One query param must be filled!");
        }
        checkDate(dateFrom);
        checkDate(dateTo);
        return service.search(actor, action, resource, page);
    }

    private void checkEvent(Event... events) {
        for (Event event : events) {
            if (Strings.isNullOrEmpty(event.getActor())) {
                throw new BadRequest("actor param is a madatory field!");
            }
            if (Strings.isNullOrEmpty(event.getAction())) {
                throw new BadRequest("action param is a madatory field!");
            }
            checkResource(event.getResource());
        }
    }

    private void checkResource(Map<String, String> resource) {
        if (null == resource || resource.isEmpty()) {
            throw new BadRequest("resource param is a madatory field!");
        }
        for (Entry<String, String> entry : resource.entrySet()) {
            if (Strings.isNullOrEmpty(entry.getKey())) {
                throw new BadRequest("resource key param is a madatory filed!");
            }
            if (Strings.isNullOrEmpty(entry.getValue())) {
                throw new BadRequest("resource value param is a madatory filed!");
            }
        }
    }

    private void checkDate(String date) {
        if (!Strings.isNullOrEmpty(date)) {
            try {
                new SimpleDateFormat("YYYY/MM/dd", Locale.US).parse(date);
            } catch (ParseException e) {
                throw new BadRequest(date + " is not a valid date! (YYYY/MM/dd)");
            }
        }
    }
}
