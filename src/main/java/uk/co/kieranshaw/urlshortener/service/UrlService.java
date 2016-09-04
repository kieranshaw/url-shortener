package uk.co.kieranshaw.urlshortener.service;

import uk.co.kieranshaw.urlshortener.controller.NotFoundException;

public interface UrlService {

	String getUrl(String shortCode) throws NotFoundException;
	String storeUrl(String url);
}
