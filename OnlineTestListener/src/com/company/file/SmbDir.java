package com.company.file;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Y on 2018/9/12.
 */
public class SmbDir extends Smb
{
    public static abstract class FileFilter
    {
        public abstract boolean isInclude(SmbFile file);
    }

    public void addIncludeRule(FileFilter fileFilter)
    {
        this.includeCondition = fileFilter;
    }

    private Map<SmbFile,Long> fileModifyMap = new HashMap<>();
    private SmbFile lastModifyFile = null;
    private volatile boolean init = false;
    private FileFilter includeCondition;

    public SmbDir(String path) throws Exception
    {
        super(path);
    }

    @Override
    public boolean isChange() throws SmbException
    {
        List<SmbFile> files = Utils.getFilesRecusive(smbfile);
        for(SmbFile file : files)
        {
            long modify = file.getLastModified();
            //已有的文件发生变化
            if(init && (includeCondition.isInclude(file) && fileModifyMap.containsKey(file) && modify != fileModifyMap.get(file)))
            {
                this.lastModifyFile = file;
                fileModifyMap.put(file, modify);
                return true;
            }
            if(includeCondition.isInclude(file))
            {
                fileModifyMap.put(file, modify);
            }
        }
        init = true;
        return false;
    }

    @Override
    public String getContent()
    {
        if(lastModifyFile!=null)
        {
            return Utils.readContent(lastModifyFile);
        }
        return "";
    }

    @Override
    public boolean isDir()
    {
        return true;
    }
}
