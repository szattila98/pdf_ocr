package ch.dachs.pdf_ocr;

import java.io.IOException;

public class App {
	public static void main(String[] args) {
		System.out.println("PDF OCR \n=================================================");
		var stripper = new CaptionStripper();
		try {
			var captions = stripper.strip("testpath");
			for (var caption : captions) {
				System.out.println(caption.toString());
			}
		} catch (IOException e) {
			System.out.println("File cound not be opened/parsed!");
		}
	}
}
