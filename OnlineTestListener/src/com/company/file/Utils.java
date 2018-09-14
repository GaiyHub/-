package com.company.file;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Y on 2018/9/12.
 */
public class Utils
{
    public static List<SmbFile> getFilesRecusive(SmbFile topDir) throws SmbException
    {
        List<SmbFile> fileList = new ArrayList<>();
        getFilesRecusive(topDir, fileList);
        return fileList;
    }

    public static void getFilesRecusive(SmbFile topDir,List<SmbFile> files) throws SmbException
    {
        if(topDir.isFile())
        {
            files.add(topDir);
        }
        else
        {
            for(SmbFile f : topDir.listFiles())
            {
                getFilesRecusive(f, files);
            }
        }
    }

    public static String readContent(SmbFile file)
    {
        List<String> stringList = readLines(file);
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : stringList)
        {
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }

    public static List<String> readLines(SmbFile file)
    {
        List<String> stringList = new ArrayList<>();
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(file),"utf-8")))
        {
            String line;
            while ((line = bufferedReader.readLine())!=null)
            {
                stringList.add(line + "\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stringList;
    }

    public static List<String> readLines(File file) throws FileNotFoundException
    {
        if(!file.exists() || !file.isFile())
        {
            throw new FileNotFoundException("File " + file.getName() + " not exist.");
        }
        List<String> stringList = new ArrayList<>();
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8")))
        {
            String line;
            while ((line = bufferedReader.readLine())!=null)
            {
                stringList.add(line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stringList;
    }
}
