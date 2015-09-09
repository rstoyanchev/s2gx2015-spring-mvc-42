package s2gx2015;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;


@Controller
public class DirectStreamingIntervalController {

	private static Logger logger = LoggerFactory.getLogger(DirectStreamingIntervalController.class);


	@RequestMapping(path = "/streaming-response-body", method = RequestMethod.GET)
	public StreamingResponseBody handle() {

		return os -> {
			for (int i=1; i <= 10; i++) {
				String line = String.valueOf(i) + "\n";
				os.write(line.getBytes(Charset.forName("UTF-8")));
				os.flush();
				logger.debug("Wrote value: " + i);
				sleep(1);
			}
		};
	}

	private static void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		}
		catch (InterruptedException ex) {
			// ignore
		}
	}

}