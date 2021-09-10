package ink.ikx.cfp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import ink.ikx.cfp.config.BaseConfig;
import ink.ikx.cfp.entity.Manifest;
import ink.ikx.cfp.utils.Utils;
import lombok.SneakyThrows;

public class Main {

    public static final String CONFIG_DIR = Utils.getPath(System.getProperty("user.dir"), "config");
    public static final String DEFAULT_MANIFEST_FILE = Utils.getPath(CONFIG_DIR, "manifest.json");

    public static Manifest manifest;

    @SneakyThrows
    public static void main(String[] args) {
        ObjectMapper om = new ObjectMapper();
        if (FileUtil.isNotEmpty(FileUtil.newFile(DEFAULT_MANIFEST_FILE))) {
            manifest = om.readValue(FileUtil.newFile(DEFAULT_MANIFEST_FILE), Manifest.class);
        } else {
            if (!StrUtil.isBlank(BaseConfig.INSTANCE.getManifest())) {
                manifest = om.readValue(BaseConfig.INSTANCE.getManifest(), Manifest.class);
            }
        }

        BaseConfig.INSTANCE.execute();
    }
}
