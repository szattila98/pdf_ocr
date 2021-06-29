package ch.dachs.pdf_ocr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class CaptionStripper {

	private static final String REGEXP = "Figure\\s+(\\w+\\.\\d+|\\d+)\\s+–.*";

	public List<ImageCaption> strip(String path) throws IOException {
		PDDocument doc = PDDocument.load(new File(path));
		int numberOfPages = doc.getNumberOfPages();

		List<ImageCaption> documentImageCaptions = new ArrayList<>();
		var captionStripper = new PDFTextStripper() {
			@Override
			protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
				var trimmed = text.trim();
				if (trimmed.matches(REGEXP)) {
					var imageCaption = new ImageCaption();
					imageCaption.setText(trimmed);
					imageCaption.setFirstLetterTextPosition(textPositions.get(0));
					documentImageCaptions.add(imageCaption);
				}
				super.writeString(text, textPositions);
			}

		};

		for (var currentPageNum = 1; currentPageNum < numberOfPages + 1; currentPageNum++) {
			captionStripper.setStartPage(currentPageNum);
			captionStripper.setEndPage(currentPageNum);
			captionStripper.getText(doc);
			for (var imageCaption : documentImageCaptions) {
				if (imageCaption.getPageNum() == 0) {
					imageCaption.setPageNum(currentPageNum);
				}
			}
			
			var imageStripper = new ImageInfoStripper();
		    var imageInfoList = imageStripper.getPageImageInfoList(doc.getPage(currentPageNum - 1));
			
		    System.out.println(currentPageNum + " : " + imageInfoList);
		    
		}		
		return documentImageCaptions;
	}
}
