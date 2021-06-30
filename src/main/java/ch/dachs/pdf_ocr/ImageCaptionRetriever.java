package ch.dachs.pdf_ocr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import ch.dachs.pdf_ocr.core.ImageCaption;
import ch.dachs.pdf_ocr.strippers.ImageInfoStripper;
import ch.dachs.pdf_ocr.strippers.RegexpPDFTextStripper;

public class ImageCaptionRetriever {

	private static final String REGEXP = "Figure\\s+(\\w+\\.\\d+|\\d+)\\s+–.*";

	public List<ImageCaption> strip(String path) throws IOException {
		PDDocument doc = PDDocument.load(new File(path));
		int numberOfPages = doc.getNumberOfPages();
		List<ImageCaption> documentImageCaptions = new ArrayList<>();
		var textStripper = new RegexpPDFTextStripper(documentImageCaptions, REGEXP);

		for (var currentPageNum = 1; currentPageNum < numberOfPages + 1; currentPageNum++) {
			// stripping text from page
			textStripper.setStartPage(currentPageNum);
			textStripper.setEndPage(currentPageNum);
			textStripper.getText(doc);

			// setting page number for captions
			for (var imageCaption : documentImageCaptions) {
				if (imageCaption.getPageNum() == 0) {
					imageCaption.setPageNum(currentPageNum);
				}
			}

			// stripping images from page
			var imageStripper = new ImageInfoStripper();
			var imageInfoList = imageStripper.getPageImageInfoList(doc.getPage(currentPageNum - 1));

			// TODO remove DEBUG
			var page = 315;
			if (currentPageNum == page) {
				for (var imageInfo : imageInfoList) {
					System.out.println(imageInfo.getImageWidth() + "x" + imageInfo.getImageHeight() + " : "
							+ imageInfo.getPositionY());
				}
			}

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

			// TODO remove DEBUG
			if (currentPageNum == page) {
				var caption = documentImageCaptions.stream().filter(cap -> cap.getPageNum() == page)
						./* skip(1). */findFirst().get();
				System.out.println("\n" + caption.getFirstLetterTextPosition().getTextMatrix().getTranslateY() + " - "
						+ caption.toString());
			}
		}
		return documentImageCaptions;
	}
}
