package com.company.watcher;

import com.company.file.FileObject;

/**
 * Created by Y on 2018/9/12.
 */
public class SharedFileWatcher extends FileWatcher
{
    public SharedFileWatcher(FileObject file)
    {
        super(file);
    }

    @Override
    public Object getContent() throws Exception
    {
        return file.getContent();
    }

    @Override
    protected boolean fileChange() throws Exception
    {
        return file.isChange();
    }
}
