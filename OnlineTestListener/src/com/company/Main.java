package com.company;

import com.company.action.SetClipBoardAction;
import com.company.file.FileObject;
import com.company.file.Smb;
import com.company.file.SmbDir;
import com.company.watcher.FileWatcher;
import com.company.watcher.SharedFileWatcher;
import jcifs.smb.SmbFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main
{
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    static class MonitorConfig
    {
        private FileObject target;
        private int interval;

        public MonitorConfig(FileObject target, int interval)
        {
            this.target = target;
            this.interval = interval;
        }

        public static List<MonitorConfig> parseConfig(File config) throws Exception
        {
            List<MonitorConfig> configs = new ArrayList<>();
            Properties proper = new Properties();
            proper.load(new InputStreamReader(new FileInputStream(config), "utf-8"));

            Object intervalSrc = proper.get("监控间隔");
            int interval = intervalSrc == null? 5: Integer.valueOf(intervalSrc.toString());
            String account = proper.getProperty("账户");
            String pwd = proper.getProperty("密码");
            String ip = proper.getProperty("IP");
            final String pattern = proper.getProperty("文件通配符");
            Object targetSrc = proper.get("文件列表");
            String[] targets = targetSrc == null ? new String[0] : targetSrc.toString().split("\\|");

            for (String ta : targets)
            {
                String completeUrl = String.format( "smb://%s%s%s/%s",
                        account == null || account.equals("") ? "": account + ":",
                        pwd == null ||  account.equals("") ? "": pwd + "@",
                        ip,
                        ta);
                Smb smb = new Smb(completeUrl);
                if(smb.isDir())
                {
                    smb = new SmbDir(completeUrl+ (completeUrl.endsWith("/") ? "" : "/"));
                    ((SmbDir)smb).addIncludeRule(new SmbDir.FileFilter()
                    {
                        @Override
                        public boolean isInclude(SmbFile file)
                        {
                            return file.getName().matches(pattern);
                        }
                    });
                }
                configs.add(new MonitorConfig(smb, interval));
            }
            return configs;
        }
    }

    /**
     * 开始所有任务
     * @param tasks
     */
    public static void startAllWatches(List<MonitorConfig> tasks)
    {
        for (MonitorConfig t: tasks)
        {
            threadPool.submit(newWatch(t));
        }
    }

    public static FileWatcher newWatch(MonitorConfig config)
    {
        System.out.println("监听：" + config.target.getPath());
        System.out.println("轮询间隔：" + config.interval + "s\n");
        FileWatcher watcher = new SharedFileWatcher(config.target);
        watcher.setInterval(config.interval);
        watcher.bindFileChangeAction(new SetClipBoardAction());
        return watcher;
    }

    private final static String[] pathToFind = new String[]{"./","../"};

    private static List<File> searchConfig()
    {
        List<File> configs = new ArrayList<>();
        for(String f : pathToFind)
        {
            File dir = new File(f);
            for (File file : dir.listFiles())
            {
                if(file.getAbsolutePath().endsWith("cfg"))
                {
                    configs.add(file);
                }
            }
        }
        return configs;
    }

    public static void main(String[] args)
    {
        List<File> configs = searchConfig();
        if(configs.size() == 0 || configs == null)
        {
            System.err.println("未找到配置文件: .*cfg，程序将退出.");
            return;
        }

        for(File file : configs)
        {
            try
            {
                List<MonitorConfig> configList = MonitorConfig.parseConfig(file);
                startAllWatches(configList);
            }
            catch (Exception e)
            {
                continue;
            }
        }
    }
}
