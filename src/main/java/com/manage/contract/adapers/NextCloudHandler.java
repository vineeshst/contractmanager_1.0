package com.manage.contract.adapers;

import com.github.sardine.Sardine;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.aarboard.nextcloud.api.utils.WebdavInputStream;
import org.aarboard.nextcloud.api.webdav.AWebdavHandler;

import java.io.IOException;
import java.io.InputStream;

public class NextCloudHandler extends AWebdavHandler {
    public NextCloudHandler(ServerConfig serverConfig) {
        super(serverConfig);
    }
    /**
     * Creates a folder at the specified path
     *
     * @param remotePath path of the folder
     */
    public void createFolder(String remotePath)
    {
        String path=  buildWebdavPath(remotePath );
        Sardine sardine = buildAuthSardine();

        try {
            sardine.createDirectory(path);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
        finally
        {
            try
            {
                sardine.shutdown();
            }
            catch (IOException ex)
            {
//                LOG.warn("error in closing sardine connector", ex);
                System.out.println("error in closing sardine connector");
            }
        }
    }

    public InputStream downloadFile(String remotePath) throws IOException {
        String path = buildWebdavPath(remotePath);
        Sardine sardine = buildAuthSardine();

        WebdavInputStream in = null;
        try
        {
            in = new WebdavInputStream(sardine, sardine.get(path));
        } catch (IOException e)
        {
            sardine.shutdown();
            throw new NextcloudApiException(e);
        }
        return in;
    }
    public void uploadFile(InputStream inputStream, String remotePath) {
        String path = buildWebdavPath(remotePath);
        Sardine sardine = buildAuthSardine();

        try
        {
            sardine.put(path, inputStream);
        } catch (IOException e)
        {
            throw new NextcloudApiException(e);
        }
    }

    public void uploadFile(InputStream inputStream, String remotePath, boolean continueHeader) {
        String path = buildWebdavPath(remotePath);
        Sardine sardine = buildAuthSardine();

        try
        {
            sardine.put(path, inputStream, null, continueHeader);
        } catch (IOException e)
        {
            throw new NextcloudApiException(e);
        }
    }

}
