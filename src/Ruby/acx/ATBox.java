/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import Ruby.DBClient;

/**
 *
 * @author Pecherk
 */
public class ATBox
{
    private APLog log;
    private final AXFile xfile = new AXFile();
    private final AXCrypt xcrypt = new AXCrypt();
    
    private final AXWorker worker = new AXWorker(this);
    private final DBClient dclient = new DBClient(this);
    private final AXClient xclient = new AXClient(this);

    public ATBox(APLog log)
    {
        this.log = log;
    }

    public void dispose()
    {
        getdClient().dispose();
    }

    public AXWorker getWorker()
    {
        return worker;
    }

    public DBClient getdClient()
    {
        return dclient;
    }

    public APLog getLog()
    {
        return log;
    }

    public <T> T getLog(Class<T> clazz)
    {
        return (T) log;
    }

    public void setLog(APLog log)
    {
        this.log = log;
    }

    public AXCrypt getXcrypt()
    {
        return xcrypt;
    }

    public AXFile getxFile()
    {
        return xfile;
    }

    /**
     * @return the xclient
     */
    public AXClient getXclient()
    {
        return xclient;
    }
}
