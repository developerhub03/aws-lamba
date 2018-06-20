package com.example.awsdemo.lambda;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.awsdemo.model.Details;
import com.example.awsdemo.model.DetailsResponse;

public class ApiLambda implements RequestHandler<Details, DetailsResponse> {

	private String awsAccessKey = "";
	private String awsSecretKey = "";
	private String awsToken = "";
	
	public DetailsResponse handleRequest(Details input, Context arg1) {
		DetailsResponse resp = new DetailsResponse();
		try {
			Details detail = new Details();
			detail.setName(input.getName());
			detail.setEmailId(input.getEmailId());
			detail.setLocation(input.getLocation());

			String msg = sendMessage(detail);

			resp.setMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}

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

	public String sendMessage(Details detail) {
		AmazonSQS sqs = awsSQSClient();
		try {
			// CreateQueueRequest createQueueRequest = new CreateQueueRequest("ram-Queue");
			// String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
			String myQueueUrl = "https://sqs.us-east-2.amazonaws.com/015887481462/ram-Queue";
			sqs.sendMessage(new SendMessageRequest(myQueueUrl, detail.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Message Sent succesful!...";
	}

}
