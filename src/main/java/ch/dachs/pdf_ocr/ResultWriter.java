package ch.dachs.pdf_ocr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.google.common.collect.Lists;

public class ResultWriter {

	// TODO clean up code and magic numbers
	public void write(List<ImageCaption> imageCaptionList) throws IOException {
		PDDocument document = new PDDocument();
		List<List<ImageCaption>> lists = Lists.partition(imageCaptionList, 19);
		for (List<ImageCaption> subList : lists) {
			PDPage page = new PDPage();
			document.addPage(page);

			PDFont font = PDType1Font.TIMES_ROMAN;
			float fontSize = 12;
			float leading = 1.6f * fontSize;
			PDRectangle mediabox = page.getMediaBox();
			float margin = 72;
			float width = mediabox.getWidth() - 2 * margin;
			float startX = mediabox.getLowerLeftX() + margin;
			float startY = mediabox.getUpperRightY() - margin;

			var lines = breakStringToLines(subList, font, fontSize, width);

			PDPageContentStream contentStream = new PDPageContentStream(document, page);
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

		document.save("pdf_ocr_results.pdf");
		document.close();
	}

	private List<String> breakStringToLines(List<ImageCaption> imageCaptionList, PDFont font, float fontSize,
			float width) throws IOException {
		List<String> lines = new ArrayList<String>();
		int lastSpace = -1;
		for (var imageCaption : imageCaptionList) {
			String text = imageCaption.toString();
			while (text.length() > 0) {
				int spaceIndex = text.indexOf(' ', lastSpace + 1);
				if (spaceIndex < 0)
					spaceIndex = text.length();
				String subString = text.substring(0, spaceIndex);
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
