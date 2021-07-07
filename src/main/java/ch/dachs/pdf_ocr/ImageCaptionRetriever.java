package ch.dachs.pdf_ocr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import ch.dachs.pdf_ocr.core.ImageCaption;
import ch.dachs.pdf_ocr.strippers.ImageInfoStripper;
import ch.dachs.pdf_ocr.strippers.RegexpPDFTextStripper;

/**
 * Retrieves ImageCaptions.
 * 
 * @author SzokeAttila
 */
public class ImageCaptionRetriever {

	private static final String REGEXP = "Figure\\s+(\\w+\\.\\d+|\\d+)\\s+–.*";

	/**
	 * Retrieves captions from the given doc. Also couples them with images.
	 * @param path the PDF doc path
	 * @return the list of captions
	 * @throws IOException thrown when PDF cannot be processed
	 */
	public List<ImageCaption> retrieve(String path) throws IOException {
		var doc = PDDocument.load(new File(path));
		int numberOfPages = doc.getNumberOfPages();
		List<ImageCaption> documentImageCaptions = new ArrayList<>();
		var textStripper = new RegexpPDFTextStripper(documentImageCaptions, REGEXP);

		for (var currentPageNum = 1; currentPageNum < numberOfPages + 1; currentPageNum++) {
			// stripping text from page
			textStripper.setStartPage(currentPageNum);
			textStripper.setEndPage(currentPageNum);
			textStripper.getText(doc);

			// stripping images from page
			var imageStripper = new ImageInfoStripper();
			var imageInfoList = imageStripper.getPageImageInfoList(doc.getPage(currentPageNum - 1));

			// coupling images to captions
			if (!imageInfoList.isEmpty()) {
				for (var imageCaption : documentImageCaptions) {
					var alreadyCoupledImageInfoList = new ArrayList<>();
					for (var imageInfo : imageInfoList) {
						if (imageCaption.getPageNum() == currentPageNum && imageInfo.getPositionY() > imageCaption
								.getFirstLetterTextPosition().getTextMatrix().getTranslateY()) {
							imageCaption.getImageInfoList().add(imageInfo);
							alreadyCoupledImageInfoList.add(imageInfo);
						}
					}
					imageInfoList.removeAll(alreadyCoupledImageInfoList);
				}
			}
		}
		return documentImageCaptions;
	}
}
