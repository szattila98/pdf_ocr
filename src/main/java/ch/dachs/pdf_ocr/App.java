package ch.dachs.pdf_ocr;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class App {	
	private static final Logger logger = LogManager.getLogger("App");
	
	public static void main(String[] args) {
		if (args.length == 0) {
			logger.error("Please specify a pdf path to continue with the extraction!");
			return;
		}
		var stripper = new CaptionStripper();
		try {
			var captions = stripper.strip(args[0]);
			new ResultWriter().write(captions);
			logger.info("Image Captions extracted to a pdf file!");
		} catch (IOException e) {
			logger.error("File cound not be opened/parsed/written!");
		}
	}
}
