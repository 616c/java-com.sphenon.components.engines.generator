package com.sphenon.engines.generator.tchandler.classes;

/****************************************************************************
  Copyright 2001-2018 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/

import com.sphenon.basics.context.*;
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.data.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tom.classes.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.Stack;
import java.util.Vector;

public class TCHRegExpPP implements TCHandler {

    public TCHRegExpPP (CallContext context) {
    }

    protected TCHandler handler;

    public TCHandler getHandler (CallContext context) {
        return this.handler;
    }

    public void setHandler (CallContext context, TCHandler handler) {
        this.handler = handler;
    }

    protected boolean unicode;

    public boolean getUnicode (CallContext context) {
        return this.unicode;
    }

    public void setUnicode (CallContext context, boolean unicode) {
        this.unicode = unicode;
    }

    protected Vector<RegularExpression> processLine(CallContext context, StringBuffer buffer, StringBuffer big_buffer, Vector<RegularExpression> regexps, boolean newline) {

        String line = buffer.toString();

        if (line.startsWith(this.unicode ? "Ⓡ" : "~~~")) {
            if (regexps == null) {
                regexps = new Vector<RegularExpression>();
            }
            String[] parts = line.substring(this.unicode ? 1 : 3).split("=",2);
            regexps.add(new RegularExpression(context, parts[0], parts[1]));
        } else {
            if (regexps != null) {
                for (RegularExpression regexp : regexps) {
                    line = regexp.replaceAll(context, line);
                }
            }
            big_buffer.append(line);
            if (newline) { big_buffer.append('\n'); }
        }
        
        buffer.setLength(0);

        return regexps;
    }

    public TOMNode handle(CallContext context, TCEvent event, TOMNode current_node, BufferedReader reader) throws InvalidTemplateSyntax{
        if (reader == null) { return current_node; }

        StringBuffer buffer     = new StringBuffer();
        StringBuffer big_buffer = new StringBuffer();

        Vector<RegularExpression> regexps = null;

        int c;
        try {
            while ((c = reader.read()) != -1) {
                if (c == '\n') {
                    regexps = processLine(context, buffer, big_buffer, regexps, true);
                } else {
                    buffer.append((char) c);
                }
            }
            regexps = processLine(context, buffer, big_buffer, regexps, false);
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "In memory string/stream operation failed");
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }

        StringReader string_reader = new StringReader(big_buffer.toString());
        BufferedReader buffered_reader = new BufferedReader(string_reader);
        return this.handler.handle(context, event, current_node, buffered_reader);
    }
}
