package ink.ikx.cfp.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.setting.dialect.Props;
import ink.ikx.cfp.Main;
import ink.ikx.cfp.utils.Utils;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BaseConfig {

    public static final BaseConfig INSTANCE = new BaseConfig();

    public static Props props;

    public BaseConfig() {
        this.createFile();
        this.initConfig();
    }

    public void initConfig() {
        props = new Props(getFile());
        if (props.get("Type") == null) props.setProperty("Type", "Update");

        props.store(getFile());
        props.clone();
    }

    @SneakyThrows
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void createFile() {
        FileUtil.mkdir(Main.CONFIG_DIR);
        File file = FileUtil.newFile(getFile());
        if (FileUtil.isEmpty(file)) {
            file.createNewFile();
        }
    }

    public void execute() {
        ReflectUtil.newInstance("ink.ikx.cfp.function." + Utils.singleWordToCamel(getType().toString()));
    }

    public List<String> getSkips() {
        return Arrays.stream(props.getStr("Skips").split(",")).map(String::toLowerCase).collect(Collectors.toList());
    }

    public String getManifest() {
        return props.getStr("Manifest");
    }

    public String getFile() {
        return Utils.getPath(Main.CONFIG_DIR, "base.setting");
    }

    public Type getType() {
        return Type.valueOf(props.getStr("Type").toUpperCase());
    }

    public enum Type {
        UPDATE, DOWNLOAD
    }
}
