package com.example.awsdemo.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.awsdemo.config.AwsClientConfig;
import com.example.awsdemo.model.Details;

public class SQSImpl {

	private AwsClientConfig awsClient;

	public String sendMessage(Details detail) {
		AmazonSQS sqs = awsClient.awsSQSClient();
		try {
			// CreateQueueRequest createQueueRequest = new CreateQueueRequest("ram-Queue");
			// String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
			String myQueueUrl = "https://sqs.us-east-2.amazonaws.com/015887481462/ran-queue";
			sqs.sendMessage(new SendMessageRequest(myQueueUrl, detail.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Message Sent succesful!...";
	}
}
