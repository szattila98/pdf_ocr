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
	private static final int MIN_DIFFERENCE = 10;
	private static final int MAX_DIFFERENCE = 12;

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
			imageCaption.setPageNum(this.getCurrentPageNo());
			documentImageCaptions.add(imageCaption);
		} else if (!documentImageCaptions.isEmpty()) {
			// Check if there is another line for the last caption and concat the line to the caption text
			var lastCaption = documentImageCaptions.get(documentImageCaptions.size() - 1);
			var lastCaptionYPos = lastCaption.getFirstLetterTextPosition().getTextMatrix().getTranslateY();
			var currentLineYPos = textPositions.get(0).getTextMatrix().getTranslateY();
			var yDifference = lastCaptionYPos - currentLineYPos;
			if (lastCaption.getPageNum() == this.getCurrentPageNo() && yDifference < MAX_DIFFERENCE && yDifference > MIN_DIFFERENCE) {
				lastCaption.concatText(trimmed);
			}
		}
		super.writeString(text, textPositions);
	}
}