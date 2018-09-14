package com.company.file;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Y on 2018/9/12.
 */
public class Smb implements FileObject
{
    protected SmbFile smbfile;
    protected String path;
    protected long lastModify;

    public Smb(String path) throws Exception
    {
        this.path = path;
        smbfile = new SmbFile(path);
        lastModify = smbfile.lastModified();
    }

    @Override
    public String getContent()
    {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(smbfile), "utf-8")))
        {
            {
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while (null != (line = reader.readLine()))
                {
                    stringBuilder.append(line).append("\n");
                }
                return stringBuilder.toString();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean isChange() throws SmbException
    {
        try
        {
            smbfile.connect();
        }
        catch (IOException e)
        {
            return false;
        }
        long modify = smbfile.getLastModified();
        boolean isModify = modify != lastModify;
        if(isModify)
        {
            this.lastModify = modify;
        }
        return isModify;
    }

    @Override
    public String getPath()
    {
        return this.path;
    }

    @Override
    public boolean isDir()
    {
        try
        {
            return smbfile.isDirectory();
        }
        catch (SmbException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
