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

import com.sphenon.engines.generator.tchandler.classes.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.tom.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.util.Hashtable;

public class TOMJTTTemplateExpression extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMJTTTemplateExpression(CallContext context, TOMNode parent, JTENodeArgumentList list) {
        super(context, parent);
        this.list = list;
    }

    // Attributes ---------------------------------------------------------------------

    protected int                 gom_id;
    protected int                 local_gom_index;
    protected JTENodeArgumentList list;

    protected Hashtable           arguments;

    // Internal -----------------------------------------------------------------------

    protected String translateJTENode(CallContext context, JTENodeArgumentList list) {
        String result = "";
        if (list != null && list.getArguments(context) != null && list.getArguments(context).size() != 0) {
            result += "_";
            for (JTENodeArgument jtena : list.getArguments(context)) {
                result += translateJTENode(context, jtena);
                result += "_";
            }
        }
        return result;
    }

    protected String translateJTENode(CallContext context, JTENodeArgument argument) {
        String result = "";
        String identifier = argument.getIdentifier(context);
        if (identifier != null && identifier.length() != 0) {
            if (argument.isCode(context) || this.arguments.get(identifier) != null) {
                result += "\" + " + identifier + " + \"";
            } else {
                result += identifier;
            }
        }
        result += translateJTENode(context, argument.getArguments(context));
        return result;
    }

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public void createJavaCode(CallContext context, Section section, JavaCodeManager java_code_manager, BufferedWriter current_writer, String indent) {

        try {
            switch (section) {
                case INIT_SECTION:
                    gom_id          = this.getRootNode(context).getNextGOMId(context);
                    local_gom_index = this.getRootNode(context).getNextLocalGOMIndex(context);

                    this.arguments = new Hashtable();
                    TOMSkeleton skeleton = this.findSuperNode(context, TOMSkeleton.class, null);
                    int i=0;
                    for (FormalArgument fa : skeleton.getSignature(context).getFormalArguments(context)) {
                        if (i >= 2) {
                            this.arguments.put(fa.getArgumentName(context), 1);
                        }
                        i++;
                    }
                    break;
                case GOM_BUILDER_SECTION:
                    current_writer.append(indent + "current_gom_node = new GOMJavaExpression(context, gom_node, " + gom_id + ");\n");
                    break;
                case GENERATOR_CODE_SECTION:
                    current_writer.append(indent + "((Class_GOMExecutionContext) gom_execution_context).getOutput(context, false).print(\"");
                    current_writer.append(translateJTENode(context, list));
                    current_writer.append("\");\n");
                    current_writer.append(indent + "gom_processor.process(context, " + local_gom_index + ", " + gom_id + ");\n");
                    break;
            } 
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Could not write to java source file/stream (for template '%(template)')", "template", this.getRootNode(context).getTemplate(context).getFullClassName(context));
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }
    }
}
