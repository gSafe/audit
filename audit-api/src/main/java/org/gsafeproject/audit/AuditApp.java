package org.gsafeproject.audit;

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

import java.net.UnknownHostException;

import org.gsafeproject.audit.config.AuditConfig;
import org.gsafeproject.audit.dao.EventDao;
import org.gsafeproject.audit.service.EventService;
import org.gsafeproject.audit.webresource.EventResource;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class AuditApp extends Service<AuditConfig> {
	private Environment env = null;
	
	public static void main(String[] args) throws Exception {
        new AuditApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<AuditConfig> bootstrap) {
        bootstrap.setName("Audit application");
        bootstrap.addBundle(new AssetsBundle("/docs/", "/docs", "index.html"));
    }

    @Override
    public void run(AuditConfig config, Environment environment) throws UnknownHostException {
        this.env = environment;
    	
    	EventDao.init(config.mongoDB);
        EventService eventService = new EventService();
        environment.addResource(new EventResource(eventService));
    }
    
	public void stop() throws Exception{
		if(env != null){
			env.stop();
		}
	}

}
