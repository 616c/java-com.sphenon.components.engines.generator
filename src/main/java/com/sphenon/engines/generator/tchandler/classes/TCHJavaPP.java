package com.sphenon.engines.generator.tchandler.classes;

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

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tom.classes.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.StringReader;
import java.io.BufferedReader;

import java.util.Stack;
import java.util.Vector;

/* [TODO] Subtemplate Argument Passing

   1. current_xxx Zeugs aus dieser Klasse entfernen und in den TOM-Baum
      integrieren 
   2. in template insert&define dann die Information benutzen, um
      eine automatische Übergabe der current locator target variablen
      einzubauen
      (also im template:  insert this.xyz() und template xyz(),
       im code aber insert this.xaz(object, accessory) usw.)
 */

public class TCHJavaPP implements TCHandler {

    protected int variable_counter;
    protected Stack<String>      current_block_type;
    protected Stack<String>      current_block_variable;
    protected Stack<String>      current_block_alias;
    protected Stack<String>      current_block_alias_type;
    protected Stack<String[][]>  current_block_accessory;
    protected Stack<Integer>     current_block_index;

    public TCHJavaPP (CallContext context) {
        this.variable_counter         = 0;
        this.if_level                 = 0;
        this.current_block_type       = new Stack<String>();
        this.current_block_variable   = new Stack<String>();
        this.current_block_alias      = new Stack<String>();
        this.current_block_alias_type = new Stack<String>();
        this.current_block_accessory  = new Stack<String[][]>();
        this.current_block_index      = new Stack<Integer>();
    }

    protected TCHandler java_code_handler;

    public TCHandler getJavaCodeHandler (CallContext context) {
        return this.java_code_handler;
    }

    public void setJavaCodeHandler (CallContext context, TCHandler java_code_handler) {
        this.java_code_handler = java_code_handler;
    }

    public TOMNode handle(CallContext context, TCEvent event, TOMNode current_node, BufferedReader reader) throws InvalidTemplateSyntax{
        JavaPP jpp = new JavaPP (context, reader, this);

        try {
            current_node = jpp.Java(context, event, current_node);
        } catch (ParseException pe) {
            InvalidTemplateSyntax.createAndThrow(context, pe, "Invalid Java Code (JPP) at " + jpp.getPosition(context));
            throw (InvalidTemplateSyntax) null;
        } catch (TokenMgrError tme) {
            InvalidTemplateSyntax.createAndThrow(context, tme, "Invalid Java Code (JPP) at " + jpp.getPosition(context));
            throw (InvalidTemplateSyntax) null;
        } catch (InvalidTemplateSyntax its) {
            InvalidTemplateSyntax.createAndThrow(context, its, "Invalid Java Code (JPP) at " + jpp.getPosition(context));
            throw (InvalidTemplateSyntax) null;
        }

        return current_node;
    }

    protected StringBuffer buffer;

    public TOMNode beginHandling (CallContext context, TCEvent event, TOMNode current_node) throws InvalidTemplateSyntax {
        this.buffer = new StringBuffer();
        return current_node;
    }

    public TOMNode endHandling (CallContext context, TCEvent event, TOMNode current_node, Vector recoding) throws InvalidTemplateSyntax {
        if (this.buffer.length() > 0) {
            TOMNode save_node = null;
            if (recoding != null && recoding.size() != 0) {
                save_node = current_node;
                current_node = new TOMPlain(context, current_node);

                current_node.getProperties(context).setRecoding(context, new Recoding(context, (Vector<String[]>) recoding));
            }

            StringReader string_reader = new StringReader(buffer.toString());
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            current_node = this.java_code_handler.handle(context, event, current_node, buffered_reader);

            if (save_node != null) {
                current_node = save_node;
            }
        }
        return current_node;
    }

    protected int do_buffering = 0;
    protected Stack<Vector<StringBuilder>> buffer_parts_stack;
    protected Vector<StringBuilder> last_buffer_parts;
    protected StringBuilder current_buffer_part;

