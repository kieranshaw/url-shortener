package uk.co.kieranshaw.urlshortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.kieranshaw.urlshortener.controller.NotFoundException;
import uk.co.kieranshaw.urlshortener.dao.UrlDao;

@Service
public class UrlServiceImpl implements UrlService {

	private UrlDao dao;

	@Autowired
	public UrlServiceImpl(UrlDao dao) {
		super();
		this.dao = dao;
	}

	@Override
	public String getUrl(String shortCode) throws NotFoundException {
		String url = dao.getUrl(shortCode);
		
		if (url == null) {
			throw new NotFoundException();
		}
		
		return url;
	}

	@Override
	public String storeUrl(String url) {
		
		String shortCode = dao.getShortCodeForUrl(url);
		if (shortCode != null) {
			return shortCode;
		}
		
		shortCode = dao.generateShortCode();
		dao.storeUrl(shortCode, url);
		return shortCode;
	}
	
}
