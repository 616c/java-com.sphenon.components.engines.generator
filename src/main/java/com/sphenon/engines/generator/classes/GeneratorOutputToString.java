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
import java.io.IOException;

import java.util.Vector;
import java.util.HashMap;

public class GeneratorOutputToString implements GeneratorOutputHandler {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.generator.classes.GeneratorOutputToString"); };

    public GeneratorOutputToString (CallContext context) {
        this.channel_names = null;
        this.channels      = null;
        this.channel       = new Entry();
        this.name          = "";
        this.usage_count   = 0;
    }

    public String getResult (CallContext context) {
        return this.getResult(context, "");
    }

    public String getResult (CallContext context, String channel_name) {
        StringBuffer result_buffer = this.getResultBuffer(context, "");
        String result = result_buffer.toString();

        if ((this.notification_level & Notifier.CHECKPOINT) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.CHECKPOINT, "Generator result '%(name)': '%(@result)'", "name", channel_name, "result", result); }

        return result;
    }

    public StringBuffer getResultBuffer (CallContext context) {
        return this.getResultBuffer(context, "");
    }

    public StringBuffer getResultBuffer (CallContext context, String channel_name) {
        Entry entry =  this.name.equals(channel_name) ?
                          this.channel
                        : ( this.channels == null ?
                               null
                             : this.channels.get(channel_name)
                          );
        StringBuffer result_buffer = entry == null ? null : entry.result_buffer;

        return result_buffer;
    }

    protected int usage_count;

    static protected class Entry {
        public StringBuffer   result_buffer;
        public EncodingWriter encoding_writer;
        public StringWriter   writer;
        public PrintWriter    print_writer;
    }

    protected Vector<String>         channel_names;
    protected HashMap<String, Entry> channels;
    protected Entry                  channel;
    protected String                 name;

    public EncodingWriter getWriter (CallContext context) {
        if (this.channel.writer == null) {
            this.channel.writer = new StringWriter();
            this.channel.encoding_writer = new EncodingWriter(context, this.channel.writer);
        }
        return this.channel.encoding_writer;
    }

    public PrintWriter getPrintWriter (CallContext context) {
        if (this.channel.print_writer == null) {
            this.channel.print_writer = new PrintWriter(this.getWriter(context));
        }
        return this.channel.print_writer;
    }

    public void openHandler (CallContext context) {
        this.usage_count++;
    }

    public void closeHandler (CallContext context) {
        this.usage_count--;
        if (this.usage_count == 0) {
            try {
                if (this.channels == null) {
                    if (this.channel.print_writer != null) {
                        this.channel.print_writer.flush();
                        this.channel.print_writer.close();
                        this.channel.print_writer = null;
                    }
                    if (this.channel.encoding_writer != null) {
                        this.channel.encoding_writer.close();
                        this.channel.encoding_writer = null;
                    }
                    if (this.channel.writer != null) {
                        this.channel.writer.close();
                        this.channel.result_buffer = this.channel.writer.getBuffer();
                        this.channel.writer = null;
                    } else {
                        this.channel.result_buffer = new StringBuffer();;
                    }
                } else {
                    for (Entry entry : this.channels.values()) {
                        if (entry.print_writer != null) {
                            entry.print_writer.flush();
                            entry.print_writer.close();
                            entry.print_writer = null;
                        }
                        if (entry.encoding_writer != null) {
                            entry.encoding_writer.close();
                            entry.encoding_writer = null;
                        }
                        if (entry.writer != null) {
                            entry.writer.close();
                            entry.writer = null;
                        }
                        if (entry.writer != null) {
                            entry.writer.close();
                            entry.result_buffer = entry.writer.getBuffer();
                            entry.writer = null;
                        } else {
                            entry.result_buffer = new StringBuffer();;
                        }
                    }
                }
            } catch (IOException ioe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Cannot close string writer after writing (generator output)");
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
        }
    }

    public void closeCurrent (CallContext context) {
        try {
            if (this.channel != null) {
                if (this.channel.print_writer != null) {
                    this.channel.print_writer.flush();
                    this.channel.print_writer.close();
                    this.channel.print_writer = null;
                }
                if (this.channel.encoding_writer != null) {
                    this.channel.encoding_writer.close();
                    this.channel.encoding_writer = null;
                }
                if (this.channel.writer != null) {
                    this.channel.writer.close();
                    this.channel.writer = null;
                }
            }
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Cannot close string writer after writing (generator output)");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public String redirectDefaultWriter (CallContext context, String channel_name, boolean do_not_modify) {
        if (do_not_modify == true) {
            CustomaryContext.create((Context)context).throwLimitation(context, "GeneratorOutputToString does not support 'do_not_modify' switch");
            throw (ExceptionLimitation) null; // compilernsists
        } else {
            return redirectDefaultWriter(context, channel_name);
        }
    }

    public String redirectDefaultWriter (CallContext context, String channel_name) {
        if (this.channels == null) {
            this.channels = new HashMap<String, Entry>();
            this.channels.put(this.name, this.channel);
            this.channel_names = new Vector<String>();
            this.channel_names.add(this.name);
        }
        Entry new_entry = this.channels.get(channel_name);
        if (new_entry == null) {
            new_entry = new Entry();
            this.channels.put(channel_name, new_entry);
            this.channel_names.add(channel_name);
        }
        String previous_name = this.name;
        this.channel = new_entry;
        this.name    = channel_name;
        return previous_name;
    }

    public Boolean isContentDiffering(CallContext context) {
        return null;
    }

    public Vector<String> getChannelNames(CallContext context) {
        return this.channel_names;
    }
}
