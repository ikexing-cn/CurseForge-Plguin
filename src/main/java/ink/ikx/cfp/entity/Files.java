package ink.ikx.cfp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Data
public class Files implements Serializable, Comparable<Files> {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("fileName")
    private String fileName;
    @JsonProperty("fileDate")
    private Date fileDate;
    @JsonProperty("fileLength")
    private Integer fileLength;
    @JsonProperty("releaseType")
    private Integer releaseType;
    @JsonProperty("fileStatus")
    private Integer fileStatus;
    @JsonProperty("downloadUrl")
    private String downloadUrl;
    @JsonProperty("isAlternate")
    private Boolean isAlternate;
    @JsonProperty("alternateFileId")
    private Integer alternateFileId;
    @JsonProperty("dependencies")
    private List<?> dependencies;
    @JsonProperty("isAvailable")
    private Boolean isAvailable;
    @JsonProperty("modules")
    private List<ModulesBean> modules;
    @JsonProperty("packageFingerprint")
    private Long packageFingerprint;
    @JsonProperty("gameVersion")
    private List<String> gameVersion;
    @JsonProperty("installMetadata")
    private Object installMetadata;
    @JsonProperty("serverPackFileId")
    private Object serverPackFileId;
    @JsonProperty("hasInstallScript")
    private Boolean hasInstallScript;
    @JsonProperty("gameVersionDateReleased")
    private Date gameVersionDateReleased;
    @JsonProperty("gameVersionFlavor")
    private Object gameVersionFlavor;

    @NoArgsConstructor
    @Data
    public static class ModulesBean implements Serializable {
        @JsonProperty("foldername")
        private String foldername;
        @JsonProperty("fingerprint")
        private Long fingerprint;
    }

    @Override
    public int compareTo(Files o) {
        return o.getFileDate().compareTo(getFileDate());
    }

}
