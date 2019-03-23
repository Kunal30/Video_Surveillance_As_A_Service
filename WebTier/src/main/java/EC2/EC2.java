package EC2;

import java.util.*;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

public class EC2 {
	
	final AmazonEC2 ec2;
	public EC2()
	{
		  ec2 = AmazonEC2ClientBuilder.defaultClient();
	}
    public void cloneInstances(int num)
    {
    	System.out.println("create an instance");

        String imageId = "ami-0881bf30288f8ffc2";  //image id of the instance
        int minInstanceCount = Math.max(1, num-1); //create 1 instance
        int maxInstanceCount = num;

        RunInstancesRequest rir = new RunInstancesRequest(imageId,
                minInstanceCount, maxInstanceCount);
        rir.setInstanceType("t2.micro"); //set instance type
        
        // running jar far on EC2 instance creation
        rir.withUserData("#!/bin/bash \n java -jar AppTier-1.0.0.jar"); // replace with terminator later
        RunInstancesResult result = ec2.runInstances(rir);
        
       
        List<Instance> resultInstance =
                result.getReservation().getInstances();

        for(Instance ins : resultInstance) {
            System.out.println("New instances has been created:" +
                    ins.getInstanceId());//print the instance ID
        }
    }
	public void createinstance() {

//        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        System.out.println("create an instance");

        String imageId = "ami-0e355297545de2f82";  //image id of the instance
        int minInstanceCount = 1; //create 1 instance
        int maxInstanceCount = 1;

        RunInstancesRequest rir = new RunInstancesRequest(imageId,
                minInstanceCount, maxInstanceCount);
        rir.setInstanceType("t2.micro"); //set instance type

        RunInstancesResult result = ec2.runInstances(rir);

        List<Instance> resultInstance =
                result.getReservation().getInstances();

        for(Instance ins : resultInstance) {
            System.out.println("New instance has been created:" +
                    ins.getInstanceId());//print the instance ID
        }
    }
    
	public int getNumInstances()
	{
		DescribeInstanceStatusRequest describeRequest = new DescribeInstanceStatusRequest();
		describeRequest.setIncludeAllInstances(true);
		DescribeInstanceStatusResult describeInstances = ec2.describeInstanceStatus(describeRequest);
		List<InstanceStatus> instanceStatusList = describeInstances.getInstanceStatuses();
		Integer countOfRunningInstances = 0;
		for (InstanceStatus instanceStatus : instanceStatusList) {
			InstanceState instanceState = instanceStatus.getInstanceState();
			if (instanceState.getName().equals(InstanceStateName.Running.toString())) {
				countOfRunningInstances++;
			}
		}
		
		return countOfRunningInstances;
	}
	
    public void startinstance(String instanceId) {
//        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        StartInstancesRequest request = new StartInstancesRequest().
                withInstanceIds(instanceId);//start instance using the instance id
        ec2.startInstances(request);

    }

    public void stopinstance(String instanceId) {
//        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        StopInstancesRequest request = new StopInstancesRequest().
                withInstanceIds(instanceId);//stop instance using the instance id
        ec2.stopInstances(request);

    }

    public void terminateinstance(String instanceId) {
//        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        TerminateInstancesRequest request = new TerminateInstancesRequest().
                withInstanceIds(instanceId);//terminate instance using the instance id
        ec2.terminateInstances(request);

    }
//	public void cloneInstances(int req_Appinstances) {
//		// TODO Auto-generated method stub
//		System.out.println("create an instance");
//
//        String imageId = "ami-0e355297545de2f82";  //image id of the instance
//        int minInstanceCount = req_Appinstances-1; //create 1 instance
//        int maxInstanceCount = req_Appinstances;
//
//        RunInstancesRequest rir = new RunInstancesRequest(imageId,
//                minInstanceCount, maxInstanceCount);
//        rir.setInstanceType("t2.micro"); //set instance type
//
//        RunInstancesResult result = ec2.runInstances(rir);
//
//        List<Instance> resultInstance =
//                result.getReservation().getInstances();
//
//        for(Instance ins : resultInstance) {
//            System.out.println("New instances has been created:" +
//                    ins.getInstanceId());//print the instance ID
//        }
//	}
	
}
