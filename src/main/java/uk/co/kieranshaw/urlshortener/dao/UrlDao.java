package uk.co.kieranshaw.urlshortener.dao;

public interface UrlDao {

	String getUrl(String shortCode);

	void storeUrl(String shortCode, String url);

	String generateShortCode();

	String getShortCodeForUrl(String url);

}
