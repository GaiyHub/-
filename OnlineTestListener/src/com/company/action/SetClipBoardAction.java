package com.company.action;

import com.company.watcher.FileWatcher;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

/**
 * Created by Y on 2018/9/12.
 */
public class SetClipBoardAction implements FileChangeAction
{
    private static final String defaultSoundUrlPath = "./sound.wav";

    private static URL defaultSoundUrl;

    static
    {
        try
        {
            defaultSoundUrl = SetClipBoardAction.class.getResource(defaultSoundUrlPath);
            File optionalFile = new File(defaultSoundUrlPath);
            if(defaultSoundUrl == null || optionalFile.exists())
            {
                defaultSoundUrl = optionalFile.toURL();
            }
        }
        catch (Exception e)
        {}
    }

    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private FileWatcher watcher;

    @Override
    public void setTarget(FileWatcher target)
    {
        this.watcher = target;
    }

    @Override
    public void changeHandle()
    {
        try
        {
            Transferable tText = new StringSelection(this.watcher.getContent().toString());
            synchronized (clipboard)
            {
                clipboard.setContents(tText, null);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void afterChange()
    {
        synchronized (SetClipBoardAction.class)
        {
            AudioClip aau = Applet.newAudioClip(defaultSoundUrl);
            aau.play();
        }
    }
}
