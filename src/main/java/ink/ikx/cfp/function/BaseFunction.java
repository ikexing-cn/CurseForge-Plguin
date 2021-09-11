package ink.ikx.cfp.function;

import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ink.ikx.cfp.entity.Files;
import ink.ikx.cfp.entity.Manifest;
import ink.ikx.cfp.utils.Utils;

import java.text.MessageFormat;

public class BaseFunction {

    public Files[] getFiles(Manifest.FilesBean file) {
        var s = HttpUtil.get(MessageFormat.format("https://addons-ecs.forgesvc.net/api/v2/addon/{0}/files",
                file.getProjectID().toString()));
        var om = new ObjectMapper();

        Files[] files;
        try {
            files = om.readValue(s, Files[].class);
        } catch (JsonProcessingException e) {
            Utils.errorLog("This projectID " + file.getProjectID() + " Does Not Exist.", e);
            return null;
        }
        return files;
    }

}
