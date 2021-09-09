package ink.ikx.cfp.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.setting.dialect.Props;
import ink.ikx.cfp.utils.Utils;

import java.io.File;
import java.io.IOException;

public class BaseConfig {

    public static final String CONFIG_DIR = Utils.getPath(System.getProperty("user.dir"), "config");
    public static final BaseConfig INSTANCE = new BaseConfig();

    public static Props props;

    public BaseConfig() {
        this.createFile();
        this.initConfig();
    }

    public void initConfig() {
        props = new Props(getFile());
        if (props.get("Type") == null) props.setProperty("Type", "Update");

        props.autoLoad(true);
        props.store(getFile());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void createFile() {
        FileUtil.mkdir(CONFIG_DIR);
        File file = FileUtil.newFile(getFile());
        if (FileUtil.isEmpty(file)) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {

            }
        }
    }

    public String getFile() {
        return Utils.getPath(CONFIG_DIR, "base.setting");
    }

    public Type getType() {
        return Type.valueOf(props.getStr("Type").toUpperCase());
    }

    public enum Type {
        UPDATE, ADD
    }
}
