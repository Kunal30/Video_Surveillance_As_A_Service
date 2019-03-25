package WebTier;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;


import EC2.EC2;
import SQS.SQS;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class WebTierInitializer {

public static void main(String args[])throws IOException, InterruptedException
{
	SpringApplication.run(WebTierInitializer.class, args);
	System.out.println("DONEZO!!!");
	scaleOut();
}
public static void scaleOut() throws InterruptedException
{
	SQS sqs=new SQS();
	while(true)
	{
		int numberOfMsgs=sqs.getNumberofMessages();
		System.out.println("Number of messages in the queue");
		System.out.println(numberOfMsgs);
		EC2 ec2=new EC2();
		int num_of_live_ec2s= ec2.getNumInstances();
		System.out.println("Number of Running Instances");
		System.out.println(num_of_live_ec2s);
		int num_of_App=num_of_live_ec2s-1;
		System.out.println("Number of App Instances");
		System.out.println(num_of_App);
		if(numberOfMsgs >0 && numberOfMsgs>num_of_App)
		{
			int possible_Appinstances_to_bcreated= 19-num_of_App;
			if(possible_Appinstances_to_bcreated>0)
			{
				int req_Appinstances= numberOfMsgs-num_of_App;
				System.out.println("Required App Instances="+req_Appinstances);
				System.out.println("Possible App Instances="+possible_Appinstances_to_bcreated);
				if(req_Appinstances >= possible_Appinstances_to_bcreated)
				{
					ec2.cloneInstances(possible_Appinstances_to_bcreated);
				}
				else if(req_Appinstances < possible_Appinstances_to_bcreated)
				{
					ec2.cloneInstances(req_Appinstances);
				}
			}
		}
		TimeUnit.SECONDS.sleep(3);

//		Integer countOfRunningInstances = ec2Service.getNumberOfInstances();
//		System.out.println(countOfRunningInstances);
//		Integer numberOfAppInstances = countOfRunningInstances - 1;
//		System.out.println(numberOfAppInstances);
//		if (numOfMsgs > 0 && numOfMsgs > numberOfAppInstances) {
//			Integer temp = Constants.MAXRUNNINGINSTANCES - numberOfAppInstances;
//			if (temp > 0) {
//				Integer temp1 = numOfMsgs - numberOfAppInstances;
//				if (temp1 >= temp) {
//					nameCount = ec2Service.startInstances(temp, nameCount);
//				} else {
//					nameCount = ec2Service.startInstances(temp1, nameCount);
//				}
//				nameCount++;
//			}
	}
}
}
