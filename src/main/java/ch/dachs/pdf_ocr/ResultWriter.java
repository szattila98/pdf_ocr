package ch.dachs.pdf_ocr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.google.common.collect.Lists;

import ch.dachs.pdf_ocr.core.ImageCaption;

/**
 * Writes String results into a PDF file.
 * 
 * @author SzokeAttila
 */
public class ResultWriter {

	private static final PDFont FONT_TYPE = PDType1Font.TIMES_ROMAN;
	private static final float FONT_SIZE = 12;
	private static final float LEADING = 1.6f * FONT_SIZE;
	private static final float MARGIN = 72;

	/**
	 * Writes the list of captions to a PDF file.
	 * 
	 * @param imageCaptionList list of captions
	 * @throws IOException thrown when PDF cannot be written
	 */
	public void write(List<ImageCaption> imageCaptionList) throws IOException {
		try (var document = new PDDocument()) {
			// page information				
			PDRectangle mediabox = new PDPage().getMediaBox();
			int allowedWidth = (int) (mediabox.getWidth() - 2 * MARGIN);
			float startX = mediabox.getLowerLeftX() + MARGIN;
			float startY = mediabox.getUpperRightY() - MARGIN;
			// breaking long lines
			var lines = breakStringToLines(imageCaptionList, allowedWidth);
			// sublists for pages based on lines
			var lineNoOnPage = (int) (startY / LEADING) - 2;
			List<List<String>> lineLists = Lists.partition(lines, lineNoOnPage);
			// writing to doc
			for (var subList : lineLists) {
				// new page
				var page = new PDPage();
				document.addPage(page);				
				// writing to page
				try (var contentStream = new PDPageContentStream(document, page)) {
					contentStream.beginText();
					contentStream.setFont(FONT_TYPE, FONT_SIZE);
					contentStream.newLineAtOffset(startX, startY);
					for (var line : subList) {
						contentStream.showText(line);
						contentStream.newLineAtOffset(0, -LEADING);
					}
					contentStream.endText();
				}
			}
			// saving doc
			document.save("pdf_ocr_results.pdf");
		}
	}

	/**
	 * Breaks a long String to lines so it does not clip out from a page.
	 * 
	 * @param imageCaptionList the list of captions
	 * @param allowedWidth            the width of writable space
	 * @return broken list of lines to write
	 * @throws IOException thrown when there is an error getting font width info
	 */
	private List<String> breakStringToLines(List<ImageCaption> imageCaptionList, int allowedWidth) throws IOException {
		List<String> lines = new ArrayList<>();
		for (var imageCaption : imageCaptionList) {
			var text = imageCaption.toString();
			String[] words = text.split(" ");
			var line = new StringBuilder();
		    for(String word : words) {
		        if(!line.isEmpty()) {
		            line.append(" ");
		        }
		        int size = (int) (FONT_SIZE * FONT_TYPE.getStringWidth(line + word) / 1000);
		        if(size > allowedWidth) {
		            lines.add(line.toString());
		            line.replace(0, line.length(), word);
		        } else {
		        	line.append(word);
		        }
		    }
		    lines.add(line.toString());
		}
		return lines;
	}
}