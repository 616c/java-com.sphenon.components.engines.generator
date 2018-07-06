package com.sphenon.basics.data.conversion;

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
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.locators.*;
import com.sphenon.basics.locating.factories.*;
import com.sphenon.basics.locating.returncodes.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.basics.security.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.aspects.*;
import com.sphenon.basics.application.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.io.BufferedInputStream;

public class Data_MediaObject_ConversionAdapter_Generator implements Data_MediaObject {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.basics.data.conversion.Data_MediaObject_ConversionAdapter_Generator"); };

    protected Data                 source_data;
    protected Scope                dyns_scope;
    protected String               template;
    protected TypeImpl_MediaObject target_type;
    protected RegularExpression    filename_substitution;
    protected String               filename_substitution_regexp;
    protected String               filename_substitution_subst;
    protected String               disposition_filename;

    /**
       Uses the source data as a template to create output
       possibly based on additional contextual parameters
     */
    public Data_MediaObject_ConversionAdapter_Generator (CallContext context, Data source_data, Scope dyns_scope, TypeImpl_MediaObject target_type, String filename_substitution_regexp, String filename_substitution_subst) {
        this(context, null, source_data, dyns_scope, target_type, filename_substitution_regexp, filename_substitution_subst);
    }

    /**
       Uses a predefined template to convert the provided source data
     */
    // but: it seems that source_data here in this case is not used at all, is it?
    public Data_MediaObject_ConversionAdapter_Generator (CallContext context, String template, Data source_data, Scope dyns_scope, TypeImpl_MediaObject target_type, String filename_substitution_regexp, String filename_substitution_subst) {
        this.source_data                  = source_data;
        this.dyns_scope                   = dyns_scope;
        this.template                     = template;
        this.target_type                  = target_type;
        this.filename_substitution_regexp = filename_substitution_regexp;
        this.filename_substitution_subst  = filename_substitution_subst;
        this.last_generator_creation      = -1;
    }

    public Type getDataType(CallContext context) {
        return target_type;
    }

    public String getMediaType(CallContext context) {
        return target_type.getMediaType(context);
    }

    public String getDispositionFilename(CallContext context) {
        if (this.disposition_filename == null) {
            String source_disposition_filename =    source_data instanceof Data_MediaObject ? ((Data_MediaObject) source_data).getDispositionFilename(context)
                                                  : source_data instanceof Data_Object ? Encoding.recode(context, ((Data_Object) source_data).getInstanceIdentifier(context), Encoding.UTF8, Encoding.VSAU)
                                                  : "---";

            if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Deriving filename: source name '%(sourcename)', regexp '%(regexp)', subexp '%(subexp)'", "sourcename", source_disposition_filename, "regexp", filename_substitution_regexp, "subexp", filename_substitution_subst); }

            this.filename_substitution = new RegularExpression(context, filename_substitution_regexp, filename_substitution_subst);
            this.disposition_filename = filename_substitution.replaceFirst(context, source_disposition_filename);

            if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Deriving filename: result '%(result)'", "result", this.disposition_filename); }
        }

        return this.disposition_filename;
    }

    public java.util.Date getLastUpdate(CallContext context) {
        if (this.generator == null) { return new java.util.Date(); }
        java.util.Date ldm = getGenerator(context).getLastDataModification(context, dyns_scope);
        if (ldm == null) { return new java.util.Date(); }        
        java.util.Date sdlu = source_data.getLastUpdate(context);
        return ldm.after(sdlu) ? ldm : sdlu;
    }

    protected Authority authority = null;
    protected Authority getAuthority(CallContext context) {
        if (this.authority == null) {
            this.authority = SecuritySessionData.get(context).getAuthority(context);
        }
        return this.authority;
    }

    protected Generator generator;
    protected long last_generator_creation;

    protected Generator getGenerator(CallContext context) {
        if (this.template == null) {
            long sdlu = 0;
            if (    this.generator == null
                 || this.last_generator_creation == -1
                 || (sdlu = source_data.getLastUpdate(context).getTime()) <= 0
                 || this.last_generator_creation < sdlu) {
                long lgc = System.currentTimeMillis();

                String template = StringUtilities.join(context, FileUtilities.readStream(context, ((Data_MediaObject) source_data).getInputStream(context)), "\n", true);
                this.generator = GeneratorRegistry.get(context).getGenerator_InMemoryTemplate(context, template);

                this.last_generator_creation = lgc;
            }
        } else {
            if (this.generator == null) {
                try {
                    long lgc = System.currentTimeMillis();

                    this.generator = GeneratorRegistry.mustGetGenerator(context, template);

                    this.last_generator_creation = lgc;
                } catch (Throwable t) {
                    ApplicationContext.get((Context) context).handleException(context, t);
                    return null;
                }
            }
        }
        return this.generator;
    }

