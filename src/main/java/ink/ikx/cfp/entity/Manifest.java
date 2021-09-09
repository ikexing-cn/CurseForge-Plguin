
package ink.ikx.cfp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class Manifest implements Serializable {

    private MinecraftBean minecraft;
    private String manifestType;
    private Integer manifestVersion;
    private String name;
    private String author;
    private List<FilesBean> files;
    private String overrides;

    @Data
    @NoArgsConstructor
    public static class MinecraftBean implements Serializable {
        private String version;
        private List<ModLoadersBean> modLoaders;

        @Data
        @NoArgsConstructor
        public static class ModLoadersBean implements Serializable {
            private String id;
            private Boolean primary;
        }
    }

    @Data
    @NoArgsConstructor
    public static class FilesBean implements Serializable {
        private Integer projectID;
        private Integer fileID;
        private Boolean required;
    }

}