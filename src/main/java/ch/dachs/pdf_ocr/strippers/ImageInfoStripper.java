package ch.dachs.pdf_ocr.strippers;

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
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.util.Matrix;

import ch.dachs.pdf_ocr.core.ImageInfo;

/**
 * Extension of PDFStreamEngine class. It finds real images (not drawings) and
 * collects information about them.
 * 
 * @author SzokeAttila
 */
public class ImageInfoStripper extends PDFStreamEngine {

	private List<ImageInfo> pageImageInfoList = new ArrayList<>();

	/**
	 * Basic constructor. Adds all needed operators.
	 * 
	 * @throws IOException operator adding exception
	 */
	public ImageInfoStripper() throws IOException {
		addOperator(new Concatenate());
		addOperator(new DrawObject());
		addOperator(new SetGraphicsStateParameters());
		addOperator(new Save());
		addOperator(new Restore());
		addOperator(new SetMatrix());
	}

	/**
	 * Gets every images' info from a page.
	 * 
	 * @param page the page
	 * @return list of ImageInfo
	 * @throws IOException thrown when page cannot be processed
	 */
	public List<ImageInfo> getPageImageInfoList(PDPage page) throws IOException {
		processPage(page);
		return pageImageInfoList;
	}

	/**
	 * ProcessPage calls this on every object. It checks whether the object is an
	 * image and extracts ImageInfo, then puts it into the result list.
	 */
	@Override
	protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
		String operation = operator.getName();
		if ("Do".equals(operation)) {
			COSName objectName = (COSName) operands.get(0);
			// get the PDF object
			PDXObject xobject = getResources().getXObject(objectName);
			// check if the object is an image object
			if (xobject instanceof PDImage) {
				// gather image info
				Matrix trMatrix = getGraphicsState().getCurrentTransformationMatrix();
				int imageWidth = (int) trMatrix.getScalingFactorX(); // displayed size in user space units
				int imageHeight = (int) trMatrix.getScalingFactorY(); // displayed size in user space units
				float xPosition = trMatrix.getTranslateX(); // positions in userSpaceUnits
				float yPosition = trMatrix.getTranslateY(); // positions in userSpaceUnits

				if (imageWidth > 1 && imageHeight > 1) {
					pageImageInfoList.add(new ImageInfo(imageWidth, imageHeight, xPosition, yPosition));
				}
			} else if (xobject instanceof PDFormXObject) {
				PDFormXObject form = (PDFormXObject) xobject;
				showForm(form);
			}
		} else {
			super.processOperator(operator, operands);
		}
	}
}
