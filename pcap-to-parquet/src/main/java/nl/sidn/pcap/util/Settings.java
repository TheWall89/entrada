/*
 * ENTRADA, a big data platform for network data analytics
 *
 * Copyright (C) 2016 SIDN [https://www.sidn.nl]
 * 
 * This file is part of ENTRADA.
 * 
 * ENTRADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ENTRADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ENTRADA.  If not, see [<http://www.gnu.org/licenses/].
 *
 */	
package nl.sidn.pcap.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for reading the entrada config file and making the
 * key-value pairs available.
 *
 */
public class Settings {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);

	public static String INPUT_LOCATION = "input.location";
	public static String OUTPUT_LOCATION = "output.location";
	public static String STATE_LOCATION = "state.location";
	public static String OUTPUT_MAX_PACKETS = "output.max.packets";
	public static String CACHE_TIMEOUT = "cache.timeout";
	
	public static String METRICS_EXCHANGE = "metrics.exchange";
	public static String METRICS_QUEUE = "metrics.queue";
	public static String METRICS_USERNAME = "metrics.username";
	public static String METRICS_PASSWORD = "metrics.password";
	public static String METRICS_VIRTUALHOST = "metrics.virtualhost";
	public static String METRICS_HOST = "metrics.host";
	public static String METRICS_TIMEOUT = "metrics.timeout";
	
	public static String RESOLVER_LIST_GOOGLE = "resolver.list.google";
	public static String RESOLVER_LIST_OPENDNS = "resolver.list.opendns";
	
	private static Properties props = null;
	private static Settings _instance = null;
	
	private static String path = null;
	private String name = null;
	
	private Settings(String path){
		init(path);
	}
	
	public static Settings getInstance(){
		if(_instance == null){
			_instance = new Settings(path);
		}
		return _instance;
	}
	
	public static void init(String path) {
		 
		props = new Properties();
		InputStream input = null;
	 
		try {
	 
			input = new FileInputStream(path);
			props.load(input);
	 
		} catch (IOException e) {
			throw new RuntimeException("Could not load settings", e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					//ignore exception while closing
					LOGGER.error("Could not close settings",e );
				}
			}
		}
	 
	  }
	
	public String getSetting(String key){
		return props.getProperty(key);
	}

	public void setSetting(String key, String value){
		props.setProperty(key, value);
	}
	
	public int getIntSetting(String key){
		try {
			return Integer.parseInt(props.getProperty(key));
		} catch (NumberFormatException e) {
			throw new RuntimeException("Value " + props.getProperty(key) + " for " + key + " is not a valid number", e);
		}
	}
	
	public static void setPath(String settingFilePath){
		path = settingFilePath;
		_instance = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	
	
}
