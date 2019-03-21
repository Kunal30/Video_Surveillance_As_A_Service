package AppInitializer;
import java.io.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import AppInitializer.AppInitializer;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class AppInitializer {

	public static void main(String args[])throws IOException, InterruptedException
{
	SpringApplication.run(AppInitializer.class, args);
	System.out.println("App Tier running!!!");
	
<<<<<<< HEAD
//	Runtime.getRuntime().exec("sudo apt install xvfb");
	
=======
	// setup only once
//	Runtime.getRuntime().exec("sudo apt install xvfb");
//	Runtime.getRuntime().exec("Xvfb :1 & export DISPLAY=:1");
>>>>>>> 153f461e3643cc186355b068706f292afd7f0ef4
	
	Listener lis_obj=new Listener();
	
	lis_obj.listen_and_giveOutput();
	
}
}