    protected void processCode(CallContext context, String code, boolean comma) {
        if (code != null && code.length() != 0) {
            if (this.do_buffering > 0) {
                if (comma) {
                    this.current_buffer_part = new StringBuilder();
                    this.buffer_parts_stack.peek().add(this.current_buffer_part);
                } else {
                    current_buffer_part.append(code);
                }
            } else {
                this.buffer.append(code);
            }
        }
    }

    public TOMNode handleCode (CallContext context, TCEvent event, TOMNode current_node, String java_code) throws InvalidTemplateSyntax {
        processCode(context, java_code, false);
        return current_node;
    }

    public TOMNode handleComma (CallContext context, TCEvent event, TOMNode current_node, String java_code) throws InvalidTemplateSyntax {
        processCode(context, java_code, true);
        return current_node;
    }

    protected void beginBuffering (CallContext context) {
        this.do_buffering++;
        if (this.buffer_parts_stack == null) {
            this.buffer_parts_stack = new Stack<Vector<StringBuilder>>();
        }
        this.buffer_parts_stack.push(new Vector<StringBuilder>());
        this.current_buffer_part = new StringBuilder();
        this.buffer_parts_stack.peek().add(this.current_buffer_part);
    }

    protected void stopBuffering (CallContext context) {
        this.do_buffering--;
        this.last_buffer_parts = this.buffer_parts_stack.pop();
        if (this.do_buffering > 0) {
            Vector<StringBuilder> parts = this.buffer_parts_stack.peek();
            this.current_buffer_part = parts.get(parts.size()-1);
        }
    }

    protected StringBuilder getLastBufferString (CallContext context) {
        boolean firstp = true;
        StringBuilder result = null;
        for (StringBuilder bp : this.last_buffer_parts) {
            if (firstp) {
                result = bp;
                firstp = false;
            } else {
                result.append(",");
                result.append(bp.toString());
            }
        }
        return result;
    }

    public TOMNode handleLocator (CallContext context, TCEvent event, TOMNode current_node, String locator, boolean is_delimited, String base, String cast, String dynamic_arguments) throws InvalidTemplateSyntax {
        if (locator != null && locator.length() > (is_delimited ? 2 : 0)) {
            TOMNode start = current_node;
            if (start.getChildNodes(context).size() > 0) {
                start = start.getChildNodes(context).lastElement();
            }
            String variable = (base != null ? base : TOMTemplateLocatorTarget.getCurrentVariable(context, start));
            String locator_string = (is_delimited ? locator.substring(1,locator.length()-1) : locator);
            if (dynamic_arguments != null) {
                if (dynamic_arguments.matches(" *") == false) {
                    dynamic_arguments = ", " + dynamic_arguments;
                }
                locator_string = "\" + DynamicString.process(context, \"" + locator_string + "\", null" + dynamic_arguments + ") + \"";
            }
            String locator_code = "resolveLocator(context, \"ctn://" + locator_string + "\", " + variable + ")";
            if (cast != null && cast.length() != 0) {
                locator_code = "((" + cast + ") " + locator_code + ")";
            }
            processCode(context, locator_code, false);
        }
        return current_node;
    }

    public TOMNode handleExpression (CallContext context, TCEvent event, TOMNode current_node, String expression, String cast, String arguments) throws InvalidTemplateSyntax {
        if (expression != null && expression.isEmpty() == false) {
            TOMNode start = current_node;
            if (start.getChildNodes(context).size() > 0) {
                start = start.getChildNodes(context).lastElement();
            }
            String variables = (arguments != null ? arguments : ("\"current\", " + TOMTemplateLocatorTarget.getCurrentVariable(context, start)));
            String expression_code = "evaluateExpression(context, \"" + expression + "\", " + variables + ")";
            if (cast != null && cast.length() != 0) {
                expression_code = "((" + cast + ") " + expression_code + ")";
            }
            processCode(context, expression_code, false);
        }
        return current_node;
    }

