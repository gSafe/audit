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

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {

    @JsonIgnore
    private String id;

    @JsonProperty("actor")
    private String actor;

    @JsonProperty("action")
    private String action;

    @JsonProperty("resource")
    private Map<String, String> resource;

    @JsonProperty("description")
    public String description;

    @JsonProperty("metadata")
    private Map<String, String> metadata = new HashMap<String, String>();

    @JsonIgnore
    private Date date = new Date();

    private Event() {
        // Jackson usage
    }

    protected Event(String actor, String action, Map<String, String> resource) {
        this.actor = actor;
        this.action = action;
        this.resource = resource;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonIgnore
    public String getActor() {
        return actor;
    }

    @JsonIgnore
    public String getAction() {
        return action;
    }

    @JsonIgnore
    public Map<String, String> getResource() {
        return resource;
    }

    @JsonIgnore
    public Date getDate() {
        return date;
    }

    @JsonIgnore
    public Map<String, String> getMetadata() {
        return metadata;
    }

    @JsonIgnore
    protected Event setDate(Date date) {
        this.date = date;
        return this;
    }

    @JsonIgnore
    protected Event setDescription(String description) {
        this.description = description;
        return this;
    }

    @JsonIgnore
    public String getDescription() {
        return description;
    }

    @JsonIgnore
    public Event setId(String id) {
        this.id = id;
        return this;
    }

    @JsonIgnore
    public Event setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MyToStringBuilder.getInstance());
    }

}
