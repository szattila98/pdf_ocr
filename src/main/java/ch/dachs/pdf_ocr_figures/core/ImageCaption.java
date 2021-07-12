package ch.dachs.pdf_ocr_figures.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.text.TextPosition;

import lombok.Data;

/**
 * Base type of the application. Contains information about captions.
 * 
 * @author Szőke Attila
 */
@Data
public class ImageCaption {

	private static final String CAPTION_FORMAT_NO_IMAGE = "%s - Page %d - no image, only drawn figure";
	private static final String CAPTION_FORMAT_WITH_IMAGE = "%s - Page %d - %s";
	private static final String GROUPED_IMAGE_SIZE_FORMAT = "%d pictures with size of %s";
	private static final String IMAGE_SIZE_FORMAT = "%dx%d";
	private static final String SEPARATOR = ", ";

	private String text;
	private int pageNum;
	private List<ImageInfo> imageInfoList = new ArrayList<>();
	private TextPosition firstLetterTextPosition; // F is the letter

	public ImageCaption(String text, int pageNum, TextPosition firstLetterTextPosition) {
		this.text = text;
		this.pageNum = pageNum;
		this.firstLetterTextPosition = firstLetterTextPosition;
	}
	
	/**
	 * Concats a string to the caption text. Used when one caption is multiple line
	 * long.
	 * 
	 * @param text the string to concat
	 */
	public void concatText(String text) {
		this.text = this.text.concat(text);
	}

	/**
	 * Generates the String representation of the object. If images are present,
	 * also prints their dimensions, if not prints a message.
	 * 
	 * @return the String representation
	 */
	@Override
	public String toString() {
		if (imageInfoList.isEmpty()) {
			return String.format(CAPTION_FORMAT_NO_IMAGE, text.trim(), pageNum);
		}
		var imageStr = separateByCommas(groupBySizes(imageInfoList));
		return String.format(CAPTION_FORMAT_WITH_IMAGE, text.trim(), pageNum, imageStr);
	}

	private List<String> groupBySizes(List<ImageInfo> list) {
		return list.stream()
				.collect(Collectors.groupingBy(
						image -> String.format(IMAGE_SIZE_FORMAT, image.getImageWidth(), image.getImageHeight())))
				.entrySet().stream().map(entry -> {
					if (entry.getValue().size() == 1) {
						return entry.getKey();
					}
					return String.format(GROUPED_IMAGE_SIZE_FORMAT, entry.getValue().size(), entry.getKey());
				}).sorted((x, y) -> Integer.compare(y.length(), x.length())).collect(Collectors.toList());
	}
	
	private String separateByCommas(List<String> list) {
		return list.stream().collect(Collectors.joining(SEPARATOR));
	}
}
