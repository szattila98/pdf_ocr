package ch.dachs.pdf_ocr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;
import org.apache.pdfbox.text.PDFTextStripper;

public class App {
	private static final String PDF_PATH = "C:\\Users\\SzokeAttila\\Desktop\\PDF32000_2008.pdf";

	public static void main(String[] args) throws Exception {
		System.out.println("PDF OCR \n=================================================");
		
		PDDocument doc = PDDocument.load(new File(PDF_PATH));
		
		int numberOfPages = doc.getNumberOfPages();
		PDFTextStripper stripper = new PDFTextStripper();
		List<String> imageCaptionTextList = new ArrayList<String>();
		for (var i = 1; i < numberOfPages + 1; i++) {
			stripper.setStartPage(i);
			stripper.setEndPage(i);
			String textOnPage = stripper.getText(doc);
			var textList = Arrays.asList(textOnPage.split("\n"));
			var figures = textList.stream().filter(text -> {
				String regex = "Figure\\s+(\\w+\\.\\d+|\\d+)\\s+(-|–).*";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(text);
				return matcher.lookingAt();
			}).collect(Collectors.toList());
			imageCaptionTextList.addAll(figures);
		}
		
		for(var imageCaption: imageCaptionTextList) {
			System.out.println(String.format("%s", imageCaption));
		}
	}
}
