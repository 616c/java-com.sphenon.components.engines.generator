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
import java.io.StringReader;
import java.io.IOException;

import java.util.Vector;

public class TOMJavaExpression extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMJavaExpression(CallContext context, TOMNode parent, BufferedReader reader) {
        super(context, parent);
        this.reader = reader;
    }

    public TOMJavaExpression(CallContext context, TOMNode parent, String code) {
        super(context, parent);
        this.reader = new BufferedReader(new StringReader(code));
    }

    // Attributes ---------------------------------------------------------------------

    protected int            gom_id;
    protected int            local_gom_index;
    protected BufferedReader reader;
    protected Recoding       recoding;
    protected Recoding       text_recoding;

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
                    current_writer.append(indent + "current_gom_node = new GOMJavaExpression(context, gom_node, " + gom_id + ");\n");
                    break;
                case GENERATOR_CODE_SECTION:
                    int c;
                    boolean ends_with_newline = false;

                    this.recoding = (this.getRecoding(context));
                    this.text_recoding = (this.getTextRecoding(context));

                    if (this.recoding != null) {
                        current_writer.append(indent + "EncodingStep[] recoding_steps_" + gom_id + " =\n");
                        this.recoding.writeEncodingStepsInitializer(context, current_writer, indent);
                        current_writer.append(";\n");
                        current_writer.append(indent + "EncodingStep[] previous_recoding_steps_" + gom_id + " = ((Class_GOMExecutionContext) gom_execution_context).getEncodingSteps(context);\n");
                        current_writer.append(indent + "((Class_GOMExecutionContext) gom_execution_context).setEncodingSteps(context, this.merge(context, recoding_steps" + gom_id + ", previous_recoding_steps_" + gom_id + "));\n");
                    }

                    if (this.text_recoding != null) {
                        current_writer.append(indent + "EncodingStep[] text_recoding_steps_" + gom_id + " =\n");
                        this.text_recoding.writeEncodingStepsInitializer(context, current_writer, indent);
                        current_writer.append(";\n");
                        current_writer.append(indent + "EncodingStep[] previous_text_recoding_steps_" + gom_id + " = ((Class_GOMExecutionContext) gom_execution_context).getTextEncodingSteps(context);\n");
                        current_writer.append(indent + "((Class_GOMExecutionContext) gom_execution_context).setTextEncodingSteps(context, this.merge(context, text_recoding_steps" + gom_id + ", previous_text_recoding_steps_" + gom_id + "));\n");
                    }

                    current_writer.append(indent + "((Class_GOMExecutionContext) gom_execution_context).getOutput(context, false).print(com.sphenon.basics.message.t.s(context, ");

                    while ((c = this.reader.read()) != -1) {
                        current_writer.append((char) c);
                        ends_with_newline = (c == '\n');
                    }

                    current_writer.append("));\n");

                    if (this.recoding != null) {
                        current_writer.append(indent + "((Class_GOMExecutionContext) gom_execution_context).setEncodingSteps(context, previous_recoding_steps_" + gom_id + ");\n");
                    }

                    if (this.text_recoding != null) {
                        current_writer.append(indent + "((Class_GOMExecutionContext) gom_execution_context).setTextEncodingSteps(context, previous_text_recoding_steps_" + gom_id + ");\n");
                    }

                    current_writer.append(indent + "gom_processor.process(context, " + local_gom_index + ", " + gom_id + ");\n");
                    break;
            } 
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Could not write to java source file/stream (for template '%(template)')", "template", this.getRootNode(context).getTemplate(context).getFullClassName(context));
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }
    }
}
