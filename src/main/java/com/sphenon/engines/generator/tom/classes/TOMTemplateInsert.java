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
import com.sphenon.engines.generator.returncodes.*;

import com.sphenon.basics.expression.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.IOException;

import java.util.Vector;

public class TOMTemplateInsert extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMTemplateInsert(CallContext context, TOMNode parent, String template_name, String arguments, boolean is_this) throws InvalidTemplateSyntax {
        super(context, parent);
        this.template_name = template_name;
        this.arguments = arguments;
        this.is_this = is_this;
    }

    // Attributes ---------------------------------------------------------------------

    protected int            gom_id;
    protected int            local_gom_index;

    protected String  template_name;

    public String getTemplateName(CallContext context) {
        return this.template_name;
    }

    protected String  arguments;
    protected boolean is_this;

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    public boolean getSuperMode(CallContext context) {
        return is_this ? false : true;
    }

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
                    TOMSkeleton skeleton = findSuperNode (context, TOMSkeleton.class, null, true);
                    boolean already_inserted = skeleton.checkAndInsertSubTemplate(context, this.getTemplateName(context));

                    if (already_inserted == false) {
                        boolean have_super_insert = this.getRootNode(context).findSubNode(context, TOMTemplateInsert.class, new TOMCondition() { public boolean test(CallContext context, TOMNode tom_node) { return getTemplateName(context).equals(((TOMTemplateInsert)tom_node).getTemplateName(context)) && ((TOMTemplateInsert)tom_node).getSuperMode(context); } }) != null;
                    
                        current_writer.append("    static protected class GOMInsert_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + " extends GOMInsert {\n");
                        if (have_super_insert) {
                            current_writer.append("        protected boolean super_mode;\n");
                            current_writer.append("\n");
                        }
                        current_writer.append("        public GOMInsert_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + "(CallContext context, GOMNode parent, int id" + (have_super_insert ? ", boolean super_mode" : "") + ") {\n");
                        current_writer.append("            super(context, parent, id);\n");
                        if (have_super_insert) {
                            current_writer.append("            this.super_mode = super_mode;\n");
                        }
                        current_writer.append("        }\n");
                        current_writer.append("\n");
                        current_writer.append("        public GOMNode getTemplate(CallContext context, GOMExecutionContext gom_execution_context) {\n");
                        if (have_super_insert) {
                            current_writer.append("            if (super_mode) {\n");
                            current_writer.append("                return ((" + this.getRootNode(context).getTemplate(context).getClassName(context) + ")(gom_execution_context.getGenerator(context))).getSuperGOMRoot_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + "(context);\n");
                            current_writer.append("            } else {\n");
                        }
                        current_writer.append((have_super_insert ? "    " : "")+ "            return ((" + this.getRootNode(context).getTemplate(context).getClassName(context) + ")(gom_execution_context.getGenerator(context))).getThisGOMRoot_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + "(context);\n");
                        if (have_super_insert) {
                            current_writer.append("            }\n");
                        }
                        current_writer.append("        }\n");
                        current_writer.append("\n");
                        current_writer.append("        public String getTemplateId(CallContext context) {\n");
                        current_writer.append("            return \"" + this.template_name + "\";\n");
                        current_writer.append("        }\n");
                        current_writer.append("    }\n");
                        current_writer.append("\n");
                        if (have_super_insert) {
                            current_writer.append("    protected GOMNode getSuperGOMRoot_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + "(CallContext context) {\n");
                            current_writer.append("        return super.getGOMRoot_" + this.template_name + "(context);\n");
                            current_writer.append("    }\n");
                            current_writer.append("\n");
                        }
                        current_writer.append("    protected GOMNode getThisGOMRoot_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + "(CallContext context) {\n");
                        current_writer.append("        return this.getGOMRoot_" + this.template_name + "(context);\n");
                        current_writer.append("    }\n");
                        current_writer.append("\n");
                    }
                    break;
                case GOM_BUILDER_DECLARATION_SECTION:
                    break;
                case GOM_BUILDER_LINKING_SECTION:
                    break;
                case GOM_BUILDER_SECTION:
                    boolean have_super_insert = this.getRootNode(context).findSubNode(context, TOMTemplateInsert.class, new TOMCondition() { public boolean test(CallContext context, TOMNode tom_node) { return getTemplateName(context).equals(((TOMTemplateInsert)tom_node).getTemplateName(context)) && ((TOMTemplateInsert)tom_node).getSuperMode(context); } }) != null;
                    current_writer.append(indent + "current_gom_node = new GOMInsert_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "_" + this.template_name + "(context, gom_node, " + gom_id + (have_super_insert ? (", " + (is_this ? "false" : "true")) : "") + ");\n");
                    for (TOMNode tom_node : this.getChildNodes(context)) {
                        tom_node.createJavaCode(context, Section.GOM_BUILDER_SECTION, java_code_manager, current_writer, indent);
                    }
                    break;
                case GENERATOR_CODE_SECTION:
                    current_writer.append(indent + (is_this ? "this" : "super")
                                                 + ".generate" + this.template_name
                                                 + "(context, output_handler, "
                                                 + "gom_execution_context, gom_processor"
                                                 + (    this.arguments != null
                                                     && this.arguments.matches("^[ \\t\\n\\r\\f]*$") == false ?
                                                           (", " + this.arguments)
                                                         : ""
                                                   )
                                                 + ");\n");
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

    public void dump(CallContext context, DumpNode dump_node) {
        dump_node.dump(context, "TOMNode", this.getClass().getName().replaceFirst(".*\\.",""));
        dump_node.dump(context, "TemplateName", this.getTemplateName(context));
        Vector<TOMNode> childs = this.getChildNodes(context);
        if (childs != null && childs.size() != 0) {
            DumpNode dn = dump_node.openDump(context, "Childs ");
            for (TOMNode child : childs) {
                dn.dump(context, "Child", child);
            }
        }
    }
}