    public TOMNode handleJoinBegin (CallContext context, TCEvent event, TOMNode current_node) throws InvalidTemplateSyntax {
        processCode(context, "com.sphenon.basics.system.StringUtilities.join(context, ", false);
        return current_node;
    }

    public TOMNode handleJoinEnd (CallContext context, TCEvent event, TOMNode current_node) throws InvalidTemplateSyntax {
        processCode(context, ")", false);
        return current_node;
    }

    public TOMNode handleKeywordFunction (CallContext context, TCEvent event, TOMNode current_node, String keyword) throws InvalidTemplateSyntax {
        switch (keyword) {
            case "time"    : processCode(context, "com.sphenon.basics.system.SystemUtilities.getDate(context, ", false);
            case "format"  : processCode(context, "com.sphenon.basics.system.SystemUtilities.format(context, ", false);
            case "size"    : processCode(context, "com.sphenon.basics.system.MathUtilities.size(context, ", false);
            case "average" : processCode(context, "com.sphenon.basics.system.MathUtilities.average(context, ", false);
            case "product" : processCode(context, "com.sphenon.basics.system.MathUtilities.product(context, ", false);
            case "sum"     : processCode(context, "com.sphenon.basics.system.MathUtilities.sum(context, ", false);
            case "minimum" : processCode(context, "com.sphenon.basics.system.MathUtilities.minimum(context, ", false);
            case "maximum" : processCode(context, "com.sphenon.basics.system.MathUtilities.maximum(context, ", false);
            case "reverse" : processCode(context, "com.sphenon.basics.many.ReverseList.create(context, ", false);
        }
        return current_node;
    }

    protected int if_level; // well, normally boolean - but who knows what we'll find

    public TOMNode handleIfBegin (CallContext context, TCEvent event, TOMNode current_node) throws InvalidTemplateSyntax {
        this.if_level++;
        processCode(context, "if(", false);
        return current_node;
    }

    public TOMNode handleIfEnd (CallContext context, TCEvent event, TOMNode current_node) throws InvalidTemplateSyntax {
        this.if_level--;
        processCode(context, ")", false);
        return current_node;
    }

