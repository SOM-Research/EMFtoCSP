/*******************************************************************************
 * Copyright (c) 2011 INRIA Rennes Bretagne-Atlantique.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     INRIA Rennes Bretagne-Atlantique - initial API and implementation
 *******************************************************************************/
package fr.inria.atlanmod.emftocsp.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import fr.inria.atlanmod.emftocsp.ILogger;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class FileLogger implements ILogger {
	PrintWriter outputStream; 
	Calendar cal;
	DateFormat dateFormat;
	File tempFile = null;
	
	public FileLogger() {
    dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");   
    try {
      tempFile = File.createTempFile("test", "log");
      tempFile.deleteOnExit();
      outputStream = new PrintWriter(new FileWriter(tempFile.getAbsolutePath()));
      writeMessage("EMFtoCSP LOGS");
      writeMessage("-------------");
    }
    catch (IOException e) {
      e.printStackTrace();
    }
	}
	
	public FileLogger(String filePath) {
	  dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");		
	  try {
	    outputStream = new PrintWriter(new FileWriter(filePath));
	    writeMessage("EMFtoCSP LOGS");
      writeMessage("-------------");
	  }
	  catch (IOException e) {
	    e.printStackTrace();
	  }
	}

  private void writeMessage(String message) {
    outputStream.println(message);
  }	
	
	private void writeMessage(String context, String message, String type) {
	  cal = Calendar.getInstance();
	  StringBuilder s = new StringBuilder();
    s.append(dateFormat.format(cal.getTime()));
    s.append(" ");
    if (!context.equals("")) {
      s.append(context);
      s.append(": ");
    }
    s.append(type);
    s.append(": ");
    s.append(message);
	  outputStream.println(s.toString());
	}
		
	@Override
	public void writeInfoMessage(String context, String infoMessage) {
	  writeMessage(context, infoMessage, "INFO");
	}

	@Override
	public void writeWarningMessage(String context, String warningMessage) {
	  writeMessage(context, warningMessage, "WARNING");
	}

	@Override
	public void writeErrorMessage(String context, String errorMessage) {
	  writeMessage(context, errorMessage, "ERROR");
	}
	
	@Override
	public void close() {
	  try {
	    if (outputStream != null) {
	      writeInfoMessage(this.getClass().toString(), "Closing EMFtoCSP ");
	      outputStream.close();
	    }
	  }
	  catch (Exception e) {
	    e.printStackTrace();
	  }
	}
	
  @Override
	public void close(String path) {
	  FileInputStream from = null;
    FileOutputStream to = null;
    try {
      outputStream.close();
      from = new FileInputStream(tempFile.getAbsolutePath());
      to = new FileOutputStream(new File(path));
      byte[] buffer = new byte[4096];
      int bytesRead;

      while ((bytesRead = from.read(buffer)) != -1)
        to.write(buffer, 0, bytesRead); // write
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (from != null)
        try {
          from.close();
        } catch (IOException e) {
          ;
        }
      if (to != null)
        try {
          to.close();
        } catch (IOException e) {
          ;
        }
    }	  
	}		
}
