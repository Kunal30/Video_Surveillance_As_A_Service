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
	
//	Runtime.getRuntime().exec("sudo apt install xvfb");

	Listener lis_obj=new Listener();
	
	lis_obj.listen_and_giveOutput();
	
}
}
