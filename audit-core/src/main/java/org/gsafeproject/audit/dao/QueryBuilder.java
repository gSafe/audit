package org.gsafeproject.audit.dao;

/*
 * #%L
 * audit-core
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

import com.mongodb.BasicDBObject;

public class QueryBuilder {

    private Map<String, Object> queryParam = new HashMap<String, Object>();

    public QueryBuilder addCriterion(String key, Object value) {
        if (value != null) {
            queryParam.put(key, value);
        }
        return this;
    }

    public QueryBuilder addCriterion(String key, Map<String, Object> map) {
        if (map != null) {
            for (String mapKey : map.keySet()) {
                addCriterion(key + "." + mapKey, map.get(mapKey));
            }
        }
        return this;
    }

    public BasicDBObject build() {
        return new BasicDBObject(queryParam);
    }

    public QueryBuilder addBetween(String string, Date from, Date to) {
        if (to == null && from == null) {
            return this;
        }

        if (to == null) {
            to = new Date();
        }
        BasicDBObject period = new BasicDBObject("$lt", to);
        if (from != null) {
            period.append("$gte", from);
        }
        queryParam.put("timestamp", period);
        return this;
    }

    @Override
    public String toString() {
        BasicDBObject result = new BasicDBObject(queryParam);
        return result.toString();
    }

}
