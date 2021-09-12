package ink.ikx.cfp.function;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import ink.ikx.cfp.Main;
import ink.ikx.cfp.utils.Utils;

public class Downloader extends BaseFunction {

    public static final String MODS = Utils.getPath(Main.CONFIG_DIR, "Mods");

    static {
        if (FileUtil.isEmpty(FileUtil.file(MODS))) FileUtil.mkdir(MODS);
    }

    public static void call(String url) {
        try {
            HttpUtil.downloadFile(url, MODS);
        } catch (Exception e) {
            Utils.errorLog("fail: ", e);
        }
    }

}
