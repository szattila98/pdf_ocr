package ch.dachs.pdf_ocr.strippers;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import ch.dachs.pdf_ocr.core.ImageCaption;

/**
 * Extension of PDFTextStripper. Strips text but only if it matches the given
 * regexp.
 * 
 * @author SzokeAttila
 */
public class RegexpPDFTextStripper extends PDFTextStripper {

	private final String regexp;
	private final List<ImageCaption> documentImageCaptions;

	/**
	 * Basic constructor. Sets the result list and the regexp.
	 * 
	 * @param documentImageCaptions result list
	 * @param regexp                to match against
	 * @throws IOException thrown when pdf cannot be processed
	 */
	public RegexpPDFTextStripper(List<ImageCaption> documentImageCaptions, String regexp) throws IOException {
		this.documentImageCaptions = documentImageCaptions;
		this.regexp = regexp;
	}

	/**
	 * Called when, getText is called. It is overriden so it extracts the text and
	 * the position of the first letter.
	 */
	@Override
	public void writeString(String text, List<TextPosition> textPositions) throws IOException {
		var trimmed = text.trim();
		if (trimmed.matches(regexp)) {
			var imageCaption = new ImageCaption();
			imageCaption.setText(trimmed);
			imageCaption.setFirstLetterTextPosition(textPositions.get(0));
			documentImageCaptions.add(imageCaption);
		}
		super.writeString(text, textPositions);
	}
}