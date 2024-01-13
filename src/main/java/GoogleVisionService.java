public class GoogleVisionService {
    VisionConfiguration visionConfiguration;

    TextProcessingService textProcessingService;

    /**
     * Google Vision API Files:Annotate
     *
     * @param file
     * @return BatchFileResponse
     * @throws Exception
     */
    @Override
    public BatchFileResponse getTextFromPdfUsingVision(byte[] file) throws JsonProcessingException {
        String pdfDataString = Base64.getEncoder().encodeToString(file);
        ArrayList<Feature> featureList = new ArrayList<>();
        featureList.add(new Feature("DOCUMENT_TEXT_DETECTION", 50));
        FileRequest request = new FileRequest();
        request.setFeatures(featureList);
        InputConfig inputConfig = new InputConfig(pdfDataString, "application/pdf");
        request.setInputConfig(inputConfig);
        VisionRequest reqBody = new VisionRequest();
        ArrayList<FileRequest> requests = new ArrayList<>();
        requests.add(request);
        reqBody.setRequests(requests);
        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(reqBody);
        String url = visionConfiguration.getApiFilesAnnotate();
        url = url + "?key=" + visionConfiguration.getApiKey();
        RestTemplate restTemplate = new RestTemplate();
        BatchFileResponse result = restTemplate.postForObject(url, requestBody, BatchFileResponse.class);
        return result;
    }

    /**
     * Get invoice date fromFactory Commercial Invoice using Vision API
     *
     * @param file
     * @return Invoice date
     * @throws Exception
     */
    @Override
    public String getInvoiceDateForFCI(byte[] file) throws Exception {
        BatchFileResponse response = getTextFromPdfUsingVision(file);

        //FCI - Factory Commercial Invoice
        return textProcessingService.getInvoiceDateFromFCI(response);
    }
}
