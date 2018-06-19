package com.example.awsdemo.lambda;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class DynamoS3Lambda implements RequestHandler<DynamodbEvent, String> {

	private static String S3Bucket = "ram-awsdemo";

	@Override
	public String handleRequest(DynamodbEvent event, Context arg1) {
		AmazonS3 s3Client = s3Config();
		pushSynamToS3(event, s3Client);
		return "File published succesfully to S3";
	}

	public void pushSynamToS3(DynamodbEvent event, AmazonS3 S3Client) {
		try {
			System.out.println("Uploading a new object to S3 from a file\n");
			File s3File = createSampleFile(event);
			S3Client.putObject(new PutObjectRequest(S3Bucket, s3File.getName(), s3File));
		} catch (Exception e) {
			System.out.println("Error during S3 Publish : " + e.getMessage());
		}

	}

	public AmazonS3 s3Config() {
		Region useast2 = Region.getRegion(Regions.US_EAST_2);
		AWSCredentials sessionCredentials = new BasicSessionCredentials("ASIAIHCADD5PJY3YOLEA",
				"t7UVxSkiXN4uiaM7dg/BGs1Hni2kjgChfOyD53uA",
				"FQoDYXdzEPP//////////wEaDCEaj7KsuoaIoaRfwCK3Ap6gZjE1aG6UZD84EtEXZ3J2o1qzIh+S0L2281NeoH8ew7k8xIYRaCslhhN1uX2wI1x+G4/Atnb4t8sxNEZsKI9QnOzCzD/Yo2rEW76Jk7rMIF0Jc7Ilvqb8HTTtnLYRdUhNTbH9+k6bi9EyD2EvAy4RB1DUA3mYYV0RWuX8hxPskysyMZ7AzmRHXXMI5JCjU3YzzlgNGwrlmBOLQe3Tk//IXFQGlXBIMHr1Rev8BKSZwiZOvs3Aj27hv/Qyx9crI3r7NEYg3k46euHPni/UAAOmMZ4du9Zx8ssXl/SBzYnVYwPtaQG2PK3cK8tBTmd5Sfavhzql70QezDqQ4RaXphnl8dc1pUasNWEgp/wfflDaa+HNndYhCbVBJLPkq6fjMUmqXa4Gy9N3TIKus1cxj8kzp0qE2BRUKMWJpdkF");
		AmazonS3 s3client = new AmazonS3Client(sessionCredentials);
		s3client.setRegion(useast2);

		return s3client;
	}

	public static File createSampleFile(DynamodbEvent event) throws IOException {
		Calendar calendar = new GregorianCalendar();

		File file = File.createTempFile("ram_" + calendar.get(Calendar.HOUR) + "_" + calendar.get(Calendar.MINUTE),
				".txt");
		file.deleteOnExit();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file));

		for (DynamodbStreamRecord record : event.getRecords()) {
			if (record == null)
				continue;
			writer.write(record.getEventID() + ":");
			writer.write(record.getEventName() + ":");
			writer.write(record.getDynamodb().toString() + "\n");
		}
		writer.close();
		return file;
	}

}
