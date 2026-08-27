package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentDto {
    private String url;
    private String binaryUrl;
    private String filename;
    private String contentType;
    private Long size;
}