    public TOMNode handlePPOpeningKeywordBegin (CallContext context, TCEvent event, TOMNode current_node, String keyword) throws InvalidTemplateSyntax {
        boolean isa = false;
        boolean optvarname = false;
        boolean iterable_mode = false;
        boolean message = false;
        String code = null;
        if (keyword.equals("for")) {
            TOMNode start = current_node;
            if (start.getChildNodes(context).size() > 0) {
                start = start.getChildNodes(context).lastElement();
            }

            String type          = TOMTemplateLocatorTarget.getCurrentType(context, start);
            String variable      = TOMTemplateLocatorTarget.getCurrentVariable(context, start);
            String accessory[][] = TOMTemplateLocatorTarget.getCurrentAccessory(context, start);
            int    index         = variable_counter++;
            this.current_block_type.push(type);
            this.current_block_variable.push(variable);
            this.current_block_alias.push(null);
            this.current_block_alias_type.push(null);
            this.current_block_accessory.push(accessory);
            this.current_block_index.push(index);

            code  = "{ " + variable + "_stack.push(" + variable + ");\n";
            code += variable + "_index_stack.push(" + variable + "_index); " + variable + "_index = 0;\n";
            code += variable + "_passflag_stack.push(" + variable + "_passflag); " + variable + "_passflag = false;\n";

            if (accessory != null && accessory.length != 0) {
                for (int aidx = 0; aidx < accessory.length; aidx++) {
                    code += variable + "_accessory_stack.push(" + accessory[aidx][1] + ");\n";
                }
            }

            code += "for (" + type + " " + variable + "_" + index + " : new " + type + "Iterable(context,";
            iterable_mode = true;
        } else if (keyword.matches("exists|notexists|empty|notempty|isvalid|isinvalid|element|notelement")) {
            TOMNode start = current_node;
            if (start.getChildNodes(context).size() > 0) {
                start = start.getChildNodes(context).lastElement();
            }

            String type          = TOMTemplateLocatorTarget.getCurrentType(context, start);
            String variable      = TOMTemplateLocatorTarget.getCurrentVariable(context, start);
            String accessory[][] = TOMTemplateLocatorTarget.getCurrentAccessory(context, start);
            int    index         = variable_counter++;
            this.current_block_type.push(type);
            this.current_block_variable.push(variable);
            this.current_block_alias.push(null);
            this.current_block_alias_type.push(null);
            this.current_block_accessory.push(accessory);
            this.current_block_index.push(index);

            if (if_level == 0) {
                code  = "{ " + variable + "_stack.push(" + variable + ");\n";
                
                if (accessory != null && accessory.length != 0) {
                    for (int aidx = 0; aidx < accessory.length; aidx++) {
                        code += variable + "_accessory_stack.push(" + accessory[aidx][1] + ");\n";
                    }
                }

                code += type + "ExistenceCheck " + variable + "_" + index + " = new " + type + "ExistenceCheck(context, ";
            } else {
                code = "(new " + type + "ExistenceCheck(context, ";
            }
        } else if (keyword.matches("select")) {
            TOMNode start = current_node;
            if (start.getChildNodes(context).size() > 0) {
                start = start.getChildNodes(context).lastElement();
            }

            String type          = TOMTemplateLocatorTarget.getCurrentType(context, start);
            String variable      = TOMTemplateLocatorTarget.getCurrentVariable(context, start);
            String accessory[][] = TOMTemplateLocatorTarget.getCurrentAccessory(context, start);
            int    index         = variable_counter++;
            this.current_block_type.push(type);
            this.current_block_variable.push(variable);
            this.current_block_alias.push(null);
            this.current_block_alias_type.push(null);
            this.current_block_accessory.push(accessory);
            this.current_block_index.push(index);

            code  = "{ " + variable + "_stack.push(" + variable + ");\n";

            if (accessory != null && accessory.length != 0) {
                for (int aidx = 0; aidx < accessory.length; aidx++) {
                    code += variable + "_accessory_stack.push(" + accessory[aidx][1] + ");\n";
                }
            }

            code += type + " " + variable + "_" + index + " = (" + type + ") (";
        } else if (keyword.matches("first|notfirst")) {
            optvarname = true;
        } else if (keyword.matches("declarepass")) {
            optvarname = true;
        } else if (keyword.matches("declareindex")) {
            optvarname = true;
        } else if (keyword.equals("current")) {
            TOMNode start = current_node;
            if (start.getChildNodes(context).size() > 0) {
                start = start.getChildNodes(context).lastElement();
            }

            String variable = TOMTemplateLocatorTarget.getCurrentVariable(context, start);

            code  = variable;
        } else if (keyword.equals("index")) {
            optvarname = true;
        } else if (keyword.matches("union")) {
            TOMNode start = current_node;
            if (start.getChildNodes(context).size() > 0) {
                start = start.getChildNodes(context).lastElement();
            }

            String type      = TOMTemplateLocatorTarget.getCurrentType(context, start);

            code = "(new " + type + "Iterable(context, ";
            iterable_mode = true;
        } else if (keyword.matches("isa|notisa")) {
            TOMNode start = current_node;
            if (start.getChildNodes(context).size() > 0) {
                start = start.getChildNodes(context).lastElement();
            }

            String type          = TOMTemplateLocatorTarget.getCurrentType(context, start);
            String variable      = TOMTemplateLocatorTarget.getCurrentVariable(context, start);
            String accessory[][] = TOMTemplateLocatorTarget.getCurrentAccessory(context, start);
            int    index         = variable_counter++;
            this.current_block_type.push(type);
            this.current_block_variable.push(variable);
            this.current_block_alias.push(null);
            this.current_block_alias_type.push(null);
            this.current_block_accessory.push(accessory);
            this.current_block_index.push(index);

            if (if_level == 0) {
                code  = "{ if ((";
            } else {
                code  = "((";
            }

            isa = true;
        } else if (keyword.matches("indent")) {
            code = " gom_execution_context.pushIndent(context, ";
        } else if (keyword.matches("else")) {
        } else if (keyword.matches("message")) {
            this.current_block_alias.push(null);
            message = true;
        } else {
            InvalidTemplateSyntax.createAndThrow(context, "Invalid java preprocessor keyword '%(keyword)'", "keyword", keyword);
            throw (InvalidTemplateSyntax) null;
        }
        processCode(context, code, false);
        if (isa || iterable_mode || optvarname || message) {
            this.beginBuffering(context);
        }
        return current_node;
    }

