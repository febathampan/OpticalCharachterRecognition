public class TextProcessingService {

    /**
     * Extract date from Trading Company Commercial Invoice, Factory Packing List
     *
     * @param data
     * @return invoice date
     * @throws ParseException
     */
    @Override
    public String extractInvoiceDate(String data) throws ParseException {
        Calendar cal = Calendar.getInstance();
        String[] shortMonths = new DateFormatSymbols().getShortMonths();
        String invoiceDate;
        String result = "";
        Pattern invoiceDatePattern = Pattern.compile("(Invoice Date:(\\s)*(Jan|Feb|Mar|Apr|May|June|July|Aug|Sep|Oct|Nov|Dec)(\\s)*[0-9][0-9](\\s)*(,)(\\s)*[0-9]+)");
        Pattern datePattern = Pattern.compile("(Date(\\s)*:(\\s)*(Jan|Feb|Mar|Apr|May|June|July|Aug|Sep|Oct|Nov|Dec)(\\s)*[0-9][0-9](\\s)*(,)(\\s)*[0-9]+)");
        int i = 0;
        Matcher m2 = datePattern.matcher(data);
        while (m2.find()) {
            result = m2.group();
        }
        Matcher m1 = invoiceDatePattern.matcher(data);
        while (m1.find()) {
            result = m1.group();
        }

        String[] split = result.split("Date(\\s)*:(\\s)*");
        System.out.println("Invoice Date: " + split[split.length-1]);
        return split[split.length-1];
    }

    /**
     * Get invoice date from FCI , provided FCI exists in certain format only so that date can be read from
     * block 4, para 0, words 1,2,3,4
     * FCI - Factory Commercial Invoice
     *
     * @param result
     * @return
     * @throws ParseException
     */
    public String getInvoiceDateFromFCI(BatchFileResponse result) throws ParseException {

        //Get data from block 4,paragraph 0, words 1,2,3,4 (All are indices : i.e, use block.get(4) not block.get(3))
        ArrayList<Blocks> blocks = result.getResponses().get(0).getResponses().get(0).getFullTextAnnotation().getPages().get(0).getBlocks();
        StringBuilder sb = new StringBuilder();
        List<Words> words = blocks.get(4).getParagraphs().get(0).getWords();

        //Words 1-4 gives invoice date(All are indices : i.e, use words.get(1), not words.get(0))
        for (int i = 1; i <= 4; i++) {
            List<Symbols> symbols = words.get(i).getSymbols();
            for (Symbols symbol : symbols) {
                sb.append(symbol.getText());
            }
            sb.append(" ");
        }
        Date date = new SimpleDateFormat("MMMM dd ,yyyy").parse(sb.toString());
        log.debug("\nInvoice date from OCR::{}", sb);
        return sb.toString();
    }



    /**
     * Get seller address from FCI
     *
     * @param response
     * @return KNResponseDTO
     * @throws ParseException
     */
    public String extractSellerAddressFromFCI(BatchFileResponse response) throws Exception {
        KNResponseDTO knResponseDTO = new KNResponseDTO();
        String data = response.getResponses().get(0).getResponses().get(0).getFullTextAnnotation().getText();
        String result = "";
        Pattern patternFCI = Pattern.compile("((?s)(?<=SELLER:)(.*?)(?=Invoice))");
        Matcher matcher = patternFCI.matcher(data);
        while (matcher.find()) {
            result = matcher.group();
        }
        if (result.isEmpty()) {
            //throw new Exception(String.format(Constants.SELLER_ADDRESS_EXTRACTION_FAILED));
            return null;
        }
        else {
            result = result.replaceAll("\n"," ").trim();
            return result;
        }
    }


    /**
     * Get invoice number from FPL
     * @param response
     * @return
     */
    public String getInvoiceNumberFromFPL(BatchFileResponse response) {
        StringBuilder sb = new StringBuilder();
        String result = "";
        int b1x1 = 0, b1y1 = 0, b2x1 = 0, b2y1 = 0;
        int w1x1 = 0, w1y1 = 0, w2x1 = 0, w2y1 = 0;
        List<Blocks> blocksList = response.getResponses().get(0).getResponses().get(0).getFullTextAnnotation().getPages().get(0).getBlocks();
        block:
        for (Blocks blocks : blocksList) {
            List<Words> wordsList = blocks.getParagraphs().get(0).getWords();
            for (Words words : wordsList) {
                List<Symbols> symbolsList = words.getSymbols();
                for (Symbols symbols : symbolsList) {
                    sb.append(symbols.getText());
                }
                if (sb.toString().contains("Invoice Number :")) {
                    w1x1 = words.getBoundingBox().getVertices().get(0).getX();
                    w1y1 = words.getBoundingBox().getVertices().get(0).getY();
                    sb.setLength(0);
                    break block;
                }
                sb.append(" ");
            }
            sb.setLength(0);
        }
        if (w1x1 != 0 && w1y1 != 0) {
            block:
            for (Blocks blocks : blocksList) {
                b1x1 = blocks.getBoundingBox().getVertices().get(0).getX();
                b1y1 = blocks.getBoundingBox().getVertices().get(0).getY();
                if (b1x1 > w1x1 && (Math.abs(w1y1 - b1y1) <=10)) {
                    List<Words> wordsList = blocks.getParagraphs().get(0).getWords();
                    for (Words words : wordsList) {
                        w2x1 = words.getBoundingBox().getVertices().get(0).getX();
                        w2y1 = words.getBoundingBox().getVertices().get(0).getY();
                        if (w2x1 > w1x1 && (Math.abs(w2y1 - w1y1) <= 10)) {
                            List<Symbols> symbolsList = words.getSymbols();
                            for (Symbols symbols : symbolsList) {
                                sb.append(symbols.getText());
                            }
                            if (sb.toString().matches("[A-Z0-9]*")) {
                                result = sb.toString().trim();
                                break block;
                            }
                        }
                    }
                }
                sb.setLength(0);
            }
            if (result.isEmpty())
                return null;
            else
                return result;
        }
        else
            return null;
    }


}
