package S3;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3 {

	AmazonS3 s3;

	public S3() {
		this.s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_1).build();

	}

	public void uploadToS3Bucket(String fileName) {
		File f = new File("result_label");

		s3.putObject("vs-kunal-isolated", fileName, f);

		FileUtils.deleteQuietly(f);

	}
}
