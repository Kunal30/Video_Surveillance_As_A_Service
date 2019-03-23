package SQS;

import java.util.List;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

public class SQS {

	AmazonSQS sqs;

	public SQS() {
		this.sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.US_WEST_1).build();
	}

	public String receiveMessages() {

//		String queueUrl = "https://sqs.us-west-1.amazonaws.com/841341665719/vs_input_queue";
		List<String> queueUrls= sqs.listQueues().getQueueUrls();

		String queueUrl = queueUrls.get(0);
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		if (messages.size() >= 1) {
			for (int i = 0; i < messages.size(); i++) {
				deleteMessage(messages.get(i));
				return messages.get(i).getBody();
			}
		}
		return "";
	}

	public void sendMessage(String message) {

//		String queueUrl = "https://sqs.us-west-1.amazonaws.com/841341665719/vs_output_queue";
		List<String> queueUrls= sqs.listQueues().getQueueUrls();

		String queueUrl = queueUrls.get(1);
		sqs.sendMessage(queueUrl, message);
	}

	public void deleteMessage(Message msg) {

//		String queueUrl = "https://sqs.us-west-1.amazonaws.com/841341665719/vs_input_queue";
		List<String> queueUrls= sqs.listQueues().getQueueUrls();

		String queueUrl = queueUrls.get(0);
		String messageReceiptHandle = msg.getReceiptHandle();

		sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));

	}

}
