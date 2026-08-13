package com.doktorthe2nd.min.net;

import java.util.Map;

public class ApkBuildFingerprint {
    private String certificateMetaSha256;
    private String dexMetaSha256;
    private Map<String, String> soMetaSha256;   // arch -> hex string

    public ApkBuildFingerprint(String certificateMetaSha256, String dexMetaSha256,
                               Map<String, String> soMetaSha256) {
        this.certificateMetaSha256 = certificateMetaSha256;
        this.dexMetaSha256 = dexMetaSha256;
        this.soMetaSha256 = soMetaSha256;
    }

    public String getCertificateMetaSha256() { return certificateMetaSha256; }
    public String getDexMetaSha256() { return dexMetaSha256; }
    public Map<String, String> getSoMetaSha256() { return soMetaSha256; }
}