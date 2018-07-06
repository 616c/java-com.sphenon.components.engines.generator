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
import com.sphenon.basics.data.*;
import com.sphenon.basics.javacode.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.tom.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class TOMJTTIdentifier extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMJTTIdentifier(CallContext context, TOMNode parent, String identifier) {
        super(context, parent);
        this.identifier = identifier;
    }

    // Attributes ---------------------------------------------------------------------

    protected int       gom_id;
    protected int       local_gom_index;
    protected String    identifier;

    protected boolean   is_argument;

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public void createJavaCode(CallContext context, Section section, JavaCodeManager java_code_manager, BufferedWriter current_writer, String indent) {

        try {
            switch (section) {
                case INIT_SECTION:
                    gom_id          = this.getRootNode(context).getNextGOMId(context);
                    local_gom_index = this.getRootNode(context).getNextLocalGOMIndex(context);

                    this.is_argument = false;
                    TOMSkeleton skeleton = this.findSuperNode(context, TOMSkeleton.class, null);
                    int i=0;
                    for (FormalArgument fa : skeleton.getSignature(context).getFormalArguments(context)) {
                        if (i >= 2 && fa.getArgumentName(context).equals(identifier)) {
                            this.is_argument = true;
                            break;
                        }
                        i++;
                    }
                    break;
                case GOM_BUILDER_SECTION:
                    if (this.is_argument) {
                        current_writer.append(indent + "current_gom_node = new GOMJavaExpression(context, gom_node, " + gom_id + ");\n");
                    } else {
                        current_writer.append(indent + "current_gom_node = new GOMText(context, gom_node, " + gom_id + ", text_" + gom_id + ");\n");
                    }
                    break;
                case GENERATOR_CODE_SECTION:
                    if (this.is_argument) {
                        current_writer.append(indent + "((Class_GOMExecutionContext) gom_execution_context).getOutput(context, false).print(" + this.identifier + ");\n");
                        current_writer.append(indent + "gom_processor.process(context, " + local_gom_index + ", " + gom_id + ");\n");
                    } else {
                        // no code
                    }
                    break;
                case DATA_SECTION:
                    if (this.is_argument) {
                        // no data
                    } else {
                        current_writer.append(indent + "static protected final String text_" + gom_id + " = \"" + this.identifier + "\";\n");
                    }
                    break;
            } 
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Could not write to java source file/stream (for template '%(template)')", "template", this.getRootNode(context).getTemplate(context).getFullClassName(context));
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }
    }
}
