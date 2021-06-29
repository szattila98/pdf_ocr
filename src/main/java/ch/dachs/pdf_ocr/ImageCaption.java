package ch.dachs.pdf_ocr;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.text.TextPosition;

import lombok.Data;

@Data
public class ImageCaption {
	private String text;
	private int pageNum;
	private List<ImageInfo> imageInfoList = new ArrayList<>();
	private TextPosition firstLetterTextPosition; // F is the letter

	@Override
	public String toString() {
		if (imageInfoList.isEmpty()) {
			return String.format("%s - Page %d - no real image detected, most probably caption belongs to a drawn figure", text.trim(), pageNum);
		}
		var sb = new StringBuilder();
		for(var imageInfo: imageInfoList) {
			sb.append(String.format(" %dx%d ", imageInfo.getImageWidth(), imageInfo.getImageHeight()));
		}
		return String.format("%s - Page %d -%s", text.trim(), pageNum, sb.toString());
	}
}