    public TOMNode handlePPOpeningKeywordAlias (CallContext context, TCEvent event, TOMNode current_node, String keyword, String alias) throws InvalidTemplateSyntax {
        this.current_block_alias.set(this.current_block_alias.size() - 1, alias);
        return current_node;
    }

    public TOMNode handlePPOpeningKeywordAliasType (CallContext context, TCEvent event, TOMNode current_node, String keyword, String alias_type) throws InvalidTemplateSyntax {
        this.current_block_alias_type.set(this.current_block_alias_type.size() - 1, alias_type);
        return current_node;
    }

    public TOMNode handlePPOpeningKeywordEnd (CallContext context, TCEvent event, TOMNode current_node, String keyword) throws InvalidTemplateSyntax {
        String code = null;
        if (keyword.equals("for")) {
            this.stopBuffering(context);

            boolean firstp = true;
            for (StringBuilder bp : this.last_buffer_parts) {
                this.processCode(context, (firstp ? "" : ",") + "(Object) (" + bp.toString() + ")", false);
                firstp = false;
            }

            String type       = this.current_block_type.peek();
            String variable   = this.current_block_variable.peek();
            String alias      = this.current_block_alias.peek();
            String alias_type = this.current_block_alias_type.peek();
            boolean use_default_variable = true;
            if (alias != null && alias.length() > 0 && alias.charAt(0) == '-') {
                use_default_variable = false;
                alias = alias.substring(1);
            }
            String accessory[][] = this.current_block_accessory.peek();
            int    index         = this.current_block_index.peek();

            code  = ")) {\n";
            if (use_default_variable) {
                code  += variable + " = " + variable + "_" + index + ";\n";
                if (accessory != null && accessory.length != 0) {
                    for (int aidx = 0; aidx < accessory.length; aidx++) {
                        code += accessory[aidx][1] + " = new " + accessory[aidx][0] + "(context, " + variable + ");\n";
                    }
                }
            }
            if (alias != null && alias.length() != 0) {
                if (use_default_variable) {
                    code += (alias_type != null ? alias_type : type) + " " + alias + " = " + (alias_type != null ? ("(" + alias_type + ")") : "") + variable + ";\n";
                    if (accessory != null && accessory.length != 0) {
                        for (int aidx = 0; aidx < accessory.length; aidx++) {
                            code += accessory[aidx][0] + " " + alias + "_" + accessory[aidx][1] + " = " + accessory[aidx][1] + ";\n";
                        }
                    }
                } else {
                    code += (alias_type != null ? alias_type : type) + " " + alias + " = " + (alias_type != null ? ("(" + alias_type + ")") : "") + variable + "_" + index + ";\n";
                    if (accessory != null && accessory.length != 0) {
                        for (int aidx = 0; aidx < accessory.length; aidx++) {
                            code += accessory[aidx][0] + " " + alias + "_" + accessory[aidx][1] + " = new " + accessory[aidx][0] + "(context, " + alias + ");\n";
                        }
                    }
                }
            }
        } else if (keyword.matches("exists|notexists|empty|notempty|isvalid|isinvalid|element|notelement")) {
            String type       = this.current_block_type.peek();
            String variable   = this.current_block_variable.peek();
            String alias      = this.current_block_alias.peek();
            String alias_type = this.current_block_alias_type.peek();
            boolean use_default_variable = true;
            if (alias != null && alias.length() > 0 && alias.charAt(0) == '-') {
                use_default_variable = false;
                alias = alias.substring(1);
            }
            if (keyword.matches("isvalid|isinvalid")) {
                use_default_variable = false;
            }
            String accessory[][] = this.current_block_accessory.peek();
            int    index         = this.current_block_index.peek();

            if (if_level == 0) {
                code  = ");\n";
                code += "if (" + variable + "_" + index + "." + keyword + "(context)) {\n";
                if (use_default_variable) {
                    code += variable + " = " + variable + "_" + index + ".getValue(context);\n";
                    if (accessory != null && accessory.length != 0) {
                        for (int aidx = 0; aidx < accessory.length; aidx++) {
                            code += accessory[aidx][1] + " = new " + accessory[aidx][0] + "(context, " + variable + ");\n";
                        }
                    }
                }
                if (alias != null && alias.length() != 0) {
                    if (use_default_variable) {
                        code += (alias_type != null ? alias_type : type) + " " + alias + " = " + (alias_type != null ? ("(" + alias_type + ")") : "") + variable + ";\n";
                        if (accessory != null && accessory.length != 0) {
                            for (int aidx = 0; aidx < accessory.length; aidx++) {
                                code += accessory[aidx][0] + " " + alias + "_" + accessory[aidx][1] + " = " + accessory[aidx][1] + ";\n";
                            }
                        }
                    } else {
                        code += (alias_type != null ? alias_type : type) + " " + alias + " = " + (alias_type != null ? ("(" + alias_type + ")") : "") + variable + "_" + index + ".getValue(context);\n";
                        if (accessory != null && accessory.length != 0) {
                            for (int aidx = 0; aidx < accessory.length; aidx++) {
                                code += accessory[aidx][0] + " " + alias + "_" + accessory[aidx][1] + " = new " + accessory[aidx][0] + "(context, " + alias + ");\n";
                            }
                        }
                    }
                }
            } else {
                code  = "))." + keyword + "(context)";
            }
        } else if (keyword.matches("select")) {
            String type       = this.current_block_type.peek();
            String variable   = this.current_block_variable.peek();
            String alias      = this.current_block_alias.peek();
            String alias_type = this.current_block_alias_type.peek();
            boolean use_default_variable = true;
            if (alias != null && alias.length() > 0 && alias.charAt(0) == '-') {
                use_default_variable = false;
                alias = alias.substring(1);
            }
            String accessory[][] = this.current_block_accessory.peek();
            int    index         = this.current_block_index.peek();

            code  = ");\n";
            code += "{\n";
            if (use_default_variable) {
                code  += variable + " = " + variable + "_" + index + ";\n";
                if (accessory != null && accessory.length != 0) {
                    for (int aidx = 0; aidx < accessory.length; aidx++) {
                        code += accessory[aidx][1] + " = new " + accessory[aidx][0] + "(context, " + variable + ");\n";
                    }
                }
            }
            if (alias != null && alias.length() != 0) {
                if (use_default_variable) {
                    code += (alias_type != null ? alias_type : type) + " " + alias + " = " + (alias_type != null ? ("(" + alias_type + ")") : "") + variable + ";\n";
                    if (accessory != null && accessory.length != 0) {
                        for (int aidx = 0; aidx < accessory.length; aidx++) {
                            code += accessory[aidx][0] + " " + alias + "_" + accessory[aidx][1] + " = " + accessory[aidx][1] + ";\n";
                        }
                    }
                } else {
                    code += (alias_type != null ? alias_type : type) + " " + alias + " = " + (alias_type != null ? ("(" + alias_type + ")") : "") + variable + "_" + index + ";\n";
                    if (accessory != null && accessory.length != 0) {
                        for (int aidx = 0; aidx < accessory.length; aidx++) {
                            code += accessory[aidx][0] + " " + alias + "_" + accessory[aidx][1] + " = new " + accessory[aidx][0] + "(context, " + alias + ");\n";
                        }
                    }
                }
            }
        } else if (keyword.matches("first|notfirst")) {
            this.stopBuffering(context);

            String varname = this.getLastBufferString(context).toString();
            if (varname == null || varname.matches(" *")) {
                TOMNode start = current_node;
                if (start.getChildNodes(context).size() > 0) {
                    start = start.getChildNodes(context).lastElement();
                }

                String variable = TOMTemplateLocatorTarget.getCurrentVariable(context, start);

                varname = variable;
            }
            
            String passvar = varname + "_passed";
            varname += "_passflag";

            code  = "{ boolean " + passvar + " = (" + varname + ");\n";
            code += "  " + varname + " = true;\n";
            code += "  if (" + passvar + " == " + (keyword.equals("first") ? "false" : "true") + ") {\n";
        } else if (keyword.matches("declarepass")) {
            this.stopBuffering(context);

            String varname = this.getLastBufferString(context).toString();

            code  = "boolean " + varname + "_passflag" + " = false;\n";
        } else if (keyword.matches("declareindex")) {
            this.stopBuffering(context);

            String varname = this.getLastBufferString(context).toString();

            code  = "int " + varname + "_index" + " = 0;\n";
        } else if (keyword.equals("current")) {
        } else if (keyword.equals("index")) {
            this.stopBuffering(context);

            String varname = this.getLastBufferString(context).toString();
            if (varname == null || varname.matches(" *")) {
                TOMNode start = current_node;
                if (start.getChildNodes(context).size() > 0) {
                    start = start.getChildNodes(context).lastElement();
                }

                String variable = TOMTemplateLocatorTarget.getCurrentVariable(context, start);

                varname = variable;
            }

            code = varname + "_index";
        } else if (keyword.equals("union")) {
            this.stopBuffering(context);

            boolean firstp = true;
            for (StringBuilder bp : this.last_buffer_parts) {
                this.processCode(context, (firstp ? "" : ",") + "(Object) (" + bp.toString() + ")", false);
                firstp = false;
            }

            code  = "))";
        } else if (keyword.matches("isa|notisa")) {
            // processCode(context, ",", true);
            this.stopBuffering(context);

            String type          = this.current_block_type.peek();
            String variable      = this.current_block_variable.peek();
            String alias         = this.current_block_alias.peek();
            String alias_type    = this.current_block_alias_type.peek();
            String accessory[][] = this.current_block_accessory.peek();
            int    index         = this.current_block_index.peek();

            if (this.last_buffer_parts.size() != 2) {
                InvalidTemplateSyntax.createAndThrow(context, "Argument of Java preprocessor keyword 'isa' must be a list with exactly two comma separated Java code pieces");
                throw (InvalidTemplateSyntax) null;
            }

            code  = "(" + this.last_buffer_parts.get(0).toString() + ") instanceof " + this.last_buffer_parts.get(1).toString();

            code += ") == " + (keyword.equals("isa") ? "true" : "false");

            if (if_level == 0) {
                code += ") {\n";
                String cast_type = this.last_buffer_parts.get(1).toString();
                if (alias != null && alias.length() != 0) {
                    code += cast_type + " " + alias + " = (" + cast_type + ") " + variable + ";\n";
                }
            } else {
                code += ")";
            }
        } else if (keyword.matches("indent")) {
            code = ");\n";
        } else if (keyword.matches("else")) {
            code = " }else{";
        } else if (keyword.matches("message")) {
            this.stopBuffering(context);

            String alias         = this.current_block_alias.pop();

            String message_code = this.getLastBufferString(context).toString();
            int cp = message_code.indexOf(':');

            code = ((alias == null || alias.isEmpty()) ? "" : (" if (" + alias + ") {")) + " System.err.println(String.format(" + message_code + "));" + ((alias == null || alias.isEmpty()) ? "" : " }") + "\n";
        } else {
            InvalidTemplateSyntax.createAndThrow(context, "Invalid java preprocessor keyword '%(keyword)'", "keyword", keyword);
            throw (InvalidTemplateSyntax) null;
        }
        processCode(context, code, false);
        return current_node;
    }

    public TOMNode handlePPClosingKeyword (CallContext context, TCEvent event, TOMNode current_node, String keyword) throws InvalidTemplateSyntax {
        String code = null;
        if (keyword.equals("for")) {
            String type          = this.current_block_type.pop();
            String variable      = this.current_block_variable.pop();
            String alias         = this.current_block_alias.pop();
            String alias_type    = this.current_block_alias_type.pop();
            String accessory[][] = this.current_block_accessory.pop();
            int    index         = this.current_block_index.pop();

            code  = "\n" + variable + "_index++;\n";
            code += "}\n";

            if (accessory != null && accessory.length != 0) {
                for (int aidx = 0; aidx < accessory.length; aidx++) {
                    code += accessory[aidx][1] + " = (" + accessory[aidx][0] + ") " + variable + "_accessory_stack.pop();\n";
                }
            }

            code += variable + "_index = " + variable + "_index_stack.pop();\n";
            code += variable + " = " + variable + "_stack.pop(); }\n";
        } else if (keyword.matches("exists|notexists|empty|notempty|isvalid|isinvalid|element|notelement")) {
            String type          = this.current_block_type.pop();
            String variable      = this.current_block_variable.pop();
            String alias         = this.current_block_alias.pop();
            String alias_type    = this.current_block_alias_type.pop();
            String accessory[][] = this.current_block_accessory.pop();
            int    index         = this.current_block_index.pop();

            code = "\n}\n";

             if (accessory != null && accessory.length != 0) {
                for (int aidx = 0; aidx < accessory.length; aidx++) {
                    code += accessory[aidx][1] + " = (" + accessory[aidx][0] + ") " + variable + "_accessory_stack.pop();\n";
                }
            }

            code += variable + " = " + variable + "_stack.pop(); }\n";
        } else if (keyword.matches("select")) {
            String type          = this.current_block_type.pop();
            String variable      = this.current_block_variable.pop();
            String alias         = this.current_block_alias.pop();
            String alias_type    = this.current_block_alias_type.pop();
            String accessory[][] = this.current_block_accessory.pop();
            int    index         = this.current_block_index.pop();

            code = "\n}\n";

             if (accessory != null && accessory.length != 0) {
                for (int aidx = 0; aidx < accessory.length; aidx++) {
                    code += accessory[aidx][1] + " = (" + accessory[aidx][0] + ") " + variable + "_accessory_stack.pop();\n";
                }
            }

            code += variable + " = " + variable + "_stack.pop(); }\n";
        } else if (keyword.matches("first|notfirst")) {
            code = "\n}}\n";
        } else if (keyword.matches("isa|notisa")) {
            String type          = this.current_block_type.pop();
            String variable      = this.current_block_variable.pop();
            String alias         = this.current_block_alias.pop();
            String alias_type    = this.current_block_alias_type.pop();
            String accessory[][] = this.current_block_accessory.pop();
            int    index         = this.current_block_index.pop();

            code = "\n}\n}\n";
        } else if (keyword.matches("indent")) {
            code = " gom_execution_context.popIndent(context);\n";
        } else {
            InvalidTemplateSyntax.createAndThrow(context, "Invalid java preprocessor keyword '%(keyword)'", "keyword", keyword);
            throw (InvalidTemplateSyntax) null;
        }
        processCode(context, code, false);
        return current_node;
    }
}
