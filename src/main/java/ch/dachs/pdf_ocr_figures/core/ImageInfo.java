package ch.dachs.pdf_ocr_figures.core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Contains image metadata, which is needed to couple images to captions.
 * 
 * @author Szőke Attila
 */
@Data
@AllArgsConstructor
public class ImageInfo {
	private int imageWidth;
	private int imageHeight;
	private float positionX;
	private float positionY;
}
