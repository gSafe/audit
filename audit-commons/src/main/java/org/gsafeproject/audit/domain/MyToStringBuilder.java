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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.builder.ToStringStyle;

public class MyToStringBuilder extends ToStringStyle {

    private static final long serialVersionUID = 1L;

    private static MyToStringBuilder instance;

    private MyToStringBuilder() {
        super();
        setUseClassName(false);
        setUseIdentityHashCode(false);
        setUseFieldNames(false);
        setContentStart("");
        setContentEnd("");
    }

    private Object readResolve() {
        return ToStringStyle.SIMPLE_STYLE;
    }

    @Override
    protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
        Object newValue = value;
        if (value instanceof Date) {
            newValue = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US).format(value);
        }
        buffer.append(newValue);
    }

    public static ToStringStyle getInstance() {
        if (instance == null) {
            instance = new MyToStringBuilder();
        }
        return instance;
    }
}
