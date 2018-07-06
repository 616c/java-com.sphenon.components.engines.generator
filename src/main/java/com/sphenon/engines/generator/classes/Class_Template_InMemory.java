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
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.returncodes.*;

import com.sphenon.basics.configuration.annotations.*;

import com.sphenon.engines.generator.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.File;
import java.util.Date;

import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import java.util.Date;
import java.util.Vector;

public class Class_Template_InMemory implements Template {

    // Configuration ------------------------------------------------------------------

    // Configuration ------------------------------------------------------------------

    @Configuration public interface Config {
        String getSHAMatchCheckPath(CallContext context);
    }
    static Config config = Configuration_Class_Template_InMemory.get(RootContext.getInitialisationContext());

    // Construction -------------------------------------------------------------------

    public Class_Template_InMemory(CallContext context, String template_code) {
        this.setTemplateCode (context, template_code);
    }

    // Attributes ---------------------------------------------------------------------

    protected long created;

    protected String template_code;

    public String getTemplateCode (CallContext context) {
        return this.template_code;
    }

    public void setTemplateCode (CallContext context, String template_code) {
        this.template_code = template_code;

        String sha_code = Encoding.recode_UTF8_SHA1(context, template_code);
        String shamcp   = config.getSHAMatchCheckPath(context);

        if (shamcp != null && shamcp.length() != 0) {
            File shamcpf = new File(shamcp);
            if (! shamcpf.exists()) {
                shamcpf.mkdirs();
            }
            File shamcff = new File(shamcpf, "SHA1_" + sha_code + ".check");
            if (shamcff.exists()) {
                InputStreamReader isr = null;
                try {
                    isr = new InputStreamReader(new FileInputStream(shamcff), "UTF-8");
                } catch (FileNotFoundException fnfe) {
                    CustomaryContext.create((Context)context).throwImpossibleState(context, fnfe, "File existence checked but file does not exist ('%(file)')", "file", shamcff);
                    throw (ExceptionImpossibleState) null; // compiler insists
                } catch (UnsupportedEncodingException uee) {
                    CustomaryContext.create((Context)context).throwInstallationError(context, "UTF-8 unknown");
                    throw (ExceptionInstallationError) null; // compiler insists
                }
                StringCharacterIterator sci = new StringCharacterIterator(this.template_code);
                try {
                    for(char c = sci.first(); c != CharacterIterator.DONE; c = sci.next()) {
                        if (c != isr.read()) {
                            CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, "A basic assumption of SHA1 (digest matches) proved false. Code '%(templatecode)' and code in file '%(matchfile)' share same digest", "templatecode", this.template_code, "matchfile", shamcff.getPath());
                            throw (ExceptionAssertionProvedFalse) null; // compiler insists
                        }
                    }
                    if (isr.ready()) {
                        CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, "A basic assumption of SHA1 (digest matches) proved false. Code '%(templatecode)' and code in file '%(matchfile)' share same digest", "templatecode", this.template_code, "matchfile", shamcff.getPath());
                        throw (ExceptionAssertionProvedFalse) null; // compiler insists
                    }
                    isr.close();
                } catch (IOException ioe) {
                    CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Reading of '%(file)' failed", "file", shamcff);
                    throw (ExceptionEnvironmentFailure) null; // compiler insists
                }
            } else {
                OutputStreamWriter osw = null;
                try {
                    osw = new OutputStreamWriter(new FileOutputStream(shamcff), "UTF-8");
                } catch (FileNotFoundException fnfe) {
                    CustomaryContext.create((Context)context).throwConfigurationError(context, fnfe, "File '%(file)' not writable", "file", shamcff);
                    throw (ExceptionConfigurationError) null; // compiler insists
                } catch (UnsupportedEncodingException uee) {
                    CustomaryContext.create((Context)context).throwInstallationError(context, "UTF-8 unknown");
                    throw (ExceptionInstallationError) null; // compiler insists
                }
                try {
                    osw.write(this.template_code, 0, this.template_code.length());
                    osw.close();
                } catch (IOException ioe) {
                    CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Writing of '%(file)' failed", "file", shamcff);
                    throw (ExceptionEnvironmentFailure) null; // compiler insists
                }
            }
            this.created = shamcff.lastModified();
        }

        this.setFullClassName(context, "com.sphenon.engines.generator.templates.inmemory.SHA1_" + sha_code);
    }

    public long getLastModification(CallContext context) {
        return created;
    }

    public Locator tryGetOrigin(CallContext context) {
        try {
            return Locator.createGenericLocator(context, "ctn://InMemoryTemplate/" + getFullClassName(context));
        } catch (InvalidLocator il) {
            return null;
        }
    }

    protected String class_name;

    public String getClassName (CallContext context) {
        return this.class_name;
    }

    protected String package_name;

    public String getPackageName (CallContext context) {
        return this.package_name;
    }

    protected String full_class_name;

    public String getFullClassName (CallContext context) {
        return this.full_class_name;
    }

    protected void setFullClassName (CallContext context, String full_class_name) {
        this.full_class_name = full_class_name;

        int    pos           = full_class_name.lastIndexOf(".");
        this.package_name    = (pos == -1 ? "" : full_class_name.substring(0,pos));
        this.class_name      = (pos == -1 ? full_class_name : full_class_name.substring(pos+1));
    }

    public Vector<String> getPackageChilds(CallContext context) {
        return null;
    }

    public String getTemplateTypeAlias(CallContext context) {
        return null;
    }

    // Operations ---------------------------------------------------------------------

    public boolean exists(CallContext context) {
        return true;
    }

    public BufferedReader getReader (CallContext context) {
        return new BufferedReader(new StringReader(this.template_code));
    }
}
