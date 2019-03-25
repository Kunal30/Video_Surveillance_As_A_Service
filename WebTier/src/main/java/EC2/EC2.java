package EC2;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.IamInstanceProfileSpecification;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

//import com.amazonaws.util.Base64;


public class EC2 {
	
	final AmazonEC2 ec2;
	public EC2()
	{
		  ec2 = AmazonEC2ClientBuilder.defaultClient();
	}
    public void cloneInstances(int num) throws InterruptedException
    {
    	System.out.println("create an instance");

        String imageId = "ami-0409f2c0b1c886dcd";  //image id of the terminator instance
        int minInstanceCount = Math.max(1, num-1); //create 1 instance
        int maxInstanceCount = num;
        
        IamInstanceProfileSpecification ias=new IamInstanceProfileSpecification().withName("vs_instance_profile_name");
        
        RunInstancesRequest rir = new RunInstancesRequest(imageId,
                minInstanceCount, maxInstanceCount);
//        rir.setInstanceType("t2.micro"); //set instance type
        List<String> securityGroupIds = new ArrayList<String>();
		securityGroupIds.add("sg-098c98359868e3ee8");
        rir.withInstanceType("t2.micro")
           .withKeyName("isolated_test")
           .withIamInstanceProfile(ias)
           .withSecurityGroupIds(securityGroupIds)
           .withUserData(getUserDataScript());   
        System.out.println("####################"+rir.toString());
        
//   		rir.setSecurityGroupIds(securityGroupIds);

//        rir.withKeyName("isolated_test.pem");
        // running jar from far on EC2 instance creation
//        String initScript="cd darknet; java -jar AppTier_Terminator-1.0.0.jar > resultsss";
         // replace with terminator later
        RunInstancesResult result = ec2.runInstances(rir);
        
       
        List<Instance> resultInstance =
                result.getReservation().getInstances();

        for(Instance ins : resultInstance) {
            System.out.println("New instances has been created:" +
                    ins.getInstanceId());//print the instance ID
        }
        TimeUnit.SECONDS.sleep(20);
    }
    private static String getUserDataScript(){
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("#! /bin/bash");
//        lines.add("curl http://www.google.com > google.html");
//        lines.add("shutdown -h 0");
//        lines.add("");
        lines.add("EXPORT AWS_ACCESS_KEY=AKIAIJ2JUEA3NKB57ZBA; EXPORT AWS_SECRET_KEY=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 ; cd /home/ubuntu/darknet; java -jar AppTier_Terminator-1.0.0.jar > file1 2>&1 &");
        
//        lines.add("cd ~ ; echo \"Hello\" > result1");
//        lines.add("echo \"Hello\" > result2");
        String str = new String(Base64.encodeBase64(join(lines, "\n").getBytes()));
        return str;
    }
    static String join(Collection<String> s, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
                break;
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }
//    private String getECuserData(String string) {
//		// TODO Auto-generated method stub
//    	String userData = string;
//        String encodedString = 
//        		  Base64.getEncoder().withoutPadding().encodeToString(userData.getBytes());
//		return userData;
//	}
//	public String getECSuserData(String clusterName) {
//        String userData = clusterName;
//        String encodedString = 
//        		  Base64.getEncoder().withoutPadding().encodeToString(userData.getBytes());
//        return encodedString;
//    }
    
    
    
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
