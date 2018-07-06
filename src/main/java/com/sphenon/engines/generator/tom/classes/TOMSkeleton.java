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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.javacode.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.BufferedWriter;
import java.io.IOException;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TOMSkeleton extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMSkeleton(CallContext context, TOMNode parent) {
        super(context, parent);
    }

    // Attributes ---------------------------------------------------------------------

    protected int root_gom_id;

    protected Signature signature;

    public Signature getSignature (CallContext context) {
        if (this.signature == null) {
            try {
                this.signature = new Class_Signature(context, null, this.getRootNode(context).getTemplate(context));
            } catch (InvalidTemplateSyntax its) {
                // should not occur
            }
        }
        return this.signature;
    }

    public void setSignature (CallContext context, Signature signature) {
        this.signature = signature;
    }

    protected String base;

    public String getBase (CallContext context) {
        return this.base;
    }

    public void setBase (CallContext context, String base) {
        this.base = base;
    }

    protected String interfaces;

    public String getInterfaces (CallContext context) {
        return this.interfaces;
    }

    public void setInterfaces (CallContext context, String interfaces) {
        this.interfaces = interfaces;
    }

    protected String polymorphic;

    public String getPolymorphic (CallContext context) {
        return this.polymorphic;
    }

    public void setPolymorphic (CallContext context, String polymorphic) {
        this.polymorphic = polymorphic;
    }

    protected boolean override;

    public boolean getOverride (CallContext context) {
        return this.override;
    }

    public void setOverride (CallContext context, boolean override) {
        this.override = override;
    }

    protected String instance_name_expression;

    public String getInstanceNameExpression (CallContext context) {
        return this.instance_name_expression;
    }

    public void setInstanceNameExpression (CallContext context, String instance_name_expression) {
        this.instance_name_expression = instance_name_expression;
    }

    protected String last_data_modification;

    public String getLastDataModification (CallContext context) {
        return this.last_data_modification;
    }

    public void setLastDataModification (CallContext context, String last_data_modification) {
        this.last_data_modification = last_data_modification;
    }

    protected int name_expression_offset;

    public int getNameExpressionOffset (CallContext context) {
        return this.name_expression_offset;
    }

    public void setNameExpressionOffset (CallContext context, int name_expression_offset) {
        this.name_expression_offset = name_expression_offset;
    }

    static protected class Requirement {
        public Requirement(String template_name, String arguments) {
            this.template_name = template_name;
            this.arguments = arguments;
        }
        public String template_name;
        public String arguments;
    };

    protected Vector<Requirement> requirements;

    public void addRequirement(CallContext context, String template_name, String arguments) {
        if (this.requirements == null) {
            this.requirements = new Vector<Requirement>();
        }
        this.requirements.add(new Requirement(template_name, arguments));
    }

    protected Vector<String> doclets;

    public void addDoclet(CallContext context, String doclet) {
        if (this.doclets == null) {
            this.doclets = new Vector<String>();
        }
        this.doclets.add(doclet);
    }

    protected Vector<String> worklets;

    public void addWorklet(CallContext context, String worklet) {
        if (this.worklets == null) {
            this.worklets = new Vector<String>();
        }
        this.worklets.add(worklet);
    }

    // Internal -----------------------------------------------------------------------

    protected Map<String, Integer> sub_templates;

    public void clearSubTemplates(CallContext context) {
        this.sub_templates = null;
    }

    public boolean checkAndInsertSubTemplate(CallContext context, String name) {
        boolean exists = false;
        if (this.sub_templates == null) {
            this.sub_templates = new HashMap<String, Integer>();
            exists = false;
        } else {
            exists = (this.sub_templates.get(name) != null ? true : false);
        }
        if (! exists) { this.sub_templates.put(name, 1); }
        return exists;
    }

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    static protected DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public void createJavaCode(CallContext context, Section section, JavaCodeManager java_code_manager, BufferedWriter current_writer, String indent) {

        if (section == Section.NO_SECTION) {
            String base = this.getBase(context);
            boolean is_root = (base == null || base.length() == 0);
            
            try {
                int aidx = 0;

                Date created = new Date();

                // preparartion run, e.g. allocation of gom_ids
                this.getRootNode(context).resetNextGOMId(context);
                this.root_gom_id = this.getRootNode(context).getNextGOMId(context);
                this.getRootNode(context).pushLocalGOMIndex(context);
                for (TOMNode tom_node : this.getChildNodes(context)) {
                    tom_node.createJavaCode(context, Section.INIT_SECTION, java_code_manager, current_writer, "");
                }
                this.getRootNode(context).popLocalGOMIndex(context);

                // now creating code
                current_writer.append("package " + this.getRootNode(context).getTemplate(context).getPackageName(context) + ";\n");
                current_writer.append("\n");
                current_writer.append("import com.sphenon.basics.context.*;\n");
                current_writer.append("import com.sphenon.basics.context.classes.*;\n");
                current_writer.append("import com.sphenon.basics.debug.RuntimeStep;\n");
                current_writer.append("import com.sphenon.basics.debug.RuntimeStepLevel;\n");
                current_writer.append("import com.sphenon.basics.debug.RuntimeStepLocationContext;\n");
                current_writer.append("import com.sphenon.basics.exception.*;\n");
                current_writer.append("import com.sphenon.basics.notification.*;\n");
                current_writer.append("import com.sphenon.basics.message.*;\n");
                current_writer.append("import com.sphenon.basics.customary.*;\n");
                current_writer.append("import com.sphenon.basics.encoding.*;\n");
                current_writer.append("import com.sphenon.basics.expression.DynamicString;\n");
                current_writer.append("import com.sphenon.basics.data.*;\n");
                current_writer.append("import com.sphenon.basics.doclet.Doclet;\n");
                current_writer.append("import com.sphenon.basics.doclet.classes.Class_Doclet;\n");
                current_writer.append("import com.sphenon.basics.worklet.Worklet;\n");
                current_writer.append("import com.sphenon.basics.worklet.classes.Class_Worklet;\n");
                current_writer.append("import com.sphenon.basics.metadata.*;\n");
                current_writer.append("import com.sphenon.engines.generator.*;\n");
                current_writer.append("import com.sphenon.engines.generator.classes.*;\n");
                current_writer.append("import com.sphenon.engines.generator.gom.*;\n");
                current_writer.append("import com.sphenon.engines.generator.gom.classes.*;\n");
                current_writer.append("import com.sphenon.engines.generator.operations.*;\n");
                current_writer.append("import com.sphenon.engines.generator.tplinst.*;\n");
                current_writer.append("import com.sphenon.engines.generator.returncodes.*;\n");
                current_writer.append("\n");
                current_writer.append("import java.io.IOException;\n");
                current_writer.append("\n");
                for (TOMNode tom_node : this.getChildNodes(context)) {
                    tom_node.createJavaCode(context, Section.IMPORT_SECTION, java_code_manager, current_writer, "");
                }
                current_writer.append("\n");
                current_writer.append("import java.io.Writer;\n");
                current_writer.append("import java.io.FileWriter;\n");
                current_writer.append("\n");
                current_writer.append("@com.sphenon.basics.tracking.annotations.ArtefactHistory(Created=\"" + date_format.format(created) + "\")\n");
                current_writer.append("public class " + this.getRootNode(context).getTemplate(context).getClassName(context)
                                      + (is_root || this.interfaces != null ? " implements " : "")
                                      + (is_root ? "CompiledGenerator, GeneratorInternal" : "")
                                      + (is_root && this.interfaces != null ? ", " : "")
                                      + (this.interfaces != null ? this.interfaces : "")
                                      + (is_root == false ? (" extends " + base) : "")
                                      + " {\n");
                current_writer.append("\n");
                current_writer.append("    static final public Class rtcfg_class = Generator.class;\n");
                current_writer.append("\n");
                current_writer.append("    static protected long runtimestep_level;\n");
                current_writer.append("    static public    long adjustRuntimeStepLevel(long new_level) { long old_level = runtimestep_level; runtimestep_level = new_level; return old_level; }\n");
                current_writer.append("    static public    long getRuntimeStepLevel() { return runtimestep_level; }\n");
                current_writer.append("    static { runtimestep_level = RuntimeStepLocationContext.getLevel(rtcfg_class); };\n");
                current_writer.append("\n");
                current_writer.append("    /* Id ----------------------------------------------------------------------------- */\n");
                current_writer.append("\n");
                current_writer.append("    static public final String generator_id = \"" + this.getRootNode(context).getTemplate(context).getPackageName(context) + "." + this.getRootNode(context).getTemplate(context).getClassName(context) + "\";\n");
                current_writer.append("\n");
                current_writer.append("    public String getId(CallContext context) {\n");
                current_writer.append("        return generator_id;\n");
                current_writer.append("    }\n");
                current_writer.append("\n");
                current_writer.append("    public String getInstanceName (CallContext context, Object... arguments) {\n");
                aidx = 0;
                for (FormalArgument formal_argument : this.getSignature(context).getFormalArguments(context)) {
                    current_writer.append("        if (arguments.length > " + aidx + ") {\n");
                    current_writer.append("            " + formal_argument.getArgumentName(context) + " = (" + formal_argument.getTypeName(context) + ") arguments[" + aidx + "];\n");
                    current_writer.append("        } else {\n");
                    if (formal_argument.getDefaultValueCode(context) != null) {
                        current_writer.append("            " + formal_argument.getArgumentName(context) + " = (" + formal_argument.getTypeName(context) + ") this.getSignature(context).get(" + aidx + ").getDefaultValue(context);\n");
                    } else {
                        current_writer.append("            CustomaryContext.create((Context)context).throwPreConditionViolation(context, \"Generator '%(id)', invoked with too few arguments, expected '%(expected)', got '%(got)'\", \"id\", this.getId(context), \"expected\", " + this.getSignature(context).getFormalArguments(context).length + ", \"got\", arguments.length);\n");
                        current_writer.append("            throw (ExceptionPreConditionViolation) null; // compiler insists\n");
                    }
                    current_writer.append("        };\n");
                    aidx++;
                }
                current_writer.append("        return ");
                if (this.instance_name_expression != null) {
                    current_writer.append(this.instance_name_expression);
                } else {
                    current_writer.append("\"" + this.getRootNode(context).getTemplate(context).getClassName(context));
                    aidx = 0;
                    for (FormalArgument formal_argument : this.getSignature(context).getFormalArguments(context)) {
                        if (aidx >= this.name_expression_offset) {
                            current_writer.append((aidx == name_expression_offset ? "_" : "") + "\" + Encoding.recode(context, (ContextAware.ToString.convert(context, " + formal_argument.getArgumentName(context) + ")), Encoding.UTF8, Encoding.FILENAME) + \"_");
                        }
                        aidx++;
                    }
                    current_writer.append("\"");
                }
                current_writer.append(";\n");
                current_writer.append("    }\n");
                current_writer.append("\n");
                current_writer.append("    /* Signature ---------------------------------------------------------------------- */\n");
                current_writer.append("\n");
                current_writer.append("    static public java.util.Vector<FormalArgument> signature;\n");
                current_writer.append("\n");
                current_writer.append("    static public java.util.Vector<FormalArgument> getSignature_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "(CallContext context) {\n");
                current_writer.append("        if (signature == null) {\n");
                current_writer.append("            signature = new java.util.Vector<FormalArgument>();\n");
                for (FormalArgument formal_argument : this.getSignature(context).getFormalArguments(context)) {
                    current_writer.append("            signature.add(new Class_FormalArgument(context, \"" + formal_argument.getTypeName(context) + "\", \"" + formal_argument.getArgumentName(context) + "\", TypeManager.get(context, " + formal_argument.getTypeName(context).replaceFirst("<.*>$","") + ".class), " + (formal_argument.getDefaultValueCode(context) != null ? formal_argument.getDefaultValueCode(context) : "null") + "));\n");
                }
                current_writer.append("        }\n");
                current_writer.append("        return signature;\n");
                current_writer.append("    }\n");
                current_writer.append("\n");
                current_writer.append("    public java.util.Vector<FormalArgument> getSignature(CallContext context) {\n");
                current_writer.append("        return getSignature_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "(context);\n");
                current_writer.append("    }\n");
                current_writer.append("\n");
                current_writer.append("    /* MetaData ----------------------------------------------------------------------- */\n");
                current_writer.append("\n");
                current_writer.append("    public<T> T getMetaData (CallContext context, Class<T> meta_data_class, boolean instance_level, Object... arguments) {\n");
                int minimal_length = 0;
                {   int i=1;
                    for (FormalArgument formal_argument : this.getSignature(context).getFormalArguments(context)) {
                        if (formal_argument.getDefaultValueCode(context) == null) { minimal_length = i; }
                        i++;
                    }
                }
                current_writer.append("        if (instance_level && arguments.length < " + minimal_length + ") {\n");
                    current_writer.append("            CustomaryContext.create((Context)context).throwPreConditionViolation(context, \"Generator '%(id)', access to meta data with too few arguments, expected '%(expected)', got '%(got)'\", \"id\", this.getId(context), \"expected\", " + minimal_length + ", \"got\", arguments.length);\n");
                    current_writer.append("            throw (ExceptionPreConditionViolation) null; // compiler insists\n");
                current_writer.append("        }\n");
                aidx = 0;
                for (FormalArgument formal_argument : this.getSignature(context).getFormalArguments(context)) {
                    current_writer.append("        if (arguments.length > " + aidx + ") {\n");
                    current_writer.append("            " + formal_argument.getArgumentName(context) + " = (" + formal_argument.getTypeName(context) + ") arguments[" + aidx + "];\n");
                    current_writer.append("        } else {\n");
                    if (formal_argument.getDefaultValueCode(context) != null) {
                        current_writer.append("            " + formal_argument.getArgumentName(context) + " = (" + formal_argument.getTypeName(context) + ") this.getSignature(context).get(" + aidx + ").getDefaultValue(context);\n");
                    } else {
                        current_writer.append("            if (instance_level) {\n");
                        current_writer.append("                CustomaryContext.create((Context)context).throwPreConditionViolation(context, \"Generator '%(id)', invoked with too few arguments, expected '%(expected)', got '%(got)'\", \"id\", this.getId(context), \"expected\", " + this.getSignature(context).getFormalArguments(context).length + ", \"got\", arguments.length);\n");
                        current_writer.append("                throw (ExceptionPreConditionViolation) null; // compiler insists\n");
                        current_writer.append("            }\n");
                    }
                    current_writer.append("        };\n");
                    aidx++;
                }

                // ok, schönere solution wäre:
                // das spezifische metadata thema wird in den sub toms gelöst
                // und es gibt eine neue section META_DATA
                // dazu sollte es jedoch dann mal in TOMSkeleton ein paar universelle
                // Instrumente geben, damit nicht jedesmal das findPreceeding Geraffel gemacht
                // werden muß
                // in der Art: 1.) eine Methode checkFirst(context, TOMNode-Klasse), die _pro_
                // section ermittelt ob ein TOMNode der entsprechenden Klasse das erste mal
                // gecalled wird und 2.) eine Möglichkeit sowas auch für den letzten Node zu
                // machen -- dazu alternativ: a) sowas wie einen post-handler registrieren b)
                // einen META_DATA-post-pass einzu führen c) generell für jede section eine
                // (init,) center und postpass einführen d) pro TOMNode Klasse eine
                // Zentral-Klasse führen die am Skeleton hängt und einmal vor und einmal nach
                // dem pass aufgerufen wird
                
                // ausserdem noch einführen: MetaDataKeyValue

                current_writer.append("        if (MetaDataRequirements.class.isAssignableFrom(meta_data_class)) {\n");
                current_writer.append("            java.util.Vector<TemplateInstance> tis = new java.util.Vector<TemplateInstance>();\n");
                if (this.requirements != null) {
                    for (Requirement req : this.requirements) {
                        current_writer.append("            tis.add(new Class_TemplateInstance(context, \"" + req.template_name + "\", buildArray(" + req.arguments + ")));\n");
                    }
                }
                current_writer.append("            return (T) new MetaDataRequirements(context, tis);\n");
                current_writer.append("        }\n");
                current_writer.append("\n");
                current_writer.append("        if (MetaDataDoclets.class.isAssignableFrom(meta_data_class)) {\n");
                current_writer.append("            java.util.Vector<Doclet> doclets = new java.util.Vector<Doclet>();\n");
                if (this.doclets != null) {
                    for (String doclet : this.doclets) {
                        current_writer.append("            doclets.add(new Class_Doclet(context, \"" + Encoding.recode(context, doclet, Encoding.UTF8, Encoding.JAVA) + "\"));\n");
                    }
                }
                current_writer.append("            return (T) new MetaDataDoclets(context, doclets);\n");
                current_writer.append("        }\n");
                current_writer.append("\n");
                current_writer.append("        if (MetaDataWorklets.class.isAssignableFrom(meta_data_class)) {\n");
                current_writer.append("            java.util.Vector<Worklet> worklets = new java.util.Vector<Worklet>();\n");
                if (this.worklets != null) {
                    for (String worklet : this.worklets) {
                        current_writer.append("            worklets.add(new Class_Worklet(context, \"" + Encoding.recode(context, worklet, Encoding.UTF8, Encoding.JAVA) + "\"));\n");
                    }
                }
                current_writer.append("            return (T) new MetaDataWorklets(context, worklets);\n");
                current_writer.append("        }\n");
                current_writer.append("\n");
                current_writer.append("        return null;\n");
                current_writer.append("    }\n");
                current_writer.append("\n");
                current_writer.append("    public java.util.Date getLastDataModification (CallContext context, Object... arguments) {\n");
                if (this.last_data_modification != null) {
                    aidx = 0;
                    for (FormalArgument formal_argument : this.getSignature(context).getFormalArguments(context)) {
                        current_writer.append("        if (arguments.length > " + aidx + ") {\n");
                        current_writer.append("            " + formal_argument.getArgumentName(context) + " = (" + formal_argument.getTypeName(context) + ") arguments[" + aidx + "];\n");
                        current_writer.append("        } else {\n");
                        if (formal_argument.getDefaultValueCode(context) != null) {
                            current_writer.append("            " + formal_argument.getArgumentName(context) + " = (" + formal_argument.getTypeName(context) + ") this.getSignature(context).get(" + aidx + ").getDefaultValue(context);\n");
                        } else {
                            current_writer.append("            CustomaryContext.create((Context)context).throwPreConditionViolation(context, \"Generator '%(id)', invoked with too few arguments, expected '%(expected)', got '%(got)'\", \"id\", this.getId(context), \"expected\", " + this.getSignature(context).getFormalArguments(context).length + ", \"got\", arguments.length);\n");
                            current_writer.append("            throw (ExceptionPreConditionViolation) null; // compiler insists\n");
                        }
                        current_writer.append("        };\n");
                        aidx++;
                    }
                    current_writer.append("        return ");
                    current_writer.append(this.last_data_modification);
                    current_writer.append(";\n");
                } else {
                    current_writer.append("return null;");
                }
                current_writer.append("    }\n");
                current_writer.append("\n");
                current_writer.append("    /* Initialisation ----------------------------------------------------------------- */\n");
                current_writer.append("\n");
                current_writer.append("    static protected boolean initialised_" + this.getRootNode(context).getTemplate(context).getClassName(context) + ";\n");
                current_writer.append("\n");
                current_writer.append("    public void initialise_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "(CallContext context) {\n");
                current_writer.append("        if (initialised_" + this.getRootNode(context).getTemplate(context).getClassName(context) + " == false) {\n");
                current_writer.append("            synchronized (" + this.getRootNode(context).getTemplate(context).getClassName(context) + ".class) {\n");
                current_writer.append("                if (initialised_" + this.getRootNode(context).getTemplate(context).getClassName(context) + " == false) {\n");
                current_writer.append("                    initialised_" + this.getRootNode(context).getTemplate(context).getClassName(context) + " = true;\n");
                if (GeneratorPackageInitialiser.use_string_cache) {
                    current_writer.append("                    com.sphenon.basics.system.StringCache sc = com.sphenon.basics.system.StringCache.getSingleton(context);\n");
                    current_writer.append("                    sc.checkTimestamp(context, string_cache_timestamp, \"template '" + this.getRootNode(context).getTemplate(context).getClassName(context) + "'\");\n");
                    current_writer.append("                    texts = new String[string_cache_indices.length];\n");
                    current_writer.append("                    int text_i = 0;\n");
                    current_writer.append("                    for (int sci : string_cache_indices) {\n");
                    current_writer.append("                        texts[text_i++] = sc.getText(context, sci);\n");
                    current_writer.append("                    }\n");
                }
                current_writer.append("                    buildGOMTree_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "(context);\n");
                for (TOMNode tom_node : this.getChildNodes(context)) {
                    tom_node.createJavaCode(context, Section.STATIC_INIT_CODE_SECTION, java_code_manager, current_writer, "    ");
                }
                if (polymorphic != null) {
                    current_writer.append("                    this.generator_registry = new GeneratorRegistry_" + polymorphic + "(context, this);\n");
                    current_writer.append("                    try {\n");
                    current_writer.append("                        Template template = GeneratorRegistry.get(context).getTemplate(context, this.getId(context));\n");
                    current_writer.append("                        java.util.Vector<String> package_childs = template.getPackageChilds(context);\n");
                    current_writer.append("                        if (package_childs != null) {;\n");
                    current_writer.append("                            for (String package_child : package_childs) {\n");
                    current_writer.append("                                GeneratorFactory generator_factory = GeneratorRegistry.get(context).getGeneratorFactory(context, template.getPackageName(context) + \".\" + package_child);\n");
                    current_writer.append("                                generator_factory.prepareCode(context);\n");
                    current_writer.append("                            }\n");
                    current_writer.append("                            for (String package_child : package_childs) {\n");
                    current_writer.append("                                GeneratorFactory generator_factory = GeneratorRegistry.get(context).getGeneratorFactory(context, template.getPackageName(context) + \".\" + package_child);\n");
                    current_writer.append("                                com.sphenon.basics.metadata.Type my_type = TypeManager.get(context, " + this.getRootNode(context).getTemplate(context).getClassName(context) + ".class);\n");
                    current_writer.append("                                com.sphenon.basics.metadata.Type sub_type = TypeManager.get(context, generator_factory.getClass(context));\n");
                    current_writer.append("                                if (sub_type.isA(context, my_type)) {\n");
                    current_writer.append("                                    this.generator_registry.registerGenerator(context, generator_factory);\n");
                    current_writer.append("                                }\n");
                    current_writer.append("                            }\n");
                    current_writer.append("                        }\n");
                    current_writer.append("                    } catch (NoSuchTemplate nst) {\n");
                    current_writer.append("                        CustomaryContext.create((Context)context).throwImpossibleState(context, nst, \"During initialisation of generator '%(id)', a template was not found which was previously queried\", \"id\", this.getId(context));\n");
                    current_writer.append("                        throw (ExceptionImpossibleState) null; // compiler insists\n");
                    current_writer.append("                    }\n");
                }
                current_writer.append("                }\n");
                current_writer.append("            }\n");
                current_writer.append("        }\n");
                current_writer.append("    }\n");
                current_writer.append("\n");
                current_writer.append("    public void initialise(CallContext context) {\n");
                if (is_root == false) {
                    current_writer.append("        super.initialise(context);\n");
                }
                current_writer.append("        initialise_" + this.getRootNode(context).getTemplate(context).getClassName(context) + "(context);\n");
                current_writer.append("    }\n");
                current_writer.append("\n");
                current_writer.append("    /* Constructor -------------------------------------------------------------------- */\n");
                current_writer.append("\n");
                current_writer.append("    public " + this.getRootNode(context).getTemplate(context).getClassName(context) + "(CallContext context) {\n");
                if (is_root == false) {
                    current_writer.append("        super(context);\n");
                }
                current_writer.append("    }\n");
                current_writer.append("\n");
                current_writer.append("    /* Attributes --------------------------------------------------------------------- */\n");
                current_writer.append("\n");
                for (TOMNode tom_node : this.getChildNodes(context)) {
                    tom_node.createJavaCode(context, Section.ATTRIBUTE_SECTION, java_code_manager, current_writer, "    ");
                }
                current_writer.append("\n");
                for (FormalArgument formal_argument : this.getSignature(context).getFormalArguments(context)) {
                    current_writer.append("    protected " + formal_argument.getTypeName(context) + " " + formal_argument.getArgumentName(context) + ";\n");
                }
                current_writer.append("\n");
                if (polymorphic != null) {
                    current_writer.append("    /* Polymorphic generator: " + polymorphic + " -------------------------------------------------- */\n");
                    current_writer.append("\n");
                    current_writer.append("    static protected GeneratorRegistry_" + polymorphic + " generator_registry;\n");
                    current_writer.append("\n");
                }
                current_writer.append("    /* Main generator method ---------------------------------------------------------- */\n");
                current_writer.append("\n");
                current_writer.append("    public GeneratorOutputHandler generate (CallContext context, GeneratorOutputHandler output_handler, Object... arguments) {\n");
                current_writer.append("        GeneratorOutputHandler goh;\n");
                current_writer.append("\n");
                current_writer.append("        RuntimeStep runtime_step = null;\n");
                current_writer.append("        if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { context = Context.create(context); runtime_step = RuntimeStep.create((Context) context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, rtcfg_class, \"Generator, template '%(step)'\", \"template\", this.getId(context)); }\n");
                current_writer.append("        try {\n");
                current_writer.append("            goh = generate(context, output_handler, null, arguments);\n");
                current_writer.append("        } catch (Error e) {\n");
                current_writer.append("            if (runtime_step != null) { runtime_step.setFailed(context, \"Generation failed\"); runtime_step = null; }\n");
                current_writer.append("            throw e;\n");
                current_writer.append("        } catch (RuntimeException re) {\n");
                current_writer.append("            if (runtime_step != null) { runtime_step.setFailed(context, \"Generation failed\"); runtime_step = null; }\n");
                current_writer.append("            throw re;\n");
                current_writer.append("        }\n");
                current_writer.append("        if (runtime_step != null) { runtime_step.setCompleted(context, \"Generation succeeded\"); runtime_step = null; }\n");
                current_writer.append("\n");
                current_writer.append("        return goh;\n");
                current_writer.append("    }\n");
                current_writer.append("\n");
                current_writer.append("    public GeneratorOutputHandler generate (CallContext context, GeneratorOutputHandler output_handler, GOMExecutionContext parent_gom_execution_context, Object... arguments) {\n");
                current_writer.append("        this.initialise(context);\n");
                current_writer.append("        \n");
                if (polymorphic != null) {
                    current_writer.append("        GeneratorFactory generator_factory = this.generator_registry.tryGet(context, arguments);\n");
                    current_writer.append("        if (generator_factory != null) {\n");
                    current_writer.append("            return ((GeneratorInternal)(generator_factory.create(context))).do_generate(context, output_handler, parent_gom_execution_context, arguments);\n");
                    current_writer.append("        }\n");
                }
                current_writer.append("        return do_generate(context, output_handler, parent_gom_execution_context, arguments);\n");
                current_writer.append("    }\n");
                current_writer.append("\n");
                current_writer.append("    public GeneratorOutputHandler do_generate (CallContext context, GeneratorOutputHandler output_handler, GOMExecutionContext parent_gom_execution_context, Object... arguments) {\n");
                aidx = 0;
                for (FormalArgument formal_argument : this.getSignature(context).getFormalArguments(context)) {
                    current_writer.append("        if (arguments.length > " + aidx + ") {\n");
                    current_writer.append("            " + formal_argument.getArgumentName(context) + " = (" + formal_argument.getTypeName(context) + ") arguments[" + aidx + "];\n");
                    current_writer.append("        } else {\n");
                    if (formal_argument.getDefaultValueCode(context) != null) {
                        current_writer.append("            " + formal_argument.getArgumentName(context) + " = (" + formal_argument.getTypeName(context) + ") this.getSignature(context).get(" + aidx + ").getDefaultValue(context);\n");
                    } else {
                        current_writer.append("            CustomaryContext.create((Context)context).throwPreConditionViolation(context, \"Generator '%(id)', invoked with too few arguments, expected '%(expected)', got '%(got)'\", \"id\", this.getId(context), \"expected\", " + this.getSignature(context).getFormalArguments(context).length + ", \"got\", arguments.length);\n");
                        current_writer.append("            throw (ExceptionPreConditionViolation) null; // compiler insists\n");
                    }
                    current_writer.append("        };\n");
                    aidx++;
                }
                current_writer.append("        \n");
                current_writer.append("        this.initialise(context);\n");
                current_writer.append("        \n");
                current_writer.append("        final GOMExecutionContext gom_execution_context = new Class_GOMExecutionContext(context, this, parent_gom_execution_context);\n");
                current_writer.append("        GOMProcessor gom_processor = new Class_GOMProcessor(context, gom_root_" + this.getRootNode(context).getTemplate(context).getClassName(context) + ", gom_execution_context, output_handler, arguments);\n");
                current_writer.append("        gom_processor.process(context);\n");
                int loopcount = 0;
                for (TOMNode tom_node : this.getChildNodes(context)) {
                    tom_node.createJavaCode(context, Section.GENERATOR_CODE_SECTION, java_code_manager, current_writer, "        ");
                    loopcount++;
                }
                if (is_root || override) {
                    current_writer.append("        return output_handler;\n");
                } else {
                    current_writer.append("        return super.do_generate(context, output_handler, parent_gom_execution_context, arguments);\n");
                }
                current_writer.append("    }\n");
                current_writer.append("\n");
                current_writer.append("    /* Methods ------------------------------------------------------------------------ */\n");
                current_writer.append("\n");
                current_writer.append("    static protected GOMNode gom_root_" + this.getRootNode(context).getTemplate(context).getClassName(context) + ";\n");
                current_writer.append("\n");
                current_writer.append("    static public void buildGOMTree_" + this.getRootNode(context).getTemplate(context).getClassName(context) + " (CallContext context) {\n");
                current_writer.append("        gom_root_" + this.getRootNode(context).getTemplate(context).getClassName(context) + " = new GOMDefaultWriter(context, " + this.root_gom_id + ");\n");
                current_writer.append("        GOMNode gom_node = gom_root_" + this.getRootNode(context).getTemplate(context).getClassName(context) + ";\n");
                current_writer.append("        GOMNode current_gom_node = null;\n");
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
                current_writer.append("        gom_root_" + this.getRootNode(context).getTemplate(context).getClassName(context) + ".dumpGOMTree(context);\n");
                current_writer.append("    }\n");
                current_writer.append("\n");
                this.clearSubTemplates(context);
                for (TOMNode tom_node : this.getChildNodes(context)) {
                    tom_node.createJavaCode(context, Section.METHOD_SECTION, java_code_manager, current_writer, "    ");
                }
                current_writer.append("\n");
                current_writer.append("    protected Object[] buildArray(Object... objects) {\n");
                current_writer.append("        return objects;\n");
                current_writer.append("    }\n");
                current_writer.append("\n");
                current_writer.append("    /* Data --------------------------------------------------------------------------- */\n");
                current_writer.append("\n");
                if (GeneratorPackageInitialiser.use_string_cache) {
                    current_writer.append("    static protected String[] texts;\n");
                    current_writer.append("    static protected String string_cache_timestamp = \"" + StringCache.getSingleton(context).getTimestamp(context) + "\";\n");
                    current_writer.append("    static protected int[] string_cache_indices = { ");
                    List<Integer> scis = this.getRootNode(context).getStringCacheIndices(context);
                    if (scis != null) {
                        boolean first_sci = true;
                        for (int sci : scis) {
                            if (first_sci) { first_sci = false; } else { current_writer.append(", "); }
                            current_writer.append((new Integer(sci)).toString());
                        }
                    }
                    current_writer.append(" };\n");
                }
                for (TOMNode tom_node : this.getChildNodes(context)) {
                    tom_node.createJavaCode(context, Section.DATA_SECTION, java_code_manager, current_writer, "    ");
                }
                current_writer.append("\n");
                current_writer.append("}\n");
            } catch (IOException ioe) {
                CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Could not write to java source file/stream (for template '%(template)')", "template", this.getRootNode(context).getTemplate(context).getFullClassName(context));
                throw (ExceptionEnvironmentFailure) null; // compiler insists
            }
        }

    }
}
