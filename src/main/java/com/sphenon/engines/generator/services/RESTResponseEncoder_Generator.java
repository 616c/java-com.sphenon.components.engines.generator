package com.sphenon.engines.generator.services;

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
import com.sphenon.basics.cache.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.metadata.Type;
import com.sphenon.basics.metadata.TypeManager;
import com.sphenon.basics.services.*;
import com.sphenon.server.ws.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;

import java.util.Vector;

public class RESTResponseEncoder_Generator implements RESTResponseEncoder {

    public RESTResponseEncoder_Generator(CallContext context) {
    }

    public void notifyNewConsumer(CallContext context, Consumer consumer) {
        // nice to see you
    }

    public boolean equals(Object object) {
        return (object instanceof RESTResponseEncoder_Generator);
    }

    static protected MIMEType my_mime_type_1;
    static protected MIMEType my_mime_type_2;
    
    // [ToDo] implement registry for templates, get generator by type,
    // possibly implement additional interface RESTEncoderTemplate
    // with .matches-Method for more finegrained tests
    protected Generator generator;
    protected Type handled_type;
    
    public Generator getGenerator (CallContext context) {
        if (this.generator == null) {
            this.generator = GeneratorRegistry.mustGetGenerator(context, "com.sphenon.ui.vui.templates.RSS");
            Vector<FormalArgument> arguments = this.generator.getSignature(context);
            this.handled_type = arguments.get(0).getType(context);
        }
        return this.generator;
    }

    public synchronized boolean canHandleMIMEType(CallContext context, MIMEType mime_type, Object data) {
        if (my_mime_type_1 == null) {
            my_mime_type_1 = new MIMEType(context, "application/rss+xml");
        }
        if (my_mime_type_2 == null) {
            my_mime_type_2 = new MIMEType(context, "text/rss+xml");
        }
        this.getGenerator(context);
        return (    (    mime_type.matches(context, my_mime_type_1)
                      || mime_type.matches(context, my_mime_type_2)
                    )
                 && (data != null && TypeManager.get(context, data.getClass()).isA(context, handled_type))
               );
    }

    public synchronized String[] encodeResponse(CallContext context, Object data) {
        GeneratorOutputToString gots = new GeneratorOutputToString(context);
        this.getGenerator(context).generate(context, gots, data);
        StringBuffer output = gots.getResultBuffer(context);
        return new String[] { output.toString(), "application/rss+xml" };
        // XML
        // Generator generator = GeneratorRegistry.mustGetGenerator(context, "com.sphenon.ui.vui.templates.XML");
        // GeneratorOutputToString gots = new GeneratorOutputToString(context);
        // generator.generate(context, gots, vui_result);
        // StringBuffer output = gots.getResultBuffer(context);
        // return new String[] { output.toString(), "application/xml" };
    }
}
