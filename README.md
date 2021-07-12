# pdf_ocr
A pdf text extractor software that collects image captions.

## Usage

- Build with

`.\mvnw clean package`

- Run jar with dependencies in target dir. Alternatively run pre-packaged jar in dist dir.

`java -jar pdf_ocr_figures-0.0.2-SNAPSHOT-jar-with-dependencies.jar PDF32000_2008.pdf`

- The result is a PDF doc with the caption info.

## Requirements
- Java 16