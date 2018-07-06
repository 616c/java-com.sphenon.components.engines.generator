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
import com.sphenon.engines.generator.returncodes.*;

import com.sphenon.basics.expression.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.IOException;

public class TOMTemplateInsertExternal extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMTemplateInsertExternal(CallContext context, TOMNode parent, String generator_name, String template_name_code, String arguments) throws InvalidTemplateSyntax {
        super(context, parent);
        this.generator_name = generator_name;
        if (this.generator_name != null && this.generator_name.isEmpty()) {
            this.generator_name = null;
        }
        this.template_name_code = template_name_code;
        if (this.template_name_code != null && this.template_name_code.isEmpty()) {
            this.template_name_code = null;
        }
        this.arguments = arguments;
    }

    // Attributes ---------------------------------------------------------------------

    protected int            gom_id;
    protected int            local_gom_index;

    protected String generator_name;
    protected String template_name_code;
    protected String arguments;

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    public void createJavaCode(CallContext context, Section section, JavaCodeManager java_code_manager, BufferedWriter current_writer, String indent) {
        try {
            switch (section) {
                case INIT_SECTION:
                    gom_id          = this.getRootNode(context).getNextGOMId(context);
                    local_gom_index = this.getRootNode(context).getNextLocalGOMIndex(context);
                    break;
                case IMPORT_SECTION:
                    break;
                case ATTRIBUTE_SECTION:
                    break;
                case METHOD_SECTION:
                    break;
                case GOM_BUILDER_DECLARATION_SECTION:
                    break;
                case GOM_BUILDER_LINKING_SECTION:
                    break;
                case GOM_BUILDER_SECTION:
                    current_writer.append(indent + "current_gom_node = new GOMInsertExternal(context, gom_node, " + gom_id + ");\n");
                    break;
                case GENERATOR_CODE_SECTION:
                    if (    this.generator_name != null
                         && this.template_name_code != null
                       ) {
                        current_writer.append(indent + "Generator " + this.generator_name + ";\n");
                    }
                    current_writer.append(indent + "{\n"); 
                    if (this.template_name_code != null) {
                        current_writer.append(indent + "    "
                                                     + (this.generator_name != null ? this.generator_name : "Generator generator")
                                                     + " = GeneratorRegistry.mustGetGenerator(context, "
                                                     + this.template_name_code + ", "
                                                     + "\"" + this.getRootNode(context).getTemplate(context).getPackageName(context) + "\""
                                                     + ");");
                    }
                    if (    (    this.generator_name != null
                              && this.template_name_code == null
                            )
                         || (    this.generator_name == null
                              && this.template_name_code != null
                            )
                       ) {
                        current_writer.append(indent + "    ((GeneratorInternal) "
                                                     + (this.generator_name != null ? this.generator_name : "generator")
                                                     + ").generate(context, " 
                                                     + "output_handler, gom_execution_context"
                                                     + (    this.arguments != null
                                                         && this.arguments.matches("^[ \\t\\n\\r\\f]*$") == false ?
                                                               (", " + this.arguments)
                                                             : ""
                                                       )
                                                     + ");\n");
                    }
                    current_writer.append(indent + "}\n");
                    current_writer.append(indent + "gom_processor.process(context, " + local_gom_index + ", " + gom_id + ");\n");
                    break;
                case DATA_SECTION:
                    break;
            } 
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Could not write to java source file/stream (for template '%(template)')", "template", this.getRootNode(context).getTemplate(context).getFullClassName(context));
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }
    }
}
