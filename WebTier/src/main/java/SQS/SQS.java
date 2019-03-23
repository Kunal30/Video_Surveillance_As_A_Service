package SQS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.*;
//import com.aws.listener.constants.Constants;

public class SQS {

	AmazonSQS sqs;
//	char c;
	public SQS() {
		this.sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.US_WEST_1).build();
	}
//	public SQS(char ch)
//	{
//		sqs= getSQSobject();
//		c=ch;
//	}
//	public AmazonSQS getSQSobject()
//	{
//		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
//        try {
//            credentialsProvider.getCredentials();
//        } catch (Exception e) {
//            throw new AmazonClientException(
//                    "Cannot load the credentials from the credential profiles file. " +
//                    "Please make sure that your credentials file is at the correct " +
//                    "location (/home/kunal/.aws/credentials), and is in valid format.",
//                    e);
//        }
//		AmazonSQS sqs = AmazonSQSClientBuilder.standard()
//                .withCredentials(credentialsProvider)
//                .withRegion(Regions.US_WEST_1)
//                .build();
//		return sqs;
//	}
	public String receiveMessages()
	{
//		AmazonSQS sqs=getSQSobject();
		
		List<String> queueUrls= sqs.listQueues().getQueueUrls();
		
		String queueUrl = queueUrls.get(1);
		
//		if(c=='I')
//		queueUrl=queueUrls.get(0);
//		else
//			queueUrl=queueUrls.get(1);
		
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(messages.size()>=1)
        {	
        for(int i=0;i<messages.size();i++)
        {
//        	System.out.println("Message=");
//        	System.out.println(messages.get(i).getBody());
        	deleteMessage(messages.get(i));
        	return messages.get(i).getBody();
        }
        }
        return "";
//        return messages.get(0).getBody();
	}
	public int getNumberofMessages()
	{
//		GetQueueUrlResult getQueueUrlResponse =
//		        sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName("vs_input_queue").build());
//		String queueUrl = getQueueUrlResponse.getQueueUrl();
		List<String> queueUrls= sqs.listQueues().getQueueUrls();

		String queueUrl = queueUrls.get(0);
//		Message message = sqsService.receiveMessage(Constants.INPUTQUEUENAME, 20, 15);
//		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
//        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        
//        return messages.size();
		List<String> attributeNames = new ArrayList<String>();
		attributeNames.add("ApproximateNumberOfMessages");
		
		GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest(queueUrl, attributeNames);
		Map<String, String> map = sqs.getQueueAttributes(getQueueAttributesRequest).getAttributes();
		String numberOfMessagesString = (String) map.get("ApproximateNumberOfMessages");
		Integer numberOfMessages = Integer.valueOf(numberOfMessagesString);
		return numberOfMessages;
		
//		int n = Integer.valueOf(sqs.getQueueAttributes(new GetQueueAttributesRequest(queueUrl, "ApproximateNumberOfMessages")).getAttributes().get("ApproximateNumberOfMessages"))
	}
	public void sendMessage(String str)
	{
//		AmazonSQS sqs=getSQSobject();
//		List<String> queueUrls= sqs.listQueues().getQueueUrls();
		List<String> queueUrls= sqs.listQueues().getQueueUrls();

		String queueUrl = queueUrls.get(0);
//		String queueUrl = "https://sqs.us-west-1.amazonaws.com/841341665719/vs_input_queue";
		
//		if(c=='I')
//		queueUrl=queueUrls.get(0);
//		else
//			queueUrl=queueUrls.get(1);
		
		System.out.println("Hey its this queue==="+queueUrl);
		sqs.sendMessage(new SendMessageRequest(queueUrl, str));
		System.out.println("Message sent in the queue");
	}
	public void deleteMessage(Message msg)
	{
//		AmazonSQS sqs=getSQSobject();
//		List<String> queueUrls= sqs.listQueues().getQueueUrls();
//		if(queueUrls.size()>0)
//		{
		List<String> queueUrls= sqs.listQueues().getQueueUrls();

		String queueUrl = queueUrls.get(1);
//		String queueUrl = "https://sqs.us-west-1.amazonaws.com/841341665719/vs_output_queue";
//		
//		if(c=='I')
//		queueUrl=queueUrls.get(0);
//		else
//			queueUrl=queueUrls.get(1);
//		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
//        
//		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
//		if(messages.size()>0)
		String messageReceiptHandle = msg.getReceiptHandle();
        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
        System.out.println("Message Deleted!!!!");
        
//        System.out.println("Message  '"+messageReceiptHandle+" ' has been deleted");
		
	}
	
	public String getOutputFromSQSOut()
	{
		
		while(true)
		{
			String msg=receiveMessages();
			
			if(!msg.equals(""))
				return msg;

		}
	}

}
