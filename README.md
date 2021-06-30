# pdf_ocr
A pdf text reader software that collects image caption info from a pdf file.

## Usage

- Build with

`.\mvnw clean package`

- Run jar with dependencies in target dir. Alternatively run pre-packaged jar in dist dir.

`java -jar pdf_ocr-0.0.1-SNAPSHOT-jar-with-dependencies PDF32000_2008.pdf`

- The result is a PDF doc with the caption info.

## Requirements
- Java 16