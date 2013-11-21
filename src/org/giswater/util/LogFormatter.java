/*
 * This file is part of Giswater
 * Copyright (C) 2013 Tecnics Associats
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author:
 *   David Erill <daviderill79@gmail.com>
 */
package org.giswater.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;


public class LogFormatter extends SimpleFormatter{
 
    @Override
    public String format(LogRecord record) {

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        long now = record.getMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);

        String output = formatter.format(calendar.getTime()) + " (" + record.getLevel() + "): "
            + record.getSourceClassName() + " " + record.getSourceMethodName() + "\n"
            + record.getMessage() + "\n\n";
        
        return output;
        
   }
    
}