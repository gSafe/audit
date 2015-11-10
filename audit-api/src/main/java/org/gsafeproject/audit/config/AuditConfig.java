package org.gsafeproject.audit.config;

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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.gsafeproject.audit.dao.MongoDBConfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class AuditConfig extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    public MongoDBConfig mongoDB;

}
