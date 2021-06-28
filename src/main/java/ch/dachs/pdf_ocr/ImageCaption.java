package ch.dachs.pdf_ocr;

import lombok.Data;

@Data
public class ImageCaption {
	private String text;
	private int pageNum;
	private int imageHeight;
	private int imageWidth;
	
	@Override
	public String toString() {
		return String.format("%s - Page %d - %dx%d", text, pageNum, imageHeight, imageWidth);
	}
}
