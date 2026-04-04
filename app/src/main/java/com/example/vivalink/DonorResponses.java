package com.example.vivalink;

public class DonorResponses {
    private String responseId;
    private String donorId;
    private String requestId;
    private String responseStatus;
    private String confirmedByStaffId;
    private String responseDate;


    public DonorResponses() {}


    public DonorResponses(String responseId, String donorId, String requestId,
                          String responseStatus, String confirmedByStaffId, String responseDate) {
        this.responseId = responseId;
        this.donorId = donorId;
        this.requestId = requestId;
        this.responseStatus = responseStatus;
        this.confirmedByStaffId = confirmedByStaffId;
        this.responseDate = responseDate;
    }


    public String getResponseId() { return responseId; }
    public void setResponseId(String responseId) { this.responseId = responseId; }

    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getResponseStatus() { return responseStatus; }
    public void setResponseStatus(String responseStatus) { this.responseStatus = responseStatus; }

    public String getConfirmedByStaffId() { return confirmedByStaffId; }
    public void setConfirmedByStaffId(String confirmedByStaffId) { this.confirmedByStaffId = confirmedByStaffId; }

    public String getResponseDate() { return responseDate; }
    public void setResponseDate(String responseDate) { this.responseDate = responseDate; }


    @Override
    public String toString() {
        return "DonorResponses{" +
                "responseId='" + responseId + '\'' +
                ", donorId='" + donorId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", responseStatus='" + responseStatus + '\'' +
                ", confirmedByStaffId='" + confirmedByStaffId + '\'' +
                ", responseDate='" + responseDate + '\'' +
                '}';
    }
}
