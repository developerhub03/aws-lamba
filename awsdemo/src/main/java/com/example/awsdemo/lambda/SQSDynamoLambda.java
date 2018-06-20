package com.example.awsdemo.lambda;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.example.awsdemo.model.CloudWatchEvent;
import com.example.awsdemo.model.CloudWatchResponse;
import com.example.awsdemo.model.Details;
import com.google.gson.Gson;

public class SQSDynamoLambda implements RequestHandler<CloudWatchEvent, CloudWatchResponse> {

	private String DYNAMODB_TABLE_NAME = "PersonDetails";
	private String awsAccessKey = "";
	private String awsSecretKey = "";
	private String awsToken = "";
	
	@Override
	public CloudWatchResponse handleRequest(CloudWatchEvent input, Context arg1) {
		Date today = Calendar.getInstance().getTime();
		CloudWatchEvent event = new CloudWatchEvent();
		event.setVersion("0.0");
		event.setId("1234");
		event.setDetailType("aws");
		event.setSource("Cloud Watch");
		event.setAccount("200031");
		event.setTime(today);
		event.setRegion("us-west-2");
		event.setResources("arn:aws:events:us-east-2:015887481462:rule/ram-rule-sqs");

		List<Message> msgs = pollSQS();
		if (msgs != null && msgs.size() > 0) {
			Gson gson = new Gson();
			for (Message msg : msgs) {
				if (msg == null)
					continue;

				String msgBody = msg.getBody();
				Details detail = gson.fromJson(msgBody, Details.class);
				storeToDynamo(detail);
				deleteMessage(msg);
			}

		}

		CloudWatchResponse resp = new CloudWatchResponse();
		resp.setMessage("Polled SQS Successfully and Stored into DynamoDB!!!");

		return resp;
	}

	@SuppressWarnings("deprecation")
	public AmazonSQS awsSQSClient() {
		Region useast2 = Region.getRegion(Regions.US_EAST_2);
		AWSCredentials sessionCredentials = new BasicSessionCredentials(awsAccessKey, awsSecretKey, awsToken);
		AmazonSQS sqs = new AmazonSQSClient(sessionCredentials);
		sqs.setRegion(useast2);

		return sqs;
	}

	@SuppressWarnings("deprecation")
	public DynamoDB dynamoDbClient() {
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();
		client.setRegion(Region.getRegion(Regions.US_EAST_2));
		DynamoDB dynamoDb = new DynamoDB(client);
		return dynamoDb;
	}

	private List<Message> pollSQS() {
		AmazonSQS sqs = awsSQSClient();
		// Get the URL for a queue
		String queueUrl = "https://sqs.us-east-2.amazonaws.com/015887481462/ram-Queue";
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		return messages;
	}

	private PutItemOutcome storeToDynamo(Details detail) throws ConditionalCheckFailedException {
		DynamoDB dyanamoDB = dynamoDbClient();
		return dyanamoDB.getTable(DYNAMODB_TABLE_NAME)
				.putItem(new PutItemSpec().withItem(new Item().withString("name", detail.getName())
						.withString("emailId", detail.getEmailId()).withString("location", detail.getLocation())));
	}

	private void deleteMessage(Message msg) {
		try {
			AmazonSQS sqs = awsSQSClient();
			String queueUrl = "https://sqs.us-east-2.amazonaws.com/015887481462/ram-Queue";
			String messageReceiptHandle = msg.getReceiptHandle();
			sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
		} catch (Exception e) {
			System.out.println("exception while deleting msg from  SQS : " + e.getMessage());
		}

	}

}
