package ink.ikx.cfp.function;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.StaticLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ink.ikx.cfp.Main;
import ink.ikx.cfp.config.BaseConfig;
import ink.ikx.cfp.entity.Files;
import ink.ikx.cfp.entity.Manifest;
import lombok.SneakyThrows;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Update {

    private Files[] files;
    public static final Manifest MANIFEST = Main.manifest;
    public static final Map<String, Manifest.FilesBean> UPDATED_FILES_LIST = new HashMap<>();

    public Update() {
        call();
    }

    public void call() {
        StaticLog.info("update start");
        var i = 1;
        for (var file : MANIFEST.getFiles()) {
            var s = HttpUtil.get(MessageFormat.format("https://addons-ecs.forgesvc.net/api/v2/addon/{0}/files",
                    file.getProjectID().toString()));
            ObjectMapper om = new ObjectMapper();

            try {
                files = om.readValue(s, Files[].class);
            } catch (JsonProcessingException e) {
                StaticLog.error("This projectID " + file.getProjectID() + " does not exist.", e);
                break;
            }
            var skipList = BaseConfig.INSTANCE.getSkips().stream()
                    .filter(skip -> files[0].getDisplayName().toLowerCase().contains(skip))
                    .collect(Collectors.toList());
            if (!skipList.isEmpty()) continue;
            StaticLog.info("check mod update, " + (i == 1 ? i + " mod" : i + " mods") + " have been detected");
            getUpdated(file.getFileID(), file.getProjectID());
            i++;
        }
        updateMod();
    }

    public void getUpdated(Integer fileID, Integer projectID) {
        var file = Arrays.stream(this.files)
                .sorted()
                .filter(f -> f.getGameVersion().contains(MANIFEST.getMinecraft().getVersion()))
                .findFirst().orElse(null);

        if (Objects.nonNull(file) && !Objects.equals(file.getId(), fileID)) {
            UPDATED_FILES_LIST.put(file.getDisplayName(), new Manifest.FilesBean(projectID, file.getId(), true));
        }
    }

    @SneakyThrows
    public void updateMod() {
        if (UPDATED_FILES_LIST.isEmpty()) return;
        for (Map.Entry<String, Manifest.FilesBean> entry : UPDATED_FILES_LIST.entrySet()) {
            var updatedFile = entry.getValue();
            var flag = MANIFEST.getFiles().removeIf(file -> Objects.equals(file.getProjectID(), updatedFile.getProjectID()) && !Objects.equals(file.getFileID(), updatedFile.getFileID()));

            if (flag) {
                Files file = Arrays.stream(this.files).filter(f -> Objects.equals(f.getId(), updatedFile.getFileID())).findFirst().orElse(null);
                MANIFEST.getFiles().add(updatedFile);
                StaticLog.info(entry.getKey() + " Updated");
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(MANIFEST);

        FileUtil.writeUtf8String(json, BaseConfig.INSTANCE.getManifest());
    }
}
