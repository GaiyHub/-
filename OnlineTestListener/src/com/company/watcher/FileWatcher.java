package com.company.watcher;
import com.company.action.FileChangeAction;
import com.company.file.FileObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Y on 2018/9/12.
 */
public abstract class FileWatcher implements Runnable
{
    protected FileObject file;

    private List<FileChangeAction> actions = new ArrayList<>();

    protected abstract boolean fileChange() throws Exception;

    public abstract Object getContent() throws Exception;

    private long interval = 2;

    public FileWatcher(FileObject file)
    {
        this.file = file;
    }

    public void setInterval(int interval)
    {
        this.interval = interval;
    }

    public void bindFileChangeAction(FileChangeAction action)
    {
        action.setTarget(this);
        this.actions.add(action);
    }

    @Override
    public void run()
    {
        while (true)
        {
            //文件发生变化
            try
            {
                if(this.fileChange())
                {
                    for(FileChangeAction action : actions)
                    {
                        action.changeHandle();
                        action.afterChange();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                break;
            }
            //睡眠
            try
            {
                TimeUnit.SECONDS.sleep(interval);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
