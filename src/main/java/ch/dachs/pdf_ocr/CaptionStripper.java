package ch.dachs.pdf_ocr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class CaptionStripper {

	private static final String PDF_PATH = "C:\\Users\\SzokeAttila\\Desktop\\PDF32000_2008.pdf";
	private static final String REGEXP = "Figure\\s+(\\w+\\.\\d+|\\d+)\\s+(-|–).*";

	public List<ImageCaption> strip(String path) throws IOException {
		PDDocument doc = PDDocument.load(new File(PDF_PATH));
		int numberOfPages = doc.getNumberOfPages();
		PDFTextStripper stripper = new PDFTextStripper();
		List<ImageCaption> imageCaptionList = new ArrayList<>();
		for (var i = 1; i < numberOfPages + 1; i++) {
			var currentPageNum = i;
			stripper.setStartPage(i);
			stripper.setEndPage(i);
			String textOnPage = stripper.getText(doc);
			var textList = Arrays.asList(textOnPage.split("\n"));
			var pageCaptionList = textList.stream().filter(text -> text.trim().matches(REGEXP)).map(text -> {
				var imageCaption = new ImageCaption();
				imageCaption.setText(text.trim());
				imageCaption.setPageNum(currentPageNum);
				return imageCaption;
			}).collect(Collectors.toList());
			imageCaptionList.addAll(pageCaptionList);
		}
		return imageCaptionList;
	}
}
