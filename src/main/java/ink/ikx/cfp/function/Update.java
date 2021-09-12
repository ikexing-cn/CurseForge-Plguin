package ink.ikx.cfp.function;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import ink.ikx.cfp.Main;
import ink.ikx.cfp.config.BaseConfig;
import ink.ikx.cfp.entity.Files;
import ink.ikx.cfp.entity.Manifest;
import ink.ikx.cfp.utils.Utils;
import lombok.SneakyThrows;
import lombok.val;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Update extends BaseFunction {

    public static final Map<String, Manifest.FilesBean> UPDATED_FILES_LIST = new HashMap<>();



    @SneakyThrows
    public Update() {
        Utils.infoLog("Update Start");
        long start = System.currentTimeMillis();

        var begin = new CountDownLatch(1);
        var end = new CountDownLatch(Main.manifest.getFiles().size());
        var exec = Executors.newFixedThreadPool(BaseConfig.INSTANCE.getThreads());

        for (var i = 0; i < Main.manifest.getFiles().size(); i++) {
            var file = Main.manifest.getFiles().get(i);
            int finalI = i;
            Runnable run = () -> {
                try {
                    begin.await();
                    var files = getFiles(file);
                    if (Objects.isNull(files)) return;
                    call(files, finalI, file);
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

    public void call(Files[] files, int i, Manifest.FilesBean file) {
        var skipList = BaseConfig.INSTANCE.getSkips().stream()
                .filter(StrUtil::isNotBlank)
                .filter(skip -> files[0].getDisplayName().toLowerCase().contains(skip))
                .collect(Collectors.toList());
        if (!skipList.isEmpty()) return;

        Utils.infoLog("Check Mod Update, " + (i == 1 ? i + " Mod" : i + " Mods") + " Have Been Detected");
        getUpdated(file.getFileID(), file.getProjectID(), files);
    }

    public void getUpdated(Integer fileID, Integer projectID, Files[] files) {
        var file = Arrays.stream(files)
                .sorted()
                .filter(f -> f.getGameVersion().contains(Main.manifest.getMinecraft().getVersion()))
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
        ExecutorService exec = Executors.newFixedThreadPool(BaseConfig.INSTANCE.getThreads());
        for (val entry : UPDATED_FILES_LIST.entrySet()) {
            var updatedFile = entry.getValue();
            var flag = Main.manifest.getFiles().removeIf(file -> Objects.equals(file.getProjectID(), updatedFile.getProjectID()) && !Objects.equals(file.getFileID(), updatedFile.getFileID()));

            if (flag) {
                Main.manifest.getFiles().add(updatedFile);
                exec.submit(() -> {
                    Files file = Arrays.stream(getFiles(updatedFile)).sorted().findFirst().orElse(null);

                    Downloader.call(Objects.requireNonNull(file).getDownloadUrl());
                    Utils.infoLog("Update " + entry.getKey().split("-")[0] + "Mod to The Last Version"); //理论上应该没问题
                });
                i++;
            }
        }

        exec.shutdown();

        var objectMapper = new ObjectMapper();
        var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(Main.manifest);

        FileUtil.writeUtf8String(json, BaseConfig.INSTANCE.getManifest());
        Utils.infoLog((i == 1 ? i + " Mod" : i + " Mods") + " Waiting Update");
    }
}
