package AppInitializer;

import SQS.SQS;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import S3.S3;
public class Listener {

//	int i=0;
	public void listen_and_giveOutput() throws IOException, InterruptedException
	{
		System.out.println("Listening!!!!!!!");
//		SQS sqs=new SQS('I');
		SQS sqs = new SQS();
		while(true)
		{
			String msg=sqs.receiveMessages();
			System.out.println(msg+" Hello");
			if(!msg.equals(""))
			{
				dark_classification(msg);
				
//				System.out.println(answer);
			}
			System.out.println("Listening!!!!!!!");
		}
	}
	
	public void dark_classification(String msg)throws IOException, InterruptedException
	{
		String vidName=downloadFile()+".txt";
		String output=runPythonScripts();
		move_To_S3(vidName,output);
		
//		SQS sqs_out=new SQS('O');
		SQS sqsout = new SQS();
		sqsout.sendMessage(output+"__"+vidName);
		
	}
	public void move_To_S3(String vidName, String output)throws IOException, InterruptedException
	{
		System.out.println("Uploading to S3");
		S3 s3=new S3();
		s3.uploadToS3Bucket(vidName);
		
	}
	public String runPythonScripts()throws IOException, InterruptedException
	{
		
//		Process p = Runtime.getRuntime().exec("bash deeplearning.sh" + " " + i);
//		Process p1 = Runtime.getRuntime().exec("./darknet detector demo cfg/coco.data cfg/yolov3-tiny.cfg yolov3-tiny.weights drz" + i + ".h264  -dont_show > result");
		Process p1 = Runtime.getRuntime().exec("./darknet detector demo cfg/coco.data cfg/yolov3-tiny.cfg yolov3-tiny.weights drz.h264  -dont_show > result");
		Process p2 = Runtime.getRuntime().exec("python darknet_test.py");
		Process p3 = Runtime.getRuntime().exec("cat result_label");
		BufferedReader stdInput = new BufferedReader(new 
                InputStreamReader(p3.getInputStream()));

           BufferedReader stdError = new BufferedReader(new 
                InputStreamReader(p3.getErrorStream()));
           
           String s;
           String s_prev="";
           // read the output from the command
           System.out.println("Here is the standard output of the command:\n");
           while ((s = stdInput.readLine()) != null) {
               System.out.println(s);
               s_prev=s;
           }
           
//           TimeUnit.SECONDS.sleep(5);
           return s_prev;
	}
	
	public String downloadFile()throws IOException
	{
		BufferedInputStream in = new BufferedInputStream(new URL("http://206.207.50.7/getvideo").openStream());
//		String instance="drz"+i;
//		String vidName = "drz"+i+".h264";  
		String instance = "drz";
		String vidName = "drz.h264";
		FileOutputStream fos = new FileOutputStream(vidName);  
		int bytee;  
		while((bytee = in.read()) != -1) {  
		    fos.write(bytee);
		}
//		i++;
		fos.close();
		return instance;
	}
}
