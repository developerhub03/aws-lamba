package com.example.awsdemo.lambda;

import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.awsdemo.config.AwsClientConfig;
import com.example.awsdemo.model.Details;
import com.example.awsdemo.model.DetailsResponse;
import com.example.awsdemo.sqs.SQSImpl;

public class ApiLambda implements RequestHandler<Map<String, Object>, DetailsResponse> {

	private SQSImpl sqsimpl = new SQSImpl();

	private AwsClientConfig awsClient = new AwsClientConfig();

	public DetailsResponse handleRequest(Map<String, Object> input, Context arg1) {
		// Details detail = new Details();
		// input.keySet().forEach((key) -> {
		// detail.setName((String) input.get("name"));
		// detail.setEmailId((String) input.get("emailId"));
		// detail.setLocation((String) input.get("location"));
		// });
		// String name = ((String) input.get("name"));
		// String emailId = ((String) input.get("emailId"));
		// String location = ((String) input.get("location"));
		DetailsResponse resp = new DetailsResponse();
		try {
			Details detail = new Details();
			detail.setName("Ram");
			detail.setEmailId("rama@gmail.com");
			detail.setLocation("NY");

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
		AWSCredentials sessionCredentials = new BasicSessionCredentials("ASIAIHCADD5PJY3YOLEA",
				"t7UVxSkiXN4uiaM7dg/BGs1Hni2kjgChfOyD53uA",
				"FQoDYXdzEPP//////////wEaDCEaj7KsuoaIoaRfwCK3Ap6gZjE1aG6UZD84EtEXZ3J2o1qzIh+S0L2281NeoH8ew7k8xIYRaCslhhN1uX2wI1x+G4/Atnb4t8sxNEZsKI9QnOzCzD/Yo2rEW76Jk7rMIF0Jc7Ilvqb8HTTtnLYRdUhNTbH9+k6bi9EyD2EvAy4RB1DUA3mYYV0RWuX8hxPskysyMZ7AzmRHXXMI5JCjU3YzzlgNGwrlmBOLQe3Tk//IXFQGlXBIMHr1Rev8BKSZwiZOvs3Aj27hv/Qyx9crI3r7NEYg3k46euHPni/UAAOmMZ4du9Zx8ssXl/SBzYnVYwPtaQG2PK3cK8tBTmd5Sfavhzql70QezDqQ4RaXphnl8dc1pUasNWEgp/wfflDaa+HNndYhCbVBJLPkq6fjMUmqXa4Gy9N3TIKus1cxj8kzp0qE2BRUKMWJpdkF");
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
