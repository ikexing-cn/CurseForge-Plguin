package ink.ikx.cfp;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import ink.ikx.cfp.config.BaseConfig;

public class CurseForgePlugin {

    public static final Log LOGGER = LogFactory.get("CurseForge-Plugin");

    public static void main(String[] args) {
        BaseConfig.INSTANCE.getFile();
    }

}
