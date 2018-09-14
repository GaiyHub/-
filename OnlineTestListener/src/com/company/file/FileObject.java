package com.company.file;

/**
 * Created by Y on 2018/9/12.
 */
public interface FileObject
{
    String getContent() throws Exception;

    boolean isChange() throws Exception;

    String getPath();

    boolean isDir() throws Exception;
}