    public java.io.InputStream getInputStream(CallContext context) {
        /*******************************************************************************

          Caution! The code here is a first try, it is much inperformant, everything
          is converted to String. Better:

            - in GeneratorRegistry and GeneratorTemplate in addition to
              TreeLeaf support Data_MediaObject (Template does something
              like this anyway, finally)

            - implement Caching, and Generator with ComparingWriter, so that
              it is written newly only in case of change

            - then: write to File and return DMO instead of File

         *******************************************************************************/

        String result;

        ClassLoader current = null;
        try {
            current = ApplicationContext.get((Context) context).getApplication(context).setApplicationClassLoader(context);

            GeneratorOutputToString gots = new GeneratorOutputToString(context);
            Generator generator = getGenerator(context);

            generator.generate(context, gots, dyns_scope);

            result = gots.getResult(context);
        } finally {
            ApplicationContext.get((Context) context).getApplication(context).resetApplicationClassLoader(context, current);
        }

        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(result.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException uee) {
            CustomaryContext.create(Context.create(context)).throwConfigurationError(context, uee, "Java version does not support utf-8");
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        return new BufferedInputStream(bais);
    }

    public java.io.InputStream getStream(CallContext context) {
        return this.getInputStream(context);
    }

    public java.io.OutputStream getOutputStream(CallContext context) {
        CustomaryContext.create((Context)context).throwLimitation(context, "Data_MediaObject_ConversionAdapter_Generator is not writable");
        throw (ExceptionLimitation) null; // compilernsists
    }

    final static String[] hex =
    {
        "00", "01", "02", "03", "04", "05", "06", "07",
        "08", "09", "0A", "0B", "0C", "0D", "0E", "0F",
        "10", "11", "12", "13", "14", "15", "16", "17",
        "18", "19", "1A", "1B", "1C", "1D", "1E", "1F",
        "20", "21", "22", "23", "24", "25", "26", "27",
        "28", "29", "2A", "2B", "2C", "2D", "2E", "2F",
        "30", "31", "32", "33", "34", "35", "36", "37",
        "38", "39", "3A", "3B", "3C", "3D", "3E", "3F",
        "40", "41", "42", "43", "44", "45", "46", "47",
        "48", "49", "4A", "4B", "4C", "4D", "4E", "4F",
        "50", "51", "52", "53", "54", "55", "56", "57",
        "58", "59", "5A", "5B", "5C", "5D", "5E", "5F",
        "60", "61", "62", "63", "64", "65", "66", "67",
        "68", "69", "6A", "6B", "6C", "6D", "6E", "6F",
        "70", "71", "72", "73", "74", "75", "76", "77",
        "78", "79", "7A", "7B", "7C", "7D", "7E", "7F",
        "80", "81", "82", "83", "84", "85", "86", "87",
        "88", "89", "8A", "8B", "8C", "8D", "8E", "8F",
        "90", "91", "92", "93", "94", "95", "96", "97",
        "98", "99", "9A", "9B", "9C", "9D", "9E", "9F",
        "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7",
        "A8", "A9", "AA", "AB", "AC", "AD", "AE", "AF",
        "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7",
        "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF",
        "C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7",
        "C8", "C9", "CA", "CB", "CC", "CD", "CE", "CF",
        "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7",
        "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF",
        "E0", "E1", "E2", "E3", "E4", "E5", "E6", "E7",
        "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF",
        "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7",
        "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF"
    };

    static public String encode(CallContext call_context, String string) {
        byte[] bytes;
        try {
            bytes = string.getBytes("UTF-8");
        } catch (java.io.UnsupportedEncodingException uee) {
            Context context = Context.create(call_context);
            CustomaryContext cc = CustomaryContext.create(context);
            cc.throwLimitation(context, "System (VM) does not support UTF-8 encoding");
            throw (ExceptionLimitation) null;
        }
        StringBuffer new_string = new StringBuffer();
        int b;
        
        boolean was_slash = false;
        for (int i=0; i < bytes.length; i++) {
            b = bytes[i];
            if (b < 0) { b += 256; }

            if (    (b >= 'A' && b <= 'Z')
                 || (b >= 'a' && b <= 'z')
                 || (b >= '0' && b <= '9')
                 || (b == '/')
               )
            {
                if (was_slash && b == '/') {
                    new_string.append('_');
                }
                new_string.append((char)b);
            } else if (b == '\\') {
                new_string.append("__/");
            } else {
                new_string.append("_"+hex[b]);
            }
            was_slash = (b == '/');
        }
        return new_string.toString();
    }  

    public Locator tryGetOrigin(CallContext context) {
        return source_data.tryGetOrigin(context);
    }
}

