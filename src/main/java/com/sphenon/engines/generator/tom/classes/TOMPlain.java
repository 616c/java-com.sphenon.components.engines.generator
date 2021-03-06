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

import com.sphenon.basics.expression.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.IOException;

public class TOMPlain extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMPlain(CallContext context, TOMNode parent) {
        super(context, parent);
    }

    // Attributes ---------------------------------------------------------------------

    protected int            gom_id;
    protected int            local_gom_index;
    protected Recoding       recoding;
    protected Recoding       text_recoding;

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public void createJavaCode(CallContext context, Section section, JavaCodeManager java_code_manager, BufferedWriter current_writer, String indent) {

        try {
            switch (section) {
                case INIT_SECTION:
                    this.recoding = (this.getRecoding(context));
                    this.text_recoding = (this.getTextRecoding(context));
                    if (this.recoding != null || this.text_recoding != null) {
                        gom_id          = this.getRootNode(context).getNextGOMId(context);
                        local_gom_index = this.getRootNode(context).getNextLocalGOMIndex(context);
                        this.getRootNode(context).pushLocalGOMIndex(context);
                    }

                    for (TOMNode tom_node : this.getChildNodes(context)) {
                        tom_node.createJavaCode(context, Section.INIT_SECTION, java_code_manager, current_writer, indent);
                    }

                    if (this.recoding != null || this.text_recoding != null) {
                        this.getRootNode(context).popLocalGOMIndex(context);
                    }
                    break;
                case GOM_BUILDER_SECTION:
                    if (this.recoding != null || this.text_recoding != null) {
                        current_writer.append(indent + "current_gom_node = new GOMRecoding(context, gom_node, " + gom_id + ");\n");
                        current_writer.append(indent + "gom_node = current_gom_node;\n");
                    }

                    for (TOMNode tom_node : this.getChildNodes(context)) {
                        tom_node.createJavaCode(context, Section.GOM_BUILDER_SECTION, java_code_manager, current_writer, indent);
                    }

                    if (this.recoding != null || this.text_recoding != null) {
                        current_writer.append(indent + "gom_node = gom_node.getParentNode(context);\n");
                    }
                    break;
                case GENERATOR_CODE_SECTION:
                    if (this.recoding != null) {
                        current_writer.append(indent + "EncodingStep[] steps_" + gom_id + " =\n");
                        this.recoding.writeEncodingStepsInitializer(context, current_writer, indent);
                        current_writer.append(";\n");
                        current_writer.append(indent + "((GOMRecoding)(gom_processor.getCurrentNode(context))).setEncodingSteps(context, steps_" + gom_id + ");\n");
                    }
                    if (this.text_recoding != null) {
                        current_writer.append(indent + "EncodingStep[] steps_text_" + gom_id + " =\n");
                        this.text_recoding.writeEncodingStepsInitializer(context, current_writer, indent);
                        current_writer.append(";\n");
                        current_writer.append(indent + "((GOMRecoding)(gom_processor.getCurrentNode(context))).setTextEncodingSteps(context, steps_text_" + gom_id + ");\n");
                    }
                    if (this.recoding != null || this.text_recoding != null) {
                        current_writer.append(indent + "gom_processor.process(context, -1, " + gom_id + ");\n");
                    }

                    for (TOMNode tom_node : this.getChildNodes(context)) {
                        tom_node.createJavaCode(context, Section.GENERATOR_CODE_SECTION, java_code_manager, current_writer, indent);
                    }

                    if (this.recoding != null || this.text_recoding != null) {
                        current_writer.append(indent + "// end (text) encoding\n");
                    }
                    break;
                default:
                    for (TOMNode tom_node : this.getChildNodes(context)) {
                        tom_node.createJavaCode(context, section, java_code_manager, current_writer, indent);
                    }
                    break;
            } 
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Could not write to java source file/stream (for template '%(template)')", "template", this.getRootNode(context).getTemplate(context).getFullClassName(context));
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }
    }
}
