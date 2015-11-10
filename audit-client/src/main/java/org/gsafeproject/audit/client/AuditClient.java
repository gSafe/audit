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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.gsafeproject.audit.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.impl.provider.entity.StringProvider;

public class AuditClient {

    private static final Logger LOGGER = LoggerFactory.getLogger("AuditClient");
    protected Client client;
    private String serviceUrl;
    private boolean activate;

    public AuditClient(String url) {
        this(url, true);
    }

    public AuditClient(String url, boolean activate) {
        if (!activate) {
            return;
        }
        validUri(url);

        serviceUrl = url;
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(StringProvider.class);
        config.getClasses().add(JacksonJsonProvider.class);
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        client = Client.create(config);
        this.activate = true;
    }

    private boolean validUri(String uri) {
        try {
            new URL(uri);
            return true;
        } catch (MalformedURLException e) {
            LOGGER.error("Uri is not valid: " + uri, e);
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public AuditClient postEvents(Event... events) {
        if (events != null) {
            postEvents(Arrays.asList(events));
        }
        return this;
    }

    public AuditClient postEvents(List<Event> events) {
        if (!isActivate()) {
            return this;
        }
        if (events == null || events.isEmpty()) {
            return this;
        }
        try {
            ClientResponse response = client.resource(serviceUrl).path("/events")
                    .type(MediaType.APPLICATION_JSON)//
                    .post(ClientResponse.class, events);
            if (ClientResponse.Status.CREATED.getStatusCode() != response.getStatus()) {
                LOGGER.error("Post event fails, http code: " + response.getStatus());
            }
        } catch (ClientHandlerException e) {
            LOGGER.error("Uri is not valid or service unreachable: " + serviceUrl, e);
        }
        return this;
    }

    public boolean isActivate() {
        return activate;
    }

}
