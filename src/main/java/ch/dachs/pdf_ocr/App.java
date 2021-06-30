package ch.dachs.pdf_ocr;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class App {

	private static final String PDF_PATH_ERR_MSG = "Please specify a pdf path to continue with the extraction!";
	private static final String FILE_OPERATION_ERR_MSG = "File cound not be opened/parsed/written!";
	private static final String SUCCESS_MSG = "Image Captions extracted to a pdf file!";

	private static final Logger logger = LogManager.getLogger("App");

	public static void main(String[] args) {
		if (args.length == 0) {
			logger.error(PDF_PATH_ERR_MSG);
			return;
		}
		var stripper = new ImageCaptionRetriever();
		try {
			var captions = stripper.strip(args[0]);
			new ResultWriter().write(captions);
			logger.info(SUCCESS_MSG);
		} catch (IOException e) {
			logger.error(FILE_OPERATION_ERR_MSG);
		}
	}
}
