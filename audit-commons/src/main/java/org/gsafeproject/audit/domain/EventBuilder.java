package org.gsafeproject.audit.domain;

/*
 * #%L
 * audit-commons
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventBuilder {

    private String id;

    private String actor;

    private String action;

    private Map<String, String> resource = new HashMap<String, String>();

    private String description;

    private Map<String, String> metadata = new HashMap<String, String>();

    private Date date = new Date();

    public EventBuilder() {
        // Ok
    }

    public EventBuilder(String actor, String action) {
        actor(actor);
        action(action);
    }

    public EventBuilder(String actor, Action action) {
        actor(actor);
        action(action);
    }

    public EventBuilder actor(String actor) {
        this.actor = actor;
        return this;
    }

    public EventBuilder id(String id) {
        this.id = id;
        return this;
    }

    public EventBuilder action(String action) {
        this.action = action;
        return this;
    }

    public EventBuilder action(Action action) {
        this.action = action.toString();
        return this;
    }

    public EventBuilder does(String action) {
        return action(action);
    }

    public EventBuilder does(Action action) {
        return action(action.toString());
    }

    public EventBuilder resource(String key, String value) {
        resource.put(key, value);
        return this;
    }

    public EventBuilder with(String key, String value) {
        return resource(key, value);
    }

    public EventBuilder on(String key, Object value) {
        return resource(key, value.toString());
    }

    public EventBuilder resource(Map<String, String> resource) {
        if (resource != null) {
            this.resource.putAll(resource);
        }
        return this;
    }

    public EventBuilder description(String description) {
        this.description = description;
        return this;
    }

    public EventBuilder date(Date date) {
        if (null == date) {
            throw new IllegalArgumentException();
        }
        this.date = date;
        return this;
    }

    public EventBuilder metadata(Map<String, String> metadata) {
        this.metadata.putAll(metadata);
        return this;
    }

    public EventBuilder meta(String key, String value) {
        metadata.put(key, value);
        return this;
    }

    public Event build() {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null");
        }
        if (action == null) {
            throw new IllegalArgumentException("action cannot be null");
        }
        return new Event(actor, action, resource)//
                .setDescription(description)//
                .setId(id)//
                .setDate(date).setMetadata(metadata);
    }
}
