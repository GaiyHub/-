package com.company.action;

import com.company.watcher.FileWatcher;

/**
 * Created by Y on 2018/9/12.
 */
public interface FileChangeAction
{
    void setTarget(FileWatcher target);

    void changeHandle();

    void afterChange();
}
