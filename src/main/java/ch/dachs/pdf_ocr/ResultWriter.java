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

	private static final int CAPTION_NO_ON_PAGE = 17;

	/**
	 * Writes the list of captions to a PDF file.
	 * 
	 * @param imageCaptionList list of captions
	 * @throws IOException thrown when PDF cannot be written
	 */
	public void write(List<ImageCaption> imageCaptionList) throws IOException {
		try (var document = new PDDocument()) {
			// sublists for pages
			List<List<ImageCaption>> lists = Lists.partition(imageCaptionList, CAPTION_NO_ON_PAGE);
			for (List<ImageCaption> subList : lists) {
				// new page
				var page = new PDPage();
				document.addPage(page);
				// formatting information
				PDFont font = PDType1Font.TIMES_ROMAN;
				float fontSize = 12;
				float leading = 1.6f * fontSize;
				PDRectangle mediabox = page.getMediaBox();
				float margin = 72;
				float width = mediabox.getWidth() - 2 * margin;
				float startX = mediabox.getLowerLeftX() + margin;
				float startY = mediabox.getUpperRightY() - margin;
				// breaking long lines
				var lines = breakStringToLines(subList, font, fontSize, width);
				// writing to page
				var contentStream = new PDPageContentStream(document, page);
				contentStream.beginText();
				contentStream.setFont(font, fontSize);
				contentStream.newLineAtOffset(startX, startY);
				for (var line : lines) {
					contentStream.showText(line);
					contentStream.newLineAtOffset(0, -leading);
				}
				contentStream.endText();
				contentStream.close();
			}
			// saving doc
			document.save("pdf_ocr_results.pdf");
		}
	}

	/**
	 * Breaks a long String to lines so it does not clip out from a page.
	 * 
	 * @param imageCaptionList the list of captions
	 * @param font             the font style
	 * @param fontSize         the font size
	 * @param width            the width of writable space
	 * @return broken list of lines to write
	 * @throws IOException thrown when there is an error getting font width info
	 */
	private List<String> breakStringToLines(List<ImageCaption> imageCaptionList, PDFont font, float fontSize,
			float width) throws IOException {
		List<String> lines = new ArrayList<>();
		int lastSpace = -1;
		for (var imageCaption : imageCaptionList) {
			var text = imageCaption.toString();
			while (text.length() > 0) {
				int spaceIndex = text.indexOf(' ', lastSpace + 1);
				if (spaceIndex < 0)
					spaceIndex = text.length();
				var subString = text.substring(0, spaceIndex);
				float size = fontSize * font.getStringWidth(subString) / 1000;
				if (size > width) {
					if (lastSpace < 0)
						lastSpace = spaceIndex;
					subString = text.substring(0, lastSpace);
					lines.add(subString);
					text = text.substring(lastSpace).trim();
					lastSpace = -1;
				} else if (spaceIndex == text.length()) {
					lines.add(text);
					text = "";
				} else {
					lastSpace = spaceIndex;
				}
			}

		}
		return lines;
	}
}