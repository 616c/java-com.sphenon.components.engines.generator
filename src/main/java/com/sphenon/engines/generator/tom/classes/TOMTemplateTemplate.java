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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.javacode.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tchandler.classes.*;
import com.sphenon.engines.generator.returncodes.*;

import com.sphenon.basics.expression.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.IOException;

import java.util.Vector;

public class TOMTemplateTemplate extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMTemplateTemplate(CallContext context, TOMNode parent, String template_name, Vector<TCodeArgument> signature) throws InvalidTemplateSyntax {
        super(context, parent);
        this.template_name = template_name;
        this.signature = new Class_Signature(context, signature);
    }

    // Attributes ---------------------------------------------------------------------

    protected int            gom_id;
    protected int            local_gom_index;

    protected String template_name;
    protected Signature signature;

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    public void createJavaCode(CallContext context, Section section, JavaCodeManager java_code_manager, BufferedWriter current_writer, String indent) {
        try {
            switch (section) {
                case INIT_SECTION:
                    gom_id          = this.getRootNode(context).getNextGOMId(context);
                    local_gom_index = this.getRootNode(context).getNextLocalGOMIndex(context);
                    this.getRootNode(context).pushLocalGOMIndex(context);
                    for (TOMNode tom_node : this.getChildNodes(context)) {
                        tom_node.createJavaCode(context, Section.INIT_SECTION, java_code_manager, current_writer, indent);
                    }
                    this.getRootNode(context).popLocalGOMIndex(context);
                    break;
                case ATTRIBUTE_SECTION:
                    current_writer.append("    static protected GOMNode gom_root_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + ";\n");
                    // current_writer.append("    static protected java.util.Vector<GOMInsert> insert_nodes_" + this.template_name + ";\n");
                    for (TOMNode tom_node : this.getChildNodes(context)) {
                        tom_node.createJavaCode(context, Section.ATTRIBUTE_SECTION, java_code_manager, current_writer, indent);
                    }
                    break;
                case METHOD_SECTION:
                    current_writer.append("    protected GOMNode getGOMRoot_" + this.template_name + "(CallContext context) {\n");
                    current_writer.append("        return getGOMRoot_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + "(context);\n");
                    current_writer.append("    }\n");
                    current_writer.append("\n");
                    current_writer.append("    protected GOMNode getGOMRoot_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + "(CallContext context) {\n");
                    current_writer.append("        return gom_root_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + ";\n");
                    current_writer.append("    }\n");
                    current_writer.append("\n");
                    // current_writer.append("    protected java.util.Vector<GOMInsert> getInsertNodes" + this.template_name + "(CallContext context) {\n");
                    // current_writer.append("        if (insert_nodes_" + this.template_name + " == null) {\n");
                    // current_writer.append("            insert_nodes_" + this.template_name + " = new Vector<GOMInsert>();\n");
                    // current_writer.append("        }\n");
                    // current_writer.append("        return insert_nodes_" + this.template_name + ";\n");
                    // current_writer.append("    }\n");
                    // current_writer.append("\n");
                    current_writer.append("    static public GOMNode buildGOMTree_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + " (CallContext context, GOMNode gom_node, GOMNode current_gom_node) {\n");
                    current_writer.append("        current_gom_node = new GOMTemplate(context, gom_node, " + gom_id + ");\n");
                    current_writer.append("        gom_root_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + " = current_gom_node;\n");
                    if (this.getChildNodes(context) != null && this.getChildNodes(context).size() != 0) {
                        current_writer.append("        gom_node = current_gom_node;\n");
                        current_writer.append("\n");
                        for (TOMNode tom_node : this.getChildNodes(context)) {
                            tom_node.createJavaCode(context, Section.GOM_BUILDER_DECLARATION_SECTION, java_code_manager, current_writer, "        ");
                        }
                        current_writer.append("\n");
                        for (TOMNode tom_node : this.getChildNodes(context)) {
                            tom_node.createJavaCode(context, Section.GOM_BUILDER_SECTION, java_code_manager, current_writer, "        ");
                        }
                        current_writer.append("\n");
                        for (TOMNode tom_node : this.getChildNodes(context)) {
                            tom_node.createJavaCode(context, Section.GOM_BUILDER_LINKING_SECTION, java_code_manager, current_writer, "        ");
                        }
                        current_writer.append("\n");
                    }
                    current_writer.append("        return gom_root_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + ";\n");
                    current_writer.append("    }\n");
                    current_writer.append("\n");
                    current_writer.append("    public void generate" + this.template_name
                                                           + "(CallContext context, GeneratorOutputHandler output_handler, "
                                                           + "GOMExecutionContext gom_execution_context, GOMProcessor gom_processor"
                                                           + (    this.signature.isEmpty(context) == false ?
                                                                     (", " + this.signature.toString(context))
                                                                   : ""
                                                             )
                                                           + ") {\n");

                    current_writer.append("        gom_processor.process(context, -1, " + gom_id + ");\n");

                    for (TOMNode tom_node : this.getChildNodes(context)) {
                        tom_node.createJavaCode(context, Section.GENERATOR_CODE_SECTION, java_code_manager, current_writer, "        ");
                    }

                    current_writer.append("    }\n");
                    current_writer.append("\n");

                    for (TOMNode tom_node : this.getChildNodes(context)) {
                        tom_node.createJavaCode(context, Section.METHOD_SECTION, java_code_manager, current_writer, indent);
                    }
                    break;
                case GOM_BUILDER_SECTION:
                    current_writer.append(indent + "current_gom_node = buildGOMTree_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + "(context, gom_node, current_gom_node);\n");
                    break;
                case GENERATOR_CODE_SECTION:
                    current_writer.append(indent + "// template declaration: " + this.template_name + "\n");
                    current_writer.append(indent + "gom_processor.process(context, " + local_gom_index + ", this.getGOMRoot_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + "(context).getId(context));\n");
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

    public void dump(CallContext context, DumpNode dump_node) {
        dump_node.dump(context, "TOMNode", this.getClass().getName().replaceFirst(".*\\.",""));
        dump_node.dump(context, "TemplateName", this.template_name);
        Vector<TOMNode> childs = this.getChildNodes(context);
        if (childs != null && childs.size() != 0) {
            DumpNode dn = dump_node.openDump(context, "Childs ");
            for (TOMNode child : childs) {
                dn.dump(context, "Child", child);
            }
        }
    }
}
