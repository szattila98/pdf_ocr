package ch.dachs.pdf_ocr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.google.common.collect.Lists;

public class ResultWriter {

	// TODO clean up code and magic numbers
	public void write(List<ImageCaption> imageCaptionList) throws IOException {
		PDDocument document = new PDDocument();
		List<List<ImageCaption>> lists = Lists.partition(imageCaptionList, 35);
		for (List<ImageCaption> subList : lists) {
			PDPage page = new PDPage();
			document.addPage(page);
			PDFont font = PDType1Font.TIMES_ROMAN;
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			contentStream.beginText();
			contentStream.setFont(font, 12);
			contentStream.newLineAtOffset(50, 730);
			for (var imageCaption : subList) {
				contentStream.showText(imageCaption.toString());
				contentStream.newLineAtOffset(0, -20);
			}
			contentStream.endText();
			contentStream.close();
		}

		document.save("pdf_ocr_results.pdf");
		document.close();
	}
}
