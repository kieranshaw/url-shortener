package uk.co.kieranshaw.urlshortener.service;

public interface UrlService {

	String getUrl(String shortCode);
	String storeUrl(String url);
}
