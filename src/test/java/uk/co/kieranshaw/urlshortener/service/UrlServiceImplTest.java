package uk.co.kieranshaw.urlshortener.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.kieranshaw.urlshortener.dao.UrlDao;

@RunWith(MockitoJUnitRunner.class)
public class UrlServiceImplTest {

	private static final String SHORT_CODE = "abcd";
	private static final String URL = "http://www.kieranshaw.co.uk";
	@Mock
	private UrlDao dao;

	private UrlServiceImpl service;

	@Before
	public void setUp() {
		service = new UrlServiceImpl(dao);
	}

	@Test
	public void getUrl_withValidShortCode_ReturnsUrl() throws Exception {
		when(dao.getUrl(SHORT_CODE)).thenReturn(URL);
		String url = service.getUrl(SHORT_CODE);
		assertEquals(URL, url);
	}

	@Test
	public void storeUrl_withValidNewUrl_ReturnsShortCode() throws Exception {
		when(dao.getShortCodeForUrl(URL)).thenReturn(null);
		when(dao.generateShortCode()).thenReturn(SHORT_CODE);

		String shortCode = service.storeUrl(URL);
		assertEquals(SHORT_CODE, shortCode);

		verify(dao).storeUrl(SHORT_CODE, URL);
	}
	@Test
	public void storeUrl_withValidExistingUrl_ReturnsShortCode() throws Exception {
		when(dao.getShortCodeForUrl(URL)).thenReturn(SHORT_CODE);
		
		String shortCode = service.storeUrl(URL);
		assertEquals(SHORT_CODE, shortCode);
		
		verify(dao, Mockito.never()).generateShortCode();
		verify(dao, Mockito.never()).storeUrl(Mockito.anyString(), Mockito.anyString());
	}

}
