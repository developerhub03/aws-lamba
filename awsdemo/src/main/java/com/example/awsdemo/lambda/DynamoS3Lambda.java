package com.example.awsdemo.lambda;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
	private String awsAccessKey = "";
	private String awsSecretKey = "";
	private String awsToken = "";
	
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
		AWSCredentials sessionCredentials = new BasicSessionCredentials(awsAccessKey, awsSecretKey, awsToken);
		AmazonS3 s3client = new AmazonS3Client(sessionCredentials);
		s3client.setRegion(useast2);

		return s3client;
	}

	public static File createSampleFile(DynamodbEvent event) throws IOException {
		File file = File.createTempFile("ram_" + buildCurrentDate() + "_" + buildCurrentTime(), ".txt");
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

	public static String buildCurrentDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date today = Calendar.getInstance().getTime();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String reportDate = df.format(today);
		return reportDate;
	}

	public static Timestamp buildCurrentTime() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Date time = calendar.getTime();
		Timestamp timestamp = new Timestamp(time.getTime());
		return timestamp;
	}
}
