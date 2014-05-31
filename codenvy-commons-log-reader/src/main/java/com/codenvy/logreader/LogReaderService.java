/*******************************************************************************
* Copyright (c) 2012-2014 Codenvy, S.A.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Codenvy, S.A. - initial API and implementation
*******************************************************************************/
package com.codenvy.logreader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("{ws-name}/log-reader-service/")
public class LogReaderService {

    private final LogPathProvider logPathProvider;

    public LogReaderService() {
        this(new SystemLogPathProvider());
    }

    public LogReaderService(LogPathProvider logPathProvider) {
        this.logPathProvider = logPathProvider;
    }

    @GET
    @Path("last-log")
    @Produces(MediaType.APPLICATION_JSON)
    public LogEntry getLastLog() throws LogReaderException {
        LogReader logReader = new LogReader(logPathProvider.getLogDirectory());
        String token = logReader.getLastToken();
        if (token != null) {
            return new LogEntry(token, logReader.getLogByToken(token), logReader.hasNextToken(token),
                                logReader.hasPrevToken(token));
        } else {
            throw new LogReaderException("No logs found.");
        }
    }

    @GET
    @Path("log")
    @Produces(MediaType.APPLICATION_JSON)
    public LogEntry getLog(@QueryParam("lrtoken") String token) throws LogReaderException {

        LogReader logReader = new LogReader(logPathProvider.getLogDirectory());
        if (!token.equals("null")) {
            return new LogEntry(token, logReader.getLogByToken(token), logReader.hasNextToken(token),
                                logReader.hasPrevToken(token));
        } else {
            throw new LogReaderException("Token must not be null.");
        }
    }

    @GET
    @Path("prev-log")
    @Produces(MediaType.APPLICATION_JSON)
    public LogEntry getPrevLog(@QueryParam("lrtoken") String token) throws LogReaderException {

        LogReader logReader = new LogReader(logPathProvider.getLogDirectory());
        String newToken = logReader.getPrevToken(token);
        if (newToken != null) {
            return new LogEntry(newToken, logReader.getLogByToken(newToken), logReader.hasNextToken(newToken),
                                logReader.hasPrevToken(newToken));
        } else {
            throw new LogReaderException("Previous token not found.");
        }
    }

    @GET
    @Path("next-log")
    @Produces(MediaType.APPLICATION_JSON)
    public LogEntry getNextLog(@QueryParam("lrtoken") String token) throws LogReaderException {
        LogReader logReader = new LogReader(logPathProvider.getLogDirectory());
        String newToken = logReader.getNextToken(token);
        if (newToken != null) {
            return new LogEntry(newToken, logReader.getLogByToken(newToken), logReader.hasNextToken(newToken),
                                logReader.hasPrevToken(newToken));
        } else {
            throw new LogReaderException("Next token not found.");
        }
    }

}
