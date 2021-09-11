package ink.ikx.cfp.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
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
        if (props.get("Types") == null) props.setProperty("Types", "Update");
        if (props.get("Skips") == null) props.setProperty("Skips", "");
        if (props.get("Threads") == null) props.setProperty("Threads", 8);

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
        getTypes().stream().map(t -> "ink.ikx.cfp.function." + Utils.singleWordToCamel(t.toString())).forEach(ReflectUtil::newInstance);
    }

    public List<String> getSkips() {
        return Arrays.stream(props.getStr("Skips").split(",")).map(String::toLowerCase).collect(Collectors.toList());
    }

    public String getManifest() {
        return StrUtil.isBlank(props.getStr("Manifest")) ? Main.DEFAULT_MANIFEST_FILE : props.getStr("Manifest");
    }

    public int getThreads() {
        return props.getInt("Threads");
    }

    public String getFile() {
        return Utils.getPath(Main.CONFIG_DIR, "base.setting");
    }

    public List<Type> getTypes() {
        return Arrays.stream(props.getStr("Types").split(",")).map(String::toUpperCase).map(Type::valueOf).collect(Collectors.toList());
    }

    public enum Type {
        UPDATE, DOWNLOADER
    }
}
