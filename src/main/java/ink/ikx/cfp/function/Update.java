package ink.ikx.cfp.function;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ink.ikx.cfp.Main;
import ink.ikx.cfp.config.BaseConfig;
import ink.ikx.cfp.entity.Files;
import ink.ikx.cfp.entity.Manifest;
import ink.ikx.cfp.utils.Utils;
import lombok.SneakyThrows;
import lombok.val;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Update {

    private Files[] files;
    public static final Manifest MANIFEST = Main.manifest;
    public static final Map<String, Manifest.FilesBean> UPDATED_FILES_LIST = new HashMap<>();

    @SneakyThrows
    public Update() {
        Utils.infoLog("Update Start");
        long start = System.currentTimeMillis();
        final CountDownLatch begin = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(MANIFEST.getFiles().size());
        ExecutorService exec = Executors.newFixedThreadPool(BaseConfig.INSTANCE.getThreads());

        for (var i = 0; i < MANIFEST.getFiles().size(); i++) {
            Manifest.FilesBean file = MANIFEST.getFiles().get(i);
            int finalI = i;
            Runnable run = () -> {
                try {
                    begin.await();
                    call(file, finalI);
                } catch (InterruptedException ignored) {
                } finally {
                    end.countDown();
                }
            };
            exec.submit(run);
        }
        begin.countDown();
        end.await();
        exec.shutdown();
        updateMod();
    }

    public void call(Manifest.FilesBean file, int i) {

        var s = HttpUtil.get(MessageFormat.format("https://addons-ecs.forgesvc.net/api/v2/addon/{0}/files",
                file.getProjectID().toString()));
        ObjectMapper om = new ObjectMapper();

        try {
            files = om.readValue(s, Files[].class);
        } catch (JsonProcessingException e) {
            Utils.errorLog("This projectID " + file.getProjectID() + " Does Not Exist.", e);
            return;
        }
        var skipList = BaseConfig.INSTANCE.getSkips().stream()
                .filter(StrUtil::isNotBlank)
                .filter(skip -> files[0].getDisplayName().toLowerCase().contains(skip))
                .collect(Collectors.toList());
        if (!skipList.isEmpty()) return;
        Utils.infoLog("Check Mod Update, " + (i == 1 ? i + " Mod" : i + " Mods") + " Have Been Detected");
        getUpdated(file.getFileID(), file.getProjectID());
    }

    public void getUpdated(Integer fileID, Integer projectID) {
        var file = Arrays.stream(this.files)
                .sorted()
                .filter(f -> f.getGameVersion().contains(MANIFEST.getMinecraft().getVersion()))
                .findFirst().orElse(null);

        if (Objects.nonNull(file) && !Objects.equals(file.getId(), fileID)) {
            UPDATED_FILES_LIST.put(file.getFileName(), new Manifest.FilesBean(projectID, file.getId(), true));
        }
    }

    @SneakyThrows
    public void updateMod() {
        if (UPDATED_FILES_LIST.isEmpty()) {
            Utils.infoLog("No Mod Need To Be Updated");
            return;
        }
        int i = 0;
        for (val entry : UPDATED_FILES_LIST.entrySet()) {
            var updatedFile = entry.getValue();
            var flag = MANIFEST.getFiles().removeIf(file -> Objects.equals(file.getProjectID(), updatedFile.getProjectID()) && !Objects.equals(file.getFileID(), updatedFile.getFileID()));

            if (flag) {
                Files file = Arrays.stream(this.files).filter(f -> Objects.equals(f.getId(), updatedFile.getFileID())).findFirst().orElse(null);
                MANIFEST.getFiles().add(updatedFile);
                Utils.infoLog("update" + entry.getKey());
                i++;
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(MANIFEST);

        FileUtil.writeUtf8String(json, BaseConfig.INSTANCE.getManifest());
        Utils.infoLog((i == 1 ? i + " Mod" : i + " Mods") + " Updated");
    }
}
