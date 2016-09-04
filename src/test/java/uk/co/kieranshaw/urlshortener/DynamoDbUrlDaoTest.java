package uk.co.kieranshaw.urlshortener;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.kieranshaw.urlshortener.dao.DynamoDbUrlDao;



public class DynamoDbUrlDaoTest {

	@Test
	public void test() throws Exception {
		DynamoDbUrlDao dao = new DynamoDbUrlDao();
		dao.afterPropertiesSet();
		
		String incomingUrl = "http://www.google.com";
		String incomingShortCode = "abcd";
		dao.storeUrl(incomingShortCode, incomingUrl);
		
		String fetchedUrl = dao.getUrl(incomingShortCode);
		
		assertEquals(incomingUrl, fetchedUrl);
	}

}
