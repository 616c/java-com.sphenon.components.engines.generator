package com.sphenon.basics.data;

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
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.BufferedInputStream;

public class Data_MediaObject_Generator implements Data_MediaObject {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.basics.data.conversion.Data_MediaObject_Generator"); };

    protected String               template;
    protected Object[]             arguments;
    protected TypeImpl_MediaObject target_type;
    protected String               disposition_filename;

    public Data_MediaObject_Generator (CallContext context, String template, String disposition_filename, Object... arguments) {
        this.template                  = template;
        this.arguments                 = arguments;
        this.target_type               = target_type;
        this.disposition_filename      = disposition_filename;
    }

    public String getTemplate (CallContext context) {
        return this.template;
    }

    public void setTemplate (CallContext context, String template) {
        this.template = template;
    }

    public String getDispositionFilename (CallContext context) {
        return this.disposition_filename;
    }

    public void setDispositionFilename (CallContext context, String disposition_filename) {
        this.disposition_filename = disposition_filename;
        this.target_type = null;
    }

    public Type getDataType(CallContext context) {
        if (this.target_type == null) {
            this.target_type = (TypeImpl_MediaObject) TypeManager.getMediaType(context, this.disposition_filename == null ? "" : this.disposition_filename.replaceFirst(".*\\.", ""));
        }
        return this.target_type;
    }

    public String getMediaType(CallContext context) {
        this.getDataType(context);
        return this.target_type.getMediaType(context);
    }

    public java.util.Date getLastUpdate(CallContext call_context) {
        // to be improved: specify dependency code, e.g.
        // "this generator output depends on object xyz's state" or alike
        return new java.util.Date(); // source_data.getLastUpdate(call_context);
    }

    public java.io.InputStream getInputStream(CallContext context) {
        /*******************************************************************************

          Achtung! Der Code hier ist noch lame. Alles wird nach String
          plattgeklopft. Besser:

            - in GeneratorRegistry und GeneratorTemplate noch zusätzlich zu
              TreeLeaf Data_MediaObject nachrüsten (wird vom Template eh in
              sowas am Ende umgewandelt)

            - Caching einbauen, und Generator mit ComparingWriter, so daß nur
              neu geschrieben wird wenn geändert

            - dann entsprechend nach File rausschreiben und DMO für File rausgeben

         *******************************************************************************/

        String result = null;
        byte[] binary = null;

        if (this.template == null || this.template.isEmpty()) {
            result = "";
        } else {
            ClassLoader current = null;
            try {
                current = ApplicationContext.get((Context) context).getApplication(context).setApplicationClassLoader(context);
                
                GeneratorOutputToString gots = new GeneratorOutputToString(context);
                Generator generator = GeneratorRegistry.mustGetGenerator(context, this.template);

                if (generator instanceof com.sphenon.engines.generator.GeneratorWithOutputStream) {
                    Object[] local_arguments = new Object[this.arguments == null ? 1 : (1 + this.arguments.length)];
                    int i=0;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    local_arguments[i++] = baos;
                    if (this.arguments != null) {
                        for (Object o : this.arguments) {
                            local_arguments[i++] = o;
                        }
                    }
                    generator.generate(context, gots, local_arguments);
                    binary = baos.toByteArray();
                } else {
                    generator.generate(context, gots, this.arguments);
                    result = gots.getResult(context);
                }

            } finally {
                ApplicationContext.get((Context) context).getApplication(context).resetApplicationClassLoader(context, current);
            }
        }

        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(binary == null ? result.getBytes("UTF-8") : binary);
        } catch (UnsupportedEncodingException uee) {
            CustomaryContext.create(Context.create(context)).throwConfigurationError(context, uee, "Java version does not support utf-8");
            throw (ExceptionConfigurationError) null;
        }
        return new BufferedInputStream(bais);
    }

    public java.io.InputStream getStream(CallContext context) {
        return this.getInputStream(context);
    }

    public java.io.OutputStream getOutputStream(CallContext context) {
        CustomaryContext.create((Context)context).throwLimitation(context, "Data_MediaObject_ConversionAdapter_Generator is not writable");
        throw (ExceptionLimitation) null;
    }

    public Locator tryGetOrigin(CallContext context) {
        return null;
    }
}

