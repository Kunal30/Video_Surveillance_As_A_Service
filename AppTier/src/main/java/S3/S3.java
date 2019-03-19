package S3;

import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import java.io.*;
public class S3 {

	static AmazonS3 s3;
	public S3()
	{
		s3= getS3object();
	}
	
	public AmazonS3 getS3object()
	{
		 AWSCredentials credentials = null;
	        try {
	            credentials = new ProfileCredentialsProvider("default").getCredentials();
	        } catch (Exception e) {
	            throw new AmazonClientException(
	                    "Cannot load the credentials from the credential profiles file. " +
	                    "Please make sure that your credentials file is at the correct " +
	                    "location (/home/kunal/.aws/credentials), and is in valid format.",
	                    e);
	        }

	        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
	            .withCredentials(new AWSStaticCredentialsProvider(credentials))
	            .withRegion("us-west-1")
	            .build();
        
		return s3;
	}
	public void uploadToS3Bucket(String fileName)
	{
		File f=new File("result_label");
        System.out.println("Uploading a new object to S3 from a file\n");
        s3.putObject(new PutObjectRequest("vs-result-bucket", fileName, f));
        System.out.println("Uploaded");
	}
}
