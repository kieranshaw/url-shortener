package uk.co.kieranshaw.urlshortener.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

@Repository
public class DynamoDbUrlDao implements UrlDao, InitializingBean {

	private static final String URL_TABLE_NAME = "url";
	private static final String ID_TABLE_NAME = "maxid";
	
	private AmazonDynamoDBClient dynamoDB;

	@Override
	public void afterPropertiesSet() throws Exception {
		initDb();
		initTables();
	}

	@Override
	public String getUrl(String shortCode) {
		GetItemRequest request = new GetItemRequest().withTableName(URL_TABLE_NAME)
				.withKey(Collections.singletonMap("shortCode", new AttributeValue(shortCode)));
		GetItemResult result = dynamoDB.getItem(request);

		if (result.getItem() == null) {
			return null;
		}

		return result.getItem().get("url").getS();
	}

	@Override
	public String getShortCodeForUrl(String url) {
		ScanRequest scanRequest = new ScanRequest().withTableName(URL_TABLE_NAME).addScanFilterEntry("url",
				new Condition().withAttributeValueList(new AttributeValue(url))
						.withComparisonOperator(ComparisonOperator.EQ));
		ScanResult scan = dynamoDB.scan(scanRequest);
		if (scan.getCount() == 0) {
			return null;
		}

		return scan.getItems().get(0).get("shortCode").getS();
	}

	@Override
	public void storeUrl(String shortCode, String url) {
		Map<String, AttributeValue> item = new HashMap<>();
		item.put("shortCode", new AttributeValue(shortCode));
		item.put("url", new AttributeValue(url));
		PutItemRequest request = new PutItemRequest(URL_TABLE_NAME, item);
		dynamoDB.putItem(request);
	}

	@Override
	public String generateShortCode() {
		UpdateItemRequest updateRequest = new UpdateItemRequest().withTableName(ID_TABLE_NAME)
				.withAttributeUpdates(Collections.singletonMap("value",
						new AttributeValueUpdate(new AttributeValue().withN("1"), AttributeAction.ADD)))
				.withKey(Collections.singletonMap("id", new AttributeValue().withN("1")))
				.withReturnValues(ReturnValue.UPDATED_OLD);
		UpdateItemResult result = dynamoDB.updateItem(updateRequest);
		String n = result.getAttributes().get("value").getN();
		return Long.toString(Long.parseLong(n), 36);
	}

	private void initDb() throws Exception {
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("urlshortener").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (C:\\Users\\Kieran\\.aws\\credentials), and is in valid format.", e);
		}
		dynamoDB = new AmazonDynamoDBClient(credentials);
		// dynamoDB.withEndpoint("http://localhost:8000");
		Region eu = Region.getRegion(Regions.EU_WEST_1);
		dynamoDB.setRegion(eu);
	}

	private void initTables() throws InterruptedException {
		createUrlTable();
		createIdTable();
	}

	private void createIdTable() throws InterruptedException {
		String idTableName = ID_TABLE_NAME;

		try {
			dynamoDB.describeTable(ID_TABLE_NAME).getTable();
		} catch (ResourceNotFoundException e) {
			CreateTableRequest createIdTableRequest = new CreateTableRequest().withTableName(idTableName)
					.withKeySchema(new KeySchemaElement().withAttributeName("id").withKeyType(KeyType.HASH))
					.withAttributeDefinitions(
							new AttributeDefinition().withAttributeName("id").withAttributeType(ScalarAttributeType.N))
					.withProvisionedThroughput(
							new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(5L));
			TableUtils.createTableIfNotExists(dynamoDB, createIdTableRequest);
			TableUtils.waitUntilActive(dynamoDB, idTableName);

			bootstrapIdTable();
		}
	}

	private void createUrlTable() throws InterruptedException {
		String tableName = URL_TABLE_NAME;

		CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
				.withKeySchema(new KeySchemaElement().withAttributeName("shortCode").withKeyType(KeyType.HASH))
				.withAttributeDefinitions(new AttributeDefinition().withAttributeName("shortCode")
						.withAttributeType(ScalarAttributeType.S))
				.withProvisionedThroughput(
						new ProvisionedThroughput().withReadCapacityUnits(20L).withWriteCapacityUnits(20L));

		TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
		TableUtils.waitUntilActive(dynamoDB, tableName);
	}

	private void bootstrapIdTable() {
		Map<String, AttributeValue> item = new HashMap<>();
		item.put("id", new AttributeValue().withN("1"));
		item.put("value", new AttributeValue().withN("1"));

		PutItemRequest request = new PutItemRequest(ID_TABLE_NAME, item);
		dynamoDB.putItem(request);
	}

}
