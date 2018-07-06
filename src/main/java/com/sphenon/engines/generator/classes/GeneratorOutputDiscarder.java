package com.sphenon.engines.generator.classes;

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
import com.sphenon.basics.notification.classes.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.data.*;

import com.sphenon.engines.generator.*;

import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;

import java.util.Vector;

public class GeneratorOutputDiscarder implements GeneratorOutputHandler {

    public GeneratorOutputDiscarder (CallContext context) {
    }

    protected Writer         writer;
    protected EncodingWriter encoding_writer;
    public    PrintWriter    print_writer;

    public EncodingWriter getWriter (CallContext context) {
        if (this.encoding_writer == null) {
            this.writer = new Writer() {
                    public void close() { }
                    public void flush() { }
                    public void write(char[] cbuf, int off, int len) { }
                };
            this.encoding_writer = new EncodingWriter(context, this.writer);
        }
        return this.encoding_writer;
    }

    public PrintWriter getPrintWriter (CallContext context) {
        if (print_writer == null) {
            print_writer = new PrintWriter(this.getWriter(context));
        }
        return this.print_writer;
    }

    public void openHandler (CallContext context) {
    }

    public void closeHandler (CallContext context) {
    }

    public void closeCurrent (CallContext context) {
    }

    public String redirectDefaultWriter (CallContext context, String channel_name) {
        return "";
    }

    public String redirectDefaultWriter (CallContext context, String channel_name, boolean do_not_modify) {
        return redirectDefaultWriter (context, channel_name);
    }

    public Boolean isContentDiffering(CallContext context) {
        return null;
    }

    public Vector<String> getChannelNames(CallContext context) {
        return null;
    }
}
