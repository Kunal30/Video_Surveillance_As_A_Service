package WebTier;

import org.springframework.web.bind.annotation.RestController;

import EC2.EC2;
import SQS.SQS;

import org.springframework.web.bind.annotation.RequestMapping;


@RestController
public class WebTierController {

	@RequestMapping("/reqobj")
    public String request_object_detection() {
       
//		SQS sqs=new SQS('I');
		SQS sqs = new SQS();
		
		sqs.sendMessage("Sending a request for object detection");
//		SQS sqsout=new SQS('O');
		SQS sqsout = new SQS();
		
		String output= sqsout.getOutputFromSQSOut();
		String out[]=output.split("__");
       return out[0];
		
    }
				        
	
}
