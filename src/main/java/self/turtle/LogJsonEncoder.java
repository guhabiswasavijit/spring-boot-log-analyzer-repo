package self.turtle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Component
public class LogJsonEncoder implements CommandLineRunner {
    @Value("${STACKTRACE_REGEX}")
    private String stackTraceRegex = null;
    @Value("${STOP_WORDS}")
    private String stopWords = null;
    @Value("${LOG_FILE}")
    private String logFile = null;
	@Override
	public void run(String... args) throws Exception {
		Arrays.asList(stopWords.split(Pattern.compile(";").pattern())).forEach(word ->{
			String regex = stackTraceRegex.replace("STOP-WORD",word);
			Pattern pattern = Pattern.compile(regex);
			String text = "";
			try(RandomAccessFile reader = new RandomAccessFile(logFile, "r")){
			    FileChannel channel = reader.getChannel();
			    int bufferSize = 1024*5;
			    if (bufferSize > channel.size()) {
			        bufferSize = (int) channel.size();
			    }
			    ByteBuffer buff = ByteBuffer.allocate(bufferSize);
			    channel.read(buff);
			    buff.flip();
                text=new String(buff.array()); 
			    channel.close();
			    reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Matcher matcher = pattern.matcher(text);
			StringBuffer output = new StringBuffer();
		    while (matcher.find()) {
		    	matcher.appendTail(output);
		    }
		    System.out.println(output.toString());
		});
		
	}

}
