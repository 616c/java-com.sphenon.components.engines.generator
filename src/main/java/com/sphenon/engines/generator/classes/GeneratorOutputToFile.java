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
import com.sphenon.basics.expression.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.factories.*;
import com.sphenon.basics.validation.returncodes.*;

import com.sphenon.engines.generator.*;

import java.io.*;

import java.util.Vector;
import java.util.HashMap;

public class GeneratorOutputToFile implements GeneratorOutputHandler {

    public GeneratorOutputToFile (CallContext context) {
        this.channel_names = null;
        this.channels      = null;
        this.channel       = new Entry();
        this.name          = "";
        this.usage_count   = 0;
    }

    public GeneratorOutputToFile (CallContext context, String text_locator) {
        this(context);
        this.setTextLocator(context, text_locator);
    }

    public GeneratorOutputToFile (CallContext context, Locator locator) {
        this(context);
        this.setLocator(context, locator);
    }

    public GeneratorOutputToFile (CallContext context, Location location) {
        this(context);
        this.setLocation(context, location);
    }

    public GeneratorOutputToFile (CallContext context, File file) {
        this(context);
        this.setFile(context, file);
    }

    protected File file;

    public File getFile (CallContext context) {
        return this.file;
    }

    public File defaultFile (CallContext context) {
        return null;
    }

    public void setFile (CallContext context, File file) {
        this.file = file;
    }

    protected String file_name;

    public String getFileName (CallContext context) {
        return this.file_name;
    }

    public String defaultFileName (CallContext context) {
        return null;
    }

    public void setFileName (CallContext context, String file_name) {
        this.file_name = file_name;
    }

    protected String text_locator;

    public String getTextLocator (CallContext context) {
        return this.text_locator;
    }

    public String defaultTextLocator (CallContext context) {
        return null;
    }

    public void setTextLocator (CallContext context, String text_locator) {
        this.text_locator = text_locator;
    }

    protected Locator locator;

    public Locator getLocator (CallContext context) {
        return this.locator;
    }

    public Locator defaultLocator (CallContext context) {
        return null;
    }

    public void setLocator (CallContext context, Locator locator) {
        this.locator = locator;
    }
    
    protected Location location;

    public Location getLocation (CallContext context) {
        return this.location;
    }

    public Location defaultLocation (CallContext context) {
        return null;
    }

    public void setLocation (CallContext context, Location location) {
        this.location = location;
    }

    protected boolean keep_unmodified_files;

    public boolean getKeepUnmodifiedFiles (CallContext context) {
        return this.keep_unmodified_files;
    }

    public boolean defaultKeepUnmodifiedFiles (CallContext context) {
        return false;
    }

    public void setKeepUnmodifiedFiles (CallContext context, boolean keep_unmodified_files) {
        this.keep_unmodified_files = keep_unmodified_files;
    }

    protected boolean keep_backup_if_modified;

    public boolean getKeepBackupIfModified (CallContext context) {
        return this.keep_backup_if_modified;
    }

    public boolean defaultKeepBackupIfModified (CallContext context) {
        return false;
    }

    public void setKeepBackupIfModified (CallContext context, boolean keep_backup_if_modified) {
        this.keep_backup_if_modified = keep_backup_if_modified;
    }

    protected void initFile(CallContext context) {

        if (file == null) {
            if (this.file_name == null && this.text_locator == null && this.locator == null && this.location == null) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "GeneratorOutputToFile: neither FileName nor TextLocator nor Locator nor Location argument given");
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
        
            if ( (   (this.file_name != null    ? 1 : 0)
                   + (this.text_locator != null ? 1 : 0)
                   + (this.locator != null      ? 1 : 0)
                   + (this.location != null     ? 1 : 0)
                 ) > 1
               ) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "GeneratorOutputToFile: FileName and/or TextLocator and/or Locator and/or Location argument given, please provide only one");
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
            
