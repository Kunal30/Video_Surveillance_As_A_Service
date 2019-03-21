package AppInitializer;

import SQS.SQS;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.io.FileUtils;

import S3.S3;

public class Listener {

	public void listen_and_giveOutput() throws IOException, InterruptedException {
		
		SQS sqs = new SQS();
		
		while (true) {
			String msg = sqs.receiveMessages();
			
			if (!msg.equals("")) {
				System.out.println("The message is: " + msg);
				dark_classification(msg);
			}
			
			System.out.println("Listening...");
			System.out.println("No message...");
		}
	}

	public void dark_classification(String msg) throws IOException, InterruptedException {
		
		String fileName = downloadFile();

		String output = runPythonScripts(fileName);

		move_To_S3(fileName, output);

		SQS sqsout = new SQS();

		System.out.println("Sending to output queue...");
		sqsout.sendMessage(output + "__" + fileName);
		System.out.println("Sent to output queue!");
		
//		System.out.println("Deleting message...");
//		System.out.println("Message deleted!");

	}

	public void move_To_S3(String fileName, String output) throws IOException, InterruptedException {
		
		System.out.println("Uploading to S3...");

		S3 s3 = new S3();

		s3.uploadToS3Bucket(fileName);
		
        System.out.println("Uploaded to S3!");

	}

	public String runPythonScripts(String fileName) throws IOException, InterruptedException {

		System.out.println("Running scripts...");

//		Process p0 = Runtime.getRuntime().exec("Xvfb :1 & export DISPLAY=:1; ./darknet detector demo cfg/coco.data cfg/yolov3-tiny.cfg yolov3-tiny.weights " + fileName + " -dont_show > result; python darknet_test.py");
//		p0.waitFor();

		Process p1 = new ProcessBuilder("/bin/bash", "-c", "Xvfb :1 & export DISPLAY=:1; ./darknet detector demo cfg/coco.data cfg/yolov3-tiny.cfg yolov3-tiny.weights " + fileName + " -dont_show > result; python darknet_test.py; cat result_label").start();
		p1.waitFor();
		
//		Process p1 = Runtime.getRuntime().exec("");
//		p1.waitFor();
//
//		Process p2 = Runtime.getRuntime().exec("");
//		p2.waitFor();

//		String output = new String(Files.readAllBytes(Paths.get("result_label")), StandardCharsets.UTF_8);
		
		
<<<<<<< HEAD
		
		BufferedReader br = new BufferedReader(new InputStreamReader(p1.getInputStream()));
		String output = br.readLine();
		p1.destroy();
		
		System.out.println("Output is: " + output);
		
		return output;

	}

	public String downloadFile() throws IOException {
		
		String url = "http://206.207.50.7/getvideo";

		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddhhmmss");

		String fileName = ft.format(dNow) + ".h264";

		System.out.println("File name created is: " + fileName);

		FileUtils.copyURLToFile(new URL(url), new File(fileName), 10000, 10000);

		return fileName;
=======
//		Process p = Runtime.getRuntime().exec("bash deeplearning.sh" + " " + i);
//		Process p1 = Runtime.getRuntime().exec("./darknet detector demo cfg/coco.data cfg/yolov3-tiny.cfg yolov3-tiny.weights drz" + i + ".h264  -dont_show > result");
//		Process p1 = Runtime.getRuntime().exec("./darknet detector demo cfg/coco.data cfg/yolov3-tiny.cfg yolov3-tiny.weights drz.h264  -dont_show > result");
//		Process p1 = Runtime.getRuntime().exec("python darknet_test.py");
//		Process p2 = Runtime.getRuntime().exec("python darknet_test.py");
//		Process p3 = Runtime.getRuntime().exec("cat result_label");
//		BufferedReader stdInput = new BufferedReader(new 
//                InputStreamReader(p3.getInputStream()));
//
//           BufferedReader stdError = new BufferedReader(new 
//                InputStreamReader(p3.getErrorStream()));
//           
//           String s;
//           String s_prev="";
//           // read the output from the command
//           System.out.println("Here is the standard output of the command:\n");
//           while ((s = stdInput.readLine()) != null) {
//               System.out.println(s);
//               s_prev=s;
//           }
           
//           TimeUnit.SECONDS.sleep(5);
           return "Temporary Output";
	}
	
	public String downloadFile()throws IOException
	{
//		BufferedInputStream in = new BufferedInputStream(new URL("http://206.207.50.7/getvideo").openStream());
//		String instance="drz"+i;
//		String vidName = "drz"+i+".h264";  
		String instance = "drz";
		String vidName = "drz.h264";
//		FileOutputStream fos = new FileOutputStream(vidName);  
//		int bytee;  
//		while((bytee = in.read()) != -1) {  
//		    fos.write(bytee);
//		}
////		i++;
//		fos.close();
		return instance;
>>>>>>> 153f461e3643cc186355b068706f292afd7f0ef4
	}
}
