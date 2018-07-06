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
import com.sphenon.basics.customary.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.factories.*;
import com.sphenon.basics.validation.returncodes.*;

import com.sphenon.engines.generator.*;

import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.io.IOException;

import java.util.Vector;
import java.util.HashMap;

public class GeneratorOutputToWriter implements GeneratorOutputHandler {

    protected Writer         writer;
    protected EncodingWriter encoding_writer;
    protected PrintWriter    print_writer;
    protected boolean        do_close;

    public GeneratorOutputToWriter (CallContext context, Writer writer, boolean do_close) {
        this.writer = writer;
        this.encoding_writer = new EncodingWriter(context, this.writer);
        this.do_close = do_close;
    }

    protected int usage_count;

    public EncodingWriter getWriter (CallContext context) {
        return this.encoding_writer;
    }

    public PrintWriter getPrintWriter (CallContext context) {
        if (this.print_writer == null) {
            this.print_writer = new PrintWriter(this.getWriter(context));
        }
        return this.print_writer;
    }

    public void openHandler (CallContext context) {
        this.usage_count++;
    }

    public void closeHandler (CallContext context) {
        this.usage_count--;
        if (this.usage_count == 0) {
            try {
                if (this.print_writer != null) {
                    this.print_writer.flush();
                    if (this.do_close) {
                        this.print_writer.close();
                    }
                    this.print_writer = null;
                }
                if (this.encoding_writer != null) {
                    if (this.do_close) {
                        this.encoding_writer.close();
                    }
                    this.encoding_writer = null;
                }
                if (this.writer != null) {
                    if (this.do_close) {
                        this.writer.close();
                    }
                    this.writer = null;
                }
            } catch (IOException ioe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Cannot close writer after writing (generator output)");
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
        }
    }

    public void closeCurrent (CallContext context) {
        try {
            if (this.print_writer != null) {
                this.print_writer.flush();
                if (this.do_close) {
                    this.print_writer.close();
                }
                this.print_writer = null;
            }
            if (this.encoding_writer != null) {
                if (this.do_close) {
                    this.encoding_writer.close();
                }
                this.encoding_writer = null;
            }
            if (this.writer != null) {
                if (this.do_close) {
                    this.writer.close();
                }
                this.writer = null;
            }
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Cannot close writer after writing (generator output)");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public String redirectDefaultWriter (CallContext context, String channel_name, boolean do_not_modify) {
        CustomaryContext.create((Context)context).throwLimitation(context, "GeneratorOutputToWriter does not support 'do_not_modify' switch");
        throw (ExceptionLimitation) null; // compilernsists
    }

    public String redirectDefaultWriter (CallContext context, String channel_name) {
        if (channel_name != null && channel_name.length() != 0) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Generator output cannot be redirected to different channel, there is only a single Writer available");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        return "";
    }

    public Boolean isContentDiffering(CallContext context) {
        return null;
    }

    public Vector<String> getChannelNames(CallContext context) {
        return null;
    }
}
