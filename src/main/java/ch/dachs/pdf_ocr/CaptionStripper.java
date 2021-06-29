package ch.dachs.pdf_ocr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class CaptionStripper {

	private static final String REGEXP = "Figure\\s+(\\w+\\.\\d+|\\d+)\\s+(-|–).*";

	public List<ImageCaption> strip(String path) throws IOException {
		PDDocument doc = PDDocument.load(new File(path));
		int numberOfPages = doc.getNumberOfPages();
		var captionStripper = new PDFTextStripper();
		List<ImageCaption> imageCaptionList = new ArrayList<>();
		for (var i = 1; i < numberOfPages + 1; i++) {
			var currentPageNum = i;
			captionStripper.setStartPage(i);
			captionStripper.setEndPage(i);
			String textOnPage = captionStripper.getText(doc);
			var textList = Arrays.asList(textOnPage.split("\n"));
			var pageCaptionList = textList.stream().filter(text -> text.trim().matches(REGEXP)).map(text -> {
				var imageCaption = new ImageCaption();
				imageCaption.setText(text);
				imageCaption.setPageNum(currentPageNum);
				return imageCaption;
			}).collect(Collectors.toList());
			var imageStripper = new ImageInfoStripper();
			var pageImageList = imageStripper.getPageImageInfo(doc.getPage(currentPageNum - 1));
			
			// System.out.println(i + " : " + pageImageList.toString());
			
			for (var caption: pageCaptionList) {
				caption.setImageInfoList(pageImageList);
			}
			imageCaptionList.addAll(pageCaptionList);
		}
		return imageCaptionList;
	}
}
