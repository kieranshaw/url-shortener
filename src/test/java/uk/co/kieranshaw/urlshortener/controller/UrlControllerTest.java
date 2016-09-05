package uk.co.kieranshaw.urlshortener.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import uk.co.kieranshaw.urlshortener.service.UrlService;

@RunWith(MockitoJUnitRunner.class)
public class UrlControllerTest {

	private static final String SHORT_CODE = "abcd";
	private static final String URL = "http://www.kieranshaw.co.uk";
	@Mock
	private UrlService service;
	private UrlController controller;

	@Before
	public void setUp() {
		controller = new UrlController(service);
	}

	@Test
	public void getUrl_withValidShortCode_ReturnsUrl() throws Exception {
		when(service.getUrl(SHORT_CODE)).thenReturn(URL);
		String url = controller.getUrl(SHORT_CODE);
		assertEquals(URL, url);
	}

	@Test
	public void shorten_withValidUrl_ReturnsUrl() throws Exception {
		when(service.storeUrl(URL)).thenReturn(SHORT_CODE);
		UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString("http://localhost/?shorten=");
		String shortCodeUrl = controller.shorten(URL, uriComponents);
		assertEquals("http://localhost/" + SHORT_CODE, shortCodeUrl);
	}
	
	@Test(expected = InvalidUrlException.class)
	public void shorten_withInvalidUrl_ThrowsException() throws Exception {
		UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString("http://localhost/?shorten=");
		controller.shorten("bad url", uriComponents);
	}

	@Test
	public void redirectToUrl_withValidShortCode_ReturnsUrl() throws Exception {
		when(service.getUrl(SHORT_CODE)).thenReturn(URL);
		ModelAndView modelAndView = controller.redirectToUrl(SHORT_CODE);
		assertEquals("redirect:" + URL, modelAndView.getViewName());
	}

}
