package com.sphenon.engines.generator.tom.classes;

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
import com.sphenon.basics.javacode.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.tom.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.IOException;

public class TOMText extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMText(CallContext context, TOMNode parent, BufferedReader reader) {
        super(context, parent);
        this.reader = reader;
    }

    public TOMText(CallContext context, TOMNode parent, String code) {
        super(context, parent);
        this.reader = new BufferedReader(new StringReader(code));
    }
    // Attributes ---------------------------------------------------------------------

    protected int            gom_id;
    protected int            local_gom_index;
    protected BufferedReader reader;

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public void createJavaCode(CallContext context, Section section, JavaCodeManager java_code_manager, BufferedWriter current_writer, String indent) {

        try {
            switch (section) {
                case INIT_SECTION:
                    gom_id          = this.getRootNode(context).getNextGOMId(context);
                    local_gom_index = this.getRootNode(context).getNextLocalGOMIndex(context);
                    break;
                case GOM_BUILDER_SECTION:
                    if (GeneratorPackageInitialiser.use_string_cache) {
                        StringBuilder sb = new StringBuilder();
                        int c;
                        if (this.reader != null) {
                            while ((c = this.reader.read()) != -1) { sb.append((char) c); }
                        }
                        int text_index = this.getRootNode(context).getTextIndex(context, sb.toString());
                        current_writer.append(indent + "current_gom_node = new GOMText(context, gom_node, " + gom_id + ", texts[" + text_index + "]);\n");
                    } else {
                        current_writer.append(indent + "current_gom_node = new GOMText(context, gom_node, " + gom_id + ", text_" + gom_id + ");\n");
                    }
                    break;
                case DATA_SECTION:
                    if (GeneratorPackageInitialiser.use_string_cache == false) {
                        current_writer.append(indent + "static protected final String text_" + gom_id + " = \"");
                        Encoding.recode(context, this.reader, current_writer, Encoding.UTF8, Encoding.JAVA);
                        current_writer.append("\";\n");
                    }
                    break;
            } 
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Could not write to java source file/stream (for template '%(template)')", "template", this.getRootNode(context).getTemplate(context).getFullClassName(context));
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }
    }
}
