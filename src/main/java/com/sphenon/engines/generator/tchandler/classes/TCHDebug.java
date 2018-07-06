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
import com.sphenon.basics.data.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tom.classes.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;

public class TCHDebug implements TCHandler {

    public TCHDebug (CallContext context) {
    }

    protected String prefix;

    public String getPrefix (CallContext context) {
        return this.prefix;
    }

    public void setPrefix (CallContext context, String prefix) {
        this.prefix = prefix;
    }

    protected TCHandler handler;

    public TCHandler getHandler (CallContext context) {
        return this.handler;
    }

    public TCHandler defaultHandler (CallContext context) {
        return null;
    }

    public void setHandler (CallContext context, TCHandler handler) {
        this.handler = handler;
    }

    public TOMNode handle(CallContext context, TCEvent event, TOMNode current_node, BufferedReader reader) throws InvalidTemplateSyntax {
        System.err.print(this.prefix + ": ");
        StringBuffer sb = (this.handler == null ? null : new StringBuffer());
        int c;
        try {
            while ((c = reader.read()) != -1) {
                if (sb != null) { sb.append((char) c); }
                switch (c) {
                    case '\n':
                        System.err.println();
                        System.err.print(this.prefix + ": ");
                        break;
                    default :
                        System.err.print((char) c);
                        break;
                }
            }
        } catch (IOException ioe) {
            System.err.println();
            ioe.printStackTrace();
        }
        System.err.println();

        if (this.handler == null) {
            return current_node;
        } else {
            StringReader string_reader = new StringReader(sb.toString());
            BufferedReader my_buffered_reader = new BufferedReader(string_reader);
            return this.handler.handle(context, event, current_node, my_buffered_reader);
        }
    }
}
