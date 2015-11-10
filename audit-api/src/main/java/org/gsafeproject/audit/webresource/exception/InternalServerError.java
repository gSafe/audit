package org.gsafeproject.audit.webresource.exception;

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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public final class InternalServerError extends WebApplicationException {

    private static final long serialVersionUID = 1L;

    public InternalServerError(String message) {
        this(message, null);
    }

    public InternalServerError(String message, Throwable cause) {
        super(cause, Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
