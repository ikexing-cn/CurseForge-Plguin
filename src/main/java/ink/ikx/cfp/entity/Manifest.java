
package ink.ikx.cfp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
public class Manifest implements Serializable {

    @JsonProperty("minecraft")
    private MinecraftBean minecraft;
    @JsonProperty("manifestType")
    private String manifestType;
    @JsonProperty("manifestVersion")
    private Integer manifestVersion;
    @JsonProperty("name")
    private String name;
    @JsonProperty("author")
    private String author;
    @JsonProperty("files")
    private List<FilesBean> files;
    @JsonProperty("overrides")
    private String overrides;

    @NoArgsConstructor
    @Data
    public static class MinecraftBean implements Serializable {
        @JsonProperty("version")
        private String version;
        @JsonProperty("modLoaders")
        private List<ModLoadersBean> modLoaders;

        @NoArgsConstructor
        @Data
        public static class ModLoadersBean implements Serializable {
            @JsonProperty("id")
            private String id;
            @JsonProperty("primary")
            private Boolean primary;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class FilesBean implements Serializable {
        @JsonProperty("projectID")
        private Integer projectID;
        @JsonProperty("fileID")
        private Integer fileID;
        @JsonProperty("required")
        private Boolean required;
    }
}