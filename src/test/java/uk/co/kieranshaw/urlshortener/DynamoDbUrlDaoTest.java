package uk.co.kieranshaw.urlshortener;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import uk.co.kieranshaw.urlshortener.dao.DynamoDbUrlDao;

public class DynamoDbUrlDaoTest {

	@Test
	@Ignore
	public void test() throws Exception {
		DynamoDbUrlDao dao = new DynamoDbUrlDao();
		dao.afterPropertiesSet();
		
		String incomingUrl = "http://www.google.com";
		String incomingShortCode = "abcd";
		dao.storeUrl(incomingShortCode, incomingUrl);
		
		String fetchedUrl = dao.getUrl(incomingShortCode);
		assertEquals(incomingUrl, fetchedUrl);
		
		String fetchedShortCode = dao.getShortCodeForUrl(incomingUrl);
		assertEquals(incomingShortCode, fetchedShortCode);
	}
	
	@Test
	@Ignore
	public void testGenerateShortCode() throws Exception {
		DynamoDbUrlDao dao = new DynamoDbUrlDao();
		dao.afterPropertiesSet();
		
		for(int i=0;i<100;i++) {
			String actualShortCode = dao.generateShortCode();
			System.out.println(actualShortCode);
		}
	}

}
