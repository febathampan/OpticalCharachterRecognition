public class OCRService {
    TextProcessingService textProcessingService;

    /**
     * Get Response DTO from pdf using pdfBox
     *
     * @param file
     * @return
     * @throws Exception
     */
    public KNResponseDTO getDTOFromPdfUsingPdfBox(byte[] file) throws Exception {
        KNResponseDTO responseDTO = new KNResponseDTO();
        responseDTO.setText(getTextFromPDF(file));
        String text = responseDTO.getText();
        responseDTO.setInvoiceDate(textProcessingService.extractInvoiceDate(text));
        responseDTO.setReferencePO(textProcessingService.getReferencePO(text));
        return responseDTO;
    }

    /**
     * For Input File Type is PDF
     * Apache PDF BOX
     *
     * @param file
     * @return
     * @throws Exception
     */
    private String getTextFromPDF(byte[] file) throws Exception {
        PDFParser parser = new PDFParser(new RandomAccessBufferedFileInputStream(new ByteArrayInputStream(file)));
        parser.parse();
        COSDocument cosDoc = parser.getDocument();
        PDFTextStripper pdfStripper = new PDFTextStripper();
        PDDocument pdDoc = new PDDocument(cosDoc);
        String parsedText = pdfStripper.getText(pdDoc);
        pdDoc.close();
        return parsedText;
    }

}
