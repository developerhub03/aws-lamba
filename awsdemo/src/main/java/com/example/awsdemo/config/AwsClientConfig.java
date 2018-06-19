package com.example.awsdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;

@PropertySource("classpath:application.properties")
public class AwsClientConfig {

	@Value("${jsa.aws.access_key_id}")
	private String awsId;

	@Value("${jsa.aws.secret_access_key}")
	private String awsKey;

	@Value("${jsa.aws.token}")
	private String awsToken;

	@SuppressWarnings("deprecation")
	public AmazonSQS awsSQSClient() {
		Region useast2 = Region.getRegion(Regions.US_EAST_2);
		AWSCredentials sessionCredentials = new BasicSessionCredentials(awsId, awsKey, awsToken);
		AmazonSQS sqs = new AmazonSQSClient(sessionCredentials);
		sqs.setRegion(useast2);

		return sqs;
	}

}