            if (this.text_locator != null) {
                try {
                    this.locator = Factory_Locator.construct(context, this.text_locator);
                } catch (ValidationFailure vf) {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, "Invalid TextLocator for generator output");
                    throw (ExceptionPreConditionViolation) null; // compiler insists
                }
            }
            
            if (this.locator != null) {
                this.file_name = this.locator.tryGetTextLocatorValue(context,Factory_Locator.tryConstruct(context,"ctn://Space/local_host/file_system"),"File");
            }
            
            if (this.location != null) {
                this.file_name = this.location.tryGetTextLocatorValue(context,Factory_Locator.tryConstruct(context,"ctn://Space/local_host/file_system"),"File");
            }
            
            this.file = new File(this.file_name);
            if (this.file.getParentFile() != null) {
                this.file.getParentFile().mkdirs();
            }
        }
    }

    protected int usage_count;

    static protected class Entry {
        public OutputStream    ostream;
        public Writer          writer;
        public ComparingWriter comparing_writer;
        public EncodingWriter  encoding_writer;
        public PrintWriter     print_writer;
        public File            file;
        public boolean         do_not_modify;
    }

    protected Vector<String>         channel_names;
    protected HashMap<String, Entry> channels;
    protected Entry                  channel;
    protected String                 name;
    
    public EncodingWriter getWriter (CallContext context) {
        if (this.channel.writer == null) {
            initFile(context);
            this.channel.file = file;
            if (this.name != null && this.name.length() != 0) {
                this.channel.file = new File(file.getParentFile(), this.name);
            }
            SystemCommandUtilities.ensureParentFolderExists(context, this.channel.file);
            try {
                if (this.getKeepUnmodifiedFiles(context)) {
                    this.channel.comparing_writer = new ComparingWriter(context, this.channel.file, this.channel.do_not_modify, this.getKeepBackupIfModified(context));
                    this.channel.writer = this.channel.comparing_writer;
                } else {
                    this.channel.comparing_writer = null;
                    this.channel.ostream = new FileOutputStream(this.channel.file);
                    this.channel.writer = new OutputStreamWriter(this.channel.ostream, "UTF-8"); // yeah, there are systems where this is not default :^)
                }
                this.channel.encoding_writer = new EncodingWriter(context, this.channel.writer);
            } catch (IOException ioe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, ioe, "Cannot open file '%(filename)' for writing (generator output)", "filename", this.channel.file.getPath());
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
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
                        this.channel.writer = null;
                    }
                    if (this.channel.ostream != null) {
                        this.channel.ostream.close();
                        this.channel.ostream = null;
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
                        if (entry.ostream != null) {
                            entry.ostream.close();
                            entry.ostream = null;
                        }
                    }
                }
            } catch (IOException ioe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Cannot close writers after writing (generator output)");
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
                if (this.channel.ostream != null) {
                    this.channel.ostream.close();
                    this.channel.ostream = null;
                }
            }
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Cannot close writers after writing (generator output)");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public String redirectDefaultWriter (CallContext context, String channel_name) {
        return redirectDefaultWriter(context, channel_name, false);
    }

    public String redirectDefaultWriter (CallContext context, String channel_name, boolean do_not_modify) {
        initFile(context);
        channel_name = DynamicString.process(context, channel_name, null, "base", file.getName());
        if (this.channels == null) {
            this.channels = new HashMap<String, Entry>();
            this.channels.put(this.name, this.channel);
            this.channel_names = new Vector<String>();
            this.channel_names.add(this.name);
        }
        Entry new_entry = this.channels.get(channel_name);
        if (new_entry == null) {
            new_entry = new Entry();
            new_entry.do_not_modify = do_not_modify;
            this.channels.put(channel_name, new_entry);
            this.channel_names.add(channel_name);
        }
        String previous_name = this.name;
        this.channel = new_entry;
        this.name    = channel_name;
        return previous_name;
    }

    public Boolean isContentDiffering(CallContext context) {
        if (this.channel.comparing_writer == null) {
            return null;
        }
        return this.channel.comparing_writer.isContentDiffering(context);
    }

    public Vector<String> getChannelNames(CallContext context) {
        return this.channel_names;
    }
}
