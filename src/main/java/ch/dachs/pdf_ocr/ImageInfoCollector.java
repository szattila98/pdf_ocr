package ch.dachs.pdf_ocr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class ImageInfoCollector extends PDFStreamEngine {

	private List<ImageInfo> pageImageInfoList = new ArrayList<>();

	public ImageInfoCollector() throws IOException {
		// TODO check what is needed here
		addOperator(new Concatenate());
		addOperator(new DrawObject());
		addOperator(new SetGraphicsStateParameters());
		addOperator(new Save());
		addOperator(new Restore());
		addOperator(new SetMatrix());
	}

	public List<ImageInfo> getDocumentImageInfo(PDDocument document) throws IOException {
		var i = 1;
		for (PDPage page : document.getPages()) {
			System.out.println(i);
			processPage(page);
			i++;
		}
		return pageImageInfoList;
	}

	// FIXME it finds images that aren't images, and does not find some images that
	// are try another approach, like check if matched text has something above them
	// and get info about that
	@Override
	protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
		String operation = operator.getName();
		if ("Do".equals(operation)) {
			COSName objectName = (COSName) operands.get(0);
			// get the PDF object
			PDXObject xobject = getResources().getXObject(objectName);
			// check if the object is an image object
			if (xobject instanceof PDImage) {
				PDImageXObject image = (PDImageXObject) xobject;
				int currentImageWidth = image.getWidth();
				int currentImageHeight = image.getHeight();
//				if (currentImageHeight > 123 && currentImageWidth > 123) {
				ImageInfo imageInfo = new ImageInfo(currentImageHeight, currentImageWidth);
				System.out.println(imageInfo.toString());
				pageImageInfoList.add(imageInfo);
//				}
			} else if (xobject instanceof PDFormXObject) {
				// TODO maybe not needed
				PDFormXObject form = (PDFormXObject) xobject;
				showForm(form);
			}
		} else {
			super.processOperator(operator, operands);
		}
	}
}
