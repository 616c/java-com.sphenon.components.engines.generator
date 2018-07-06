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
import com.sphenon.engines.generator.tchandler.classes.*;

import com.sphenon.basics.expression.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.IOException;

import java.util.Vector;

public class TOMTemplateLocatorTarget extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMTemplateLocatorTarget(CallContext context, TOMNode parent, String id, Vector<TCodeArgument> arguments) throws InvalidTemplateSyntax {
        super(context, parent);
        this.id = id;
        this.type = arguments == null || arguments.size() == 0 ? null : arguments.get(0).getType(context);
        this.variable = arguments == null || arguments.size() == 0  ? null : arguments.get(0).getName(context);

        this.is_declaration = (this.type != null);

        if (arguments != null && arguments.size() > 1) {
            this.accessory = new String[arguments.size()-1][];
            for (int i=1; i<arguments.size(); i++) {
                this.accessory[i-1] = new String[2];
                String atype = arguments.get(i).getType(context);
                String aname = arguments.get(i).getName(context);
                this.accessory[i-1][0] = atype;
                this.accessory[i-1][1] = aname;
            }
        }
    }

    // Attributes ---------------------------------------------------------------------

    protected int            gom_id;
    protected int            local_gom_index;

    protected boolean        is_declaration;

    protected String id;

    public String getId(CallContext context) {
        return id;
    }

    protected String type;

    public String getType(CallContext context) throws InvalidTemplateSyntax {
        if (this.type == null) {
            TOMTemplateLocatorTarget ttlt = findMatchingPredecessor(context);
            this.type = ttlt.getType(context);
            if (this.type == null) {
                InvalidTemplateSyntax.createAndThrow(context, "Template code 'locator target' refers to '%(id)', but no preceeding declaration was found", "id", this.id);
                throw (InvalidTemplateSyntax) null;
            }
        }
        return type;
    }

    protected String variable;

    public String getVariable(CallContext context) throws InvalidTemplateSyntax {
        if (this.variable == null) {
            TOMTemplateLocatorTarget ttlt = findMatchingPredecessor(context);
            this.variable = ttlt.getVariable(context);
            if (this.variable == null) {
                InvalidTemplateSyntax.createAndThrow(context, "Template code 'locator target' refers to '%(id)', but no preceeding declaration was found", "id", this.id);
                throw (InvalidTemplateSyntax) null;
            }
        }
        return variable;
    }

    protected String[][] accessory;

    public String[][] getAccessory(CallContext context) throws InvalidTemplateSyntax {
        if (this.accessory == null && this.is_declaration == false) {
            TOMTemplateLocatorTarget ttlt = findMatchingPredecessor(context);
            this.accessory = ttlt.getAccessory(context);
        }
        return accessory;
    }


    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    static public String getCurrentType(CallContext context, TOMNode tom_node) throws InvalidTemplateSyntax {
        return getCurrent(context, tom_node).getType(context);
    }

    static public String getCurrentVariable(CallContext context, TOMNode tom_node) throws InvalidTemplateSyntax {
        return getCurrent(context, tom_node).getVariable(context);
    }

    static public String[][] getCurrentAccessory(CallContext context, TOMNode tom_node) throws InvalidTemplateSyntax {
        return getCurrent(context, tom_node).getAccessory(context);
    }

    protected TOMTemplateLocatorTarget findMatchingPredecessor(CallContext context) throws InvalidTemplateSyntax {
        TOMTemplateLocatorTarget tn = this;
        do {
            tn = (TOMTemplateLocatorTarget) tn.findPreceedingNode (context, TOMTemplateLocatorTarget.class);
            if (tn != null && tn.getId(context).equals(this.id)) {
                break;
            }
        } while (tn != null);
        if (tn == null) {
            InvalidTemplateSyntax.createAndThrow(context, "Template code 'locator target' refers to '%(id)', but no preceeding declaration was found", "id", this.id);
            throw (InvalidTemplateSyntax) null;
        }
        return tn;
    }

    static protected TOMTemplateLocatorTarget getCurrent(CallContext context, TOMNode tom_node) throws InvalidTemplateSyntax {
        TOMTemplateLocatorTarget ttlt = (TOMTemplateLocatorTarget) tom_node.findPreceedingNode (context, TOMTemplateLocatorTarget.class, new TOMConditionTrue(), false, true);
        if (ttlt == null) {
            InvalidTemplateSyntax.createAndThrow(context, "No preceeding template code 'locator target' found (needed for pathes and loops)");
            throw (InvalidTemplateSyntax) null;
        }
        return ttlt;
    }

    // --------------------------------------------------------------------------------

    public void createJavaCode(CallContext context, Section section, JavaCodeManager java_code_manager, BufferedWriter current_writer, String indent) {
        if (this.is_declaration == false) { return; }
        try {
            switch (section) {
                case INIT_SECTION:
                    gom_id          = this.getRootNode(context).getNextGOMId(context);
                    local_gom_index = this.getRootNode(context).getNextLocalGOMIndex(context);
                    break;
                case ATTRIBUTE_SECTION:
                    current_writer.append("    protected java.util.Stack<" + type + "> " + variable + "_stack;\n");
                    current_writer.append("    protected java.util.Stack<Integer> " + variable + "_index_stack;\n");
                    current_writer.append("    protected java.util.Stack<Boolean> " + variable + "_passflag_stack;\n");
                    current_writer.append("    protected java.util.Stack<Object> " + variable + "_accessory_stack;\n");
                    current_writer.append("    protected " + type + " " + variable + ";\n");
                    current_writer.append("    protected int " + variable + "_index;\n");
                    current_writer.append("    protected boolean " + variable + "_passflag;\n");
                    if (accessory != null && accessory.length != 0) {
                        for (int aidx = 0; aidx < accessory.length; aidx++) {
                            current_writer.append("    protected " + accessory[aidx][0] + " " + accessory[aidx][1] + ";\n");
                        }
                    }
                    break;
                case GOM_BUILDER_SECTION:
                    current_writer.append(indent + "current_gom_node = new GOMJavaCode(context, gom_node, " + gom_id + ");\n");
                    break;
                case GENERATOR_CODE_SECTION:
                    current_writer.append(indent + variable + " = null;\n");
                    current_writer.append(indent + variable + "_index = 0;\n");
                    current_writer.append(indent + variable + "_passflag = false;\n");
                    current_writer.append(indent + variable + "_stack = new java.util.Stack<" + type + ">();\n");
                    current_writer.append(indent + variable + "_index_stack = new java.util.Stack<Integer>();\n");
                    current_writer.append(indent + variable + "_passflag_stack = new java.util.Stack<Boolean>();\n");
                    current_writer.append(indent + variable + "_accessory_stack = new java.util.Stack<Object>();\n");
                    if (accessory != null && accessory.length != 0) {
                        for (int aidx = 0; aidx < accessory.length; aidx++) {
                            current_writer.append(indent + accessory[aidx][1] + " = null;\n");
                        }
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
