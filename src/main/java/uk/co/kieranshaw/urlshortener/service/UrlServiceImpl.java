package uk.co.kieranshaw.urlshortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public String getUrl(String shortCode) {
		return dao.getUrl(shortCode);
	}

	@Override
	public String storeUrl(String url) {
		String shortCode = dao.generateShortCode(url);
		dao.storeUrl(shortCode, url);
		return shortCode;
	}
	
}
