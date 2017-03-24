package com.pandero.ws.dao.impl;

import java.io.IOException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.pandero.ws.util.ServiceRestTemplate;

@Repository
public class SpringCache {
	private Logger logger = Logger.getLogger(SpringCache.class);
	@Autowired
	private CacheManager cacheManager;

	private static final String CASPIO_KEY_CACHE_NAME = "caspioKey";

	@Cacheable(value = "defaultCacheEmpty")
	public String defaultCache() throws Exception {
		return "";
	}

	@CacheEvict(value = "defaultCacheEmpty", allEntries = true)
	public void resetDefaultCache() {

	}

	public String cacheCaspioServiceKey() throws Exception {
		// Create a singleton CacheManager using defaults CacheManager manager =
		// CacheManager.create();
		if (cacheManager.getCache(CASPIO_KEY_CACHE_NAME) == null) {
			logger.info("creating Dynamic Cache");
			// Create a Cache specifying its configuration.
			Cache dynamicCache = new Cache(new CacheConfiguration(
					CASPIO_KEY_CACHE_NAME, 10000)
					.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
					.eternal(false)
					.timeToLiveSeconds(60*60*24)
					.timeToIdleSeconds(60*60*24)
					.diskExpiryThreadIntervalSeconds(0)
					.persistence(
							new PersistenceConfiguration()
									.strategy(Strategy.LOCALTEMPSWAP)));
			cacheManager.addCache(dynamicCache);

			Cache cache = cacheManager.getCache(CASPIO_KEY_CACHE_NAME);
			String caspioKey = ServiceRestTemplate.obtenerTokenCaspio();
			Element element = new Element(CASPIO_KEY_CACHE_NAME, caspioKey);
			cache.put(element);
			return caspioKey;

		}
		logger.info("get Dynamic Cache");
		Cache cache = cacheManager.getCache(CASPIO_KEY_CACHE_NAME);
		Element element = cache.get(CASPIO_KEY_CACHE_NAME);
		if (element == null) {
			cacheManager.removeCache(CASPIO_KEY_CACHE_NAME);
			return cacheCaspioServiceKey();
		}
		return element.getObjectValue().toString();

	}
	
	public void removeCaspioKeyCache() {

		CacheManager manager = CacheManager.create();
		if (manager.getCache(CASPIO_KEY_CACHE_NAME) != null) {
			manager.removeCache(CASPIO_KEY_CACHE_NAME);
		}

	}
}
