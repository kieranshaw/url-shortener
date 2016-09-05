package uk.co.kieranshaw.urlshortener.controller;


import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import uk.co.kieranshaw.urlshortener.service.UrlService;

@Controller
public class UrlController {

	private UrlService service;

	@Autowired
	public UrlController(UrlService service) {
		this.service = service;
	}

	@GetMapping("/")
	@ResponseBody
	public String shorten(@RequestParam(name = "shorten") String url, UriComponentsBuilder uriBuilder) throws InvalidUrlException {
		
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			throw new InvalidUrlException();
		}
		
		String shortCode = service.storeUrl(url);
		String shortCodeUrl = uriBuilder.replacePath(shortCode).replaceQuery("").build().toString();
		return shortCodeUrl;
	}

	@GetMapping("/{shortCode}")
	@ResponseStatus(code = HttpStatus.TEMPORARY_REDIRECT)
	public ModelAndView redirectToUrl(@PathVariable(value = "shortCode") String shortCode) throws NotFoundException {
		String url = service.getUrl(shortCode);
		return new ModelAndView("redirect:" + url);
	}

	@GetMapping("/{shortCode}+")
	@ResponseBody
	public String getUrl(@PathVariable(value = "shortCode") String shortCode) throws NotFoundException {
		return service.getUrl(shortCode);
	}

}