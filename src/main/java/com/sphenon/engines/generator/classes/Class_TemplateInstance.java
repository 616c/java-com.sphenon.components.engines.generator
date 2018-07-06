package com.sphenon.engines.generator.classes;

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
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;

import com.sphenon.basics.configuration.annotations.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.returncodes.*;

import java.util.Vector;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;

public class Class_TemplateInstance implements TemplateInstance, MessageAware {

    // Configuration ------------------------------------------------------------------

    @Configuration public interface Config {
        String getTemplateInstancePath(CallContext context);
        void   setTemplateInstancePath(CallContext context, String template_instance_path);
    }
    static public Config config = Configuration_Class_TemplateInstance.get(RootContext.getInitialisationContext());

    // Construction -------------------------------------------------------------------

    public Class_TemplateInstance (CallContext context, String template_instance_expression) {
        if (template_instance_expression == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Argument template_instance_expression is null");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        this.setTemplateInstanceExpression(context, template_instance_expression);
    }

    public Class_TemplateInstance (CallContext context, String template_name, Object[] arguments) {
        if (template_name == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Argument template_name is null");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        if (arguments == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Argument arguments is null");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        this.template_name                  = template_name;
        this.arguments                      = arguments;
        this.is_generator_template_instance = true;
        this.template_instance_expression   = null;
    }

    // Attributes ---------------------------------------------------------------------

    protected String template_instance_expression;

    public String getTemplateInstanceExpression (CallContext context) {
        return this.getTemplateInstanceExpression(context, false);
    }

    public String getTemplateInstanceExpression (CallContext context, boolean unicode) {
        if (this.template_instance_expression == null) {
            this.template_instance_expression = this.getTemplateName(context) + "<-";
            boolean first = true;
            String gap = "";
            for (Object argument : arguments) {
                if (argument == null) { gap += ","; continue; }
                this.template_instance_expression += (first ? "" : ",") + gap + (argument instanceof ContextAware ? ((ContextAware)argument).toString(context) : argument.toString());
                first = false;
                gap = "";
            }
            this.template_instance_expression += "->";
        }
        return (unicode ? this.template_instance_expression.replaceAll("<-", "≤").replaceAll("->", "≥") : this.template_instance_expression);
    }

    protected boolean is_generator_template_instance;

    public boolean getIsGeneratorTemplateInstance(CallContext context) {
        return is_generator_template_instance;
    }

    protected boolean is_java_generics_instance;

    public boolean getIsJavaGenericsInstance(CallContext context) {
        return is_java_generics_instance;
    }

    static protected RegularExpression template_sep = new RegularExpression("((?:->)|(?:<(?:-?))|>|,)");

    public void setTemplateInstanceExpression (CallContext context, String template_instance_expression) {
        this.template_instance_expression = template_instance_expression.replaceAll("≤", "<-").replaceAll("≥", "->");

        this.is_generator_template_instance = false;
        this.is_java_generics_instance = false;

        Matcher m = template_sep.getMatcher(context, this.template_instance_expression);
        boolean got_name = false;
        Vector<Object> oargs = new Vector<Object>();
        Stack<Boolean> nesting = new Stack<Boolean>();

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String sep  = m.group(1);

            m.appendReplacement(sb, "");

            if (got_name == false) {
                if (sep.equals("<-")) {
                    this.template_name = sb.toString();
                    sb.setLength(0);
                    this.is_generator_template_instance = true;
                    got_name = true;
                    continue;
                }
                if (sep.equals("<")) {
                    this.template_name = sb.toString();
                    sb.setLength(0);
                    this.is_java_generics_instance = true;
                    got_name = true;
                    continue;
                }
            } else {
                if (    nesting.empty()
                     && (    (sep.equals("->") && this.is_generator_template_instance)
                          || (sep.equals(">")  && this.is_java_generics_instance)
                          ||  sep.equals(",")
                        )
                   ) {
                    String sub_expression = sb.toString();
                    Class_TemplateInstance ti = new Class_TemplateInstance(context, sub_expression);
                    sb.setLength(0);
                    oargs.add(ti.getIsGeneratorTemplateInstance(context) ? ti : sub_expression);
                    continue;
                }
                if (sep.equals("<-")) {
                    sb.append(sep);
                    nesting.push(true);
                    continue;
                }
                if (sep.equals("<")) {
                    sb.append(sep);
                    nesting.push(true);
                    continue;
                }
                if (sep.equals("->") && nesting.empty() == false && nesting.peek() == true) {
                    sb.append(sep);
                    nesting.pop();
                    continue;
                }
                if (sep.equals(">") && nesting.empty() == false && nesting.peek() == true) {
                    sb.append(sep);
                    nesting.pop();
                    continue;
                }
                if (sep.equals(",") && nesting.empty() == false) {
                    sb.append(sep);
                    continue;
                }
            }
            // ERROR HANDLING
        }
        sb.setLength(0);
        m.appendTail(sb);

        if (sb.length() != 0) {
            if (    this.is_generator_template_instance == true
                 || this.is_java_generics_instance == true
               ) {
                // ERROR HANDLING
            }
            this.template_name = sb.toString();
        }

        this.arguments = new Object[oargs.size()];
        int index = 0;
        for (Object oarg : oargs) {
            this.arguments[index++] = oarg;
        }
    }

    protected String template_name;

    public String getTemplateName (CallContext context) {
        return this.template_name;
    }

    public void setTemplateName (CallContext context, String template_name) {
        this.template_name = template_name;
        this.is_generator_template_instance = true;
        this.template_instance_expression   = null;
    }

    protected Object[] arguments;

    public Object[] getArguments (CallContext context) {
        return this.arguments;
    }

    public void setArguments (CallContext context, Object[] arguments) {
        this.arguments = arguments;
        this.is_generator_template_instance = true;
        this.template_instance_expression   = null;
    }

    public MessageText toMessageText(CallContext context) {
        return MessageText.create(context, "'%(name)'<'%(arguments)'>", "name", this.template_name, "arguments", this.arguments);
    }

    // Operations ---------------------------------------------------------------------

    protected String concatPathes(CallContext context, String s1, String s2) {
        return   (s1 == null ? "" : s1)
               + ((s1 != null && s1.length() > 0 && /* s1.charAt(s1.length()-1) != '/' && */ s2 != null && s2.length() > 0 && s2.charAt(0) != '/') ? "/" : "")
               + (s2 == null ? "" : s2);
    }

    public void create(CallContext context, String package_name, Vector<String> imports, JavaTemplateTraits traits, boolean recursive, Map<String,TemplateInstance> already_processed, TIFilter filter, boolean keep_unmodified_files) {

        if (already_processed != null && already_processed.get(this.getTemplateInstanceExpression(context)) != null) {
            // already processed
            return;
        }

        if (filter.include(context, this) == false) {
            // do not process in this context
            return;
        }

        Generator generator;
        try {
            generator = GeneratorRegistry.get(context).getGenerator(context, this.getTemplateName(context));
        } catch (NoSuchTemplate nst) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, nst, "Cannot instantiate template instance, template not found");
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        Object[] gen_args = new Object[this.getArguments(context).length + 3];

        int index = 0;
        gen_args[index++] = package_name;
        gen_args[index++] = imports;
        gen_args[index++] = traits;

        for (Object o : this.getArguments(context)) {
            gen_args[index++] = o;
        }

        String tip = config.getTemplateInstancePath(context);
        String pp  = package_name.replace(".","/");
        String in  = generator.getInstanceName(context, gen_args);

        String filename = concatPathes(context, concatPathes(context, tip, pp), in + ".java");
        
        GeneratorOutputToFile gotf = new GeneratorOutputToFile(context, filename);
        gotf.setKeepUnmodifiedFiles(context, keep_unmodified_files);


        generator.generate(context, gotf, gen_args);

        if (recursive) {
            MetaDataRequirements mdr = generator.getMetaData(context, MetaDataRequirements.class, true, gen_args);
            Vector<TemplateInstance> requirements  = mdr == null ? null : mdr.getRequirements(context);

            if (requirements != null) {
                for (TemplateInstance ti : requirements) {
                    ti.create(context, package_name, imports, traits, recursive, already_processed, filter, keep_unmodified_files);
                }
            }
        }
    }
}
