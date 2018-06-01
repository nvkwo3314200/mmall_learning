package com.mmall.common;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class TokenCache {
	 private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

	 public static final String TOKEN_PREFIX = "token_";
	 
	 private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder()
			 .initialCapacity(1000) // 初始化容器
			 .maximumSize(10000) // 最大容器，满了之后会用LRU算法，清除不常用的数据
			 .expireAfterAccess(30, TimeUnit.MINUTES)
			 .build(new CacheLoader<String, String>(){
				 //默认的数据加载实现,当调用get取值的时候,如果key没有对应的值,就调用这个方法进行加载.
				@Override
				public String load(String arg0) throws Exception {
					return "null";
				}
			 });
	 
	 public static void setKey(String key, String value) {
		 localCache.put(key, value);
	 }
	 
	 public static String getKey(String key) {
		 String value = null;
		 try {
			value = localCache.get(key);
			if("null".equals(value)) value = null;
		} catch (ExecutionException e) {
			logger.error(e.getMessage());
		}
		 return value;
	 }
}
