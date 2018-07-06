package com.sphenon.engines.generator.operations;

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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.monitoring.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.factories.*;
import com.sphenon.basics.locating.returncodes.*;
import com.sphenon.basics.validation.returncodes.ValidationFailure;
import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.basics.data.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.gom.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.returncodes.*;

import com.sphenon.ui.annotations.*;

import java.util.Vector;

import java.io.Writer;

@UIName("Generate")
public class Operation_Generate implements Operation {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static protected boolean initialised;

    public Operation_Generate (CallContext context) {
        if ( ! initialised) {
            notification_level = NotificationLocationContext.getLevel(context, "com.sphenon.engines.generator.operations.Operation_Generate");
            initialised = true;
        }
    }

    protected String generator_class;

    @UIAttribute(Name="Template Class")
    public String getGeneratorClass (CallContext context) {
        return this.generator_class;
    }

    public void setGeneratorClass (CallContext context, String generator_class) {
        this.generator_class = generator_class;
        if (this.generator_class != null && this.generator_class.length() == 0) {
            this.generator_class = null;
        }
    }

    protected String text_locator;

    @UIAttribute(Name="Text Locator")
    public String getTextLocator (CallContext context) {
        return this.text_locator;
    }

    public String defaultTextLocator (CallContext context) {
        return null;
    }

    public void setTextLocator (CallContext context, String text_locator) {
        this.text_locator = text_locator;
    }

    protected GeneratorOutputHandler generator_output_handler;

    public GeneratorOutputHandler getGeneratorOutputHandler (CallContext context) {
        return this.generator_output_handler;
    }

    public GeneratorOutputHandler defaultGeneratorOutputHandler (CallContext context) {
        return null;
    }

    public void setGeneratorOutputHandler (CallContext context, GeneratorOutputHandler generator_output_handler) {
        this.generator_output_handler = generator_output_handler;
    }

    protected Vector_Object_long_ arguments;

    public Vector_Object_long_ getArguments (CallContext context) {
        return this.arguments;
    }

    public Vector_Object_long_ defaultArguments (CallContext context) {
        return null;
    }

    public void setArguments (CallContext context, Vector_Object_long_ arguments) {
        this.arguments = arguments;
    }

    protected boolean interactive;

    public boolean getInteractive (CallContext context) {
        return this.interactive;
    }

    public boolean defaultInteractive (CallContext context) {
        return false;
    }

    public void setInteractive (CallContext context, boolean interactive) {
        this.interactive = interactive;
    }

    protected TemplateInstanceRegistry template_instance_registry;

    public TemplateInstanceRegistry getTemplateInstanceRegistry (CallContext context) {
        return this.template_instance_registry;
    }

    public TemplateInstanceRegistry defaultTemplateInstanceRegistry (CallContext context) {
        return null;
    }

    public void setTemplateInstanceRegistry (CallContext context, TemplateInstanceRegistry template_instance_registry) {
        this.template_instance_registry = template_instance_registry;
    }

    public Execution execute (CallContext context) {
        return execute(context, null);
    }

    public Execution execute (CallContext context, com.sphenon.basics.data.DataSink<Execution> execution_sink) {
        Execution execution = null;
        Object[] o_args = new Object[arguments == null ? 0 : (int)arguments.getSize(context)];
        if (arguments != null) {
            for (int i=0; i<arguments.getSize(context); i++) {
                o_args[i] = arguments.tryGet(context, i);
            }
        }
        try {
            execute(context, generator_class, text_locator, generator_output_handler, interactive, template_instance_registry, o_args);
            if (generator_output_handler instanceof GeneratorOutputToString) {
                GeneratorOutputToString gots = (GeneratorOutputToString) generator_output_handler;
                Vector<String> cns = gots.getChannelNames(context);
                if (cns == null) {
                    gots.getResult(context);
                } else {
                    for (String cn : cns) {
                        gots.getResult(context, cn);
                    }
                }
            }
            execution = Class_Execution.createExecutionSuccess(context);
        } catch (Throwable t) {
            execution = Class_Execution.createExecutionFailure(context, t);
        }

        if (execution_sink != null) { execution_sink.set(context, execution); }
        return execution;
    }

    static protected Object convertValue(CallContext context, FormalArgument fa, String value) throws ValidationFailure, InvalidLocator {
        String tn = fa.getTypeName(context);
        boolean is_string       = tn.matches("(java.lang.)?String");
        boolean is_string_array = tn.matches("(java.lang.)?String\\[\\]");
        boolean is_boolean      = tn.matches("(java.lang.)?Boolean");
        boolean is_char         = tn.matches("(java.lang.)?Char");
        boolean is_int          = tn.matches("(java.lang.)?Integer");
        boolean is_long         = tn.matches("(java.lang.)?Long");
        boolean is_float        = tn.matches("(java.lang.)?Float");
        boolean is_double       = tn.matches("(java.lang.)?Double");

        if (is_string) {
            return value;
        } else if (is_string_array) {
            if (value == null || value.isEmpty()) {
                return null;
            }
            return value.split(",");
        } else if (is_boolean) {
            return Boolean.parseBoolean(value);
        } else if (is_char) {
            return value.charAt(0);
        } else if (is_int) {
            return Integer.parseInt(value);
        } else if (is_long) {
            return Long.parseLong(value);
        } else if (is_float) {
            return Float.parseFloat(value);
        } else if (is_double) {
            return Double.parseDouble(value);
        } else {
            return Factory_Locator.construct(context, value).retrieveTarget(context);
        }
    }

    static protected void execute (CallContext context, String generator_class, String text_locator, GeneratorOutputHandler generator_output_handler, GOMExecutionContext gom_execution_context, boolean interactive, TemplateInstanceRegistry template_instance_registry, Object... o_args) throws NoSuchTemplate {
        Generator generator = GeneratorRegistry.get(context).getGenerator(context, generator_class, text_locator);

        Vector<FormalArgument> signature = generator.getSignature(context);

        if (signature != null && interactive) {

            Object[] new_args = new Object[signature.size()];

            for (int idx=0; idx < signature.size(); idx++) {
                FormalArgument fa  = signature.get(idx);
                String tn = fa.getTypeName(context);
                boolean is_string       = tn.matches("(java.lang.)?String");
                boolean is_string_array = tn.matches("(java.lang.)?String\\[\\]");
                boolean is_boolean      = tn.matches("(java.lang.)?Boolean");
                boolean is_char         = tn.matches("(java.lang.)?Char");
                boolean is_int          = tn.matches("(java.lang.)?Integer");
                boolean is_long         = tn.matches("(java.lang.)?Long");
                boolean is_float        = tn.matches("(java.lang.)?Float");
                boolean is_double       = tn.matches("(java.lang.)?Double");
                Object default_value_o = fa.getDefaultValue(context);
                String default_value = (default_value_o == null ? null : default_value_o.toString());
                String default_value_explanation = (default_value == null ? "" : ", default: " + default_value);
                String typedesc =   is_string       ? "String"
                                  : is_string_array ? "String[]"
                                  : is_boolean      ? "Boolean"
                                  : is_char         ? "Character"
                                  : is_int          ? "Integer"
                                  : is_long         ? "Long"
                                  : is_float        ? "Float"
                                  : is_double       ? "Double"
                                  :                   "Locator";

                boolean provided = (idx < o_args.length  ? true : false);

                String input;

                if (provided) {
                    try {
                        new_args[idx] = convertValue(context, fa, (String) o_args[idx]);
                    } catch (ValidationFailure vf) {
                        CustomaryContext.create((Context)context).throwPreConditionViolation(context, vf, "Invalid argument '%(name)' to generator, expected '%(expected)'", "name", fa.getArgumentName(context), "expected", typedesc + default_value_explanation);
                        throw (ExceptionPreConditionViolation) null; // compiler insists
                    } catch (InvalidLocator il) {
                        CustomaryContext.create((Context)context).throwPreConditionViolation(context, il, "Invalid argument '%(name)' to generator, expected '%(expected)'", "name", fa.getArgumentName(context), "expected", typedesc + default_value_explanation);
                        throw (ExceptionPreConditionViolation) null; // compiler insists
                    }
                } else {
                    System.err.print(fa.getArgumentName(context) + " [" + typedesc + default_value_explanation + "]: ");

                    while (true) {
                        try {
                            input = (new java.io.BufferedReader(new java.io.InputStreamReader(System.in))).readLine();
                        } catch (java.io.IOException ioe) {
                            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Could not read input");
                            throw (ExceptionEnvironmentFailure) null; // compiler insists
                        }
                        if ((input == null || input.length() == 0) && default_value != null) {
                            input = default_value;
                        }

                        try {
                            new_args[idx] = convertValue(context, fa, input);
                            break;
                        } catch (ValidationFailure vf) {
                            Dumper.dump(context, "Invalid input", vf);
                            continue;
                        } catch (InvalidLocator il) {
                            Dumper.dump(context, "Invalid input", il);
                            continue;
                        }
                    }
                }
            }
            o_args = new_args;
        }

        String instance_name = generator.getInstanceName(context, o_args);

        if (generator_output_handler == null) {
            generator_output_handler = new GeneratorOutputToFile(context, instance_name);
        }

        MetaDataRequirements mdr = generator.getMetaData(context, MetaDataRequirements.class, true, o_args);
        Vector<TemplateInstance> requirements  = mdr == null ? null : mdr.getRequirements(context);

        if (requirements != null && template_instance_registry != null) {
            for (TemplateInstance ti : requirements) {
                template_instance_registry.addTemplateInstance(context, ti);
            }
        }

        if ((notification_level & Notifier.CHECKPOINT) != 0) {

            if (signature != null) {
                for (FormalArgument fa : signature) {
                    NotificationContext.sendCheckpoint(context, "FormalArgument: '%(name)' - '%(type)'", "name", fa.getArgumentName(context), "type", fa.getTypeName(context));
                }
            }

            if (instance_name != null) {
                NotificationContext.sendCheckpoint(context, "InstanceName: '%(name)'", "name", instance_name);
            }

            if (requirements != null) {
                for (TemplateInstance ti : requirements) {
                    NotificationContext.sendCheckpoint(context, "Requirement: '%(instance)'", "instance", ti);
                }
            }
        }

        ((GeneratorInternal) generator).generate(context, generator_output_handler, gom_execution_context, o_args);
    }

    static public void execute (CallContext context, String generator_class, String text_locator, GeneratorOutputHandler generator_output_handler, boolean interactive, TemplateInstanceRegistry template_instance_registry, Object... o_args) throws NoSuchTemplate {
        execute(context, generator_class, text_locator, generator_output_handler, null, interactive, template_instance_registry, o_args);
    }

    static public void execute (CallContext context, String generator_class, GeneratorOutputHandler generator_output_handler, Object... o_args) {
        execute(context, generator_class, generator_output_handler, (GOMExecutionContext) null, o_args);
    }

    static public void execute (CallContext context, String generator_class, GeneratorOutputHandler generator_output_handler, GOMExecutionContext gom_execution_context, Object... o_args) {
        execute (context, generator_class, null, generator_output_handler, gom_execution_context, o_args);
    }

    static public void execute (CallContext context, String generator_class, String text_locator, GeneratorOutputHandler generator_output_handler, GOMExecutionContext gom_execution_context, Object... o_args) {
        try {
            execute(context, generator_class, text_locator, generator_output_handler, gom_execution_context, false, null, o_args);
        } catch (NoSuchTemplate nst) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, nst, "Generator invocation failed");
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    static public void execute (CallContext context, String generator_class, Writer writer, Object... o_args) {
        GeneratorOutputToWriter gotw = new GeneratorOutputToWriter(context, writer, false);
        execute(context, generator_class, gotw, o_args); 
    }

    static public void execute (CallContext context, String generator_class, String text_locator, Writer writer, Object... o_args) {
        GeneratorOutputToWriter gotw = new GeneratorOutputToWriter(context, writer, false);
        execute(context, generator_class, text_locator, gotw, (GOMExecutionContext) null, o_args);
    }

    static public void execute (CallContext context, String generator_class, String text_locator, Object... o_args) {
        GeneratorOutputToFile gotf = new GeneratorOutputToFile(context, text_locator);
        execute(context, generator_class, gotf, o_args); 
    }

    static public void execute (CallContext context, String generator_class, Locator locator, Object... o_args) {
        GeneratorOutputToFile gotf = new GeneratorOutputToFile(context, locator);
        execute(context, generator_class, gotf, o_args); 
    }

    static public void execute (CallContext context, String generator_class, Location location, Object... o_args) {
        GeneratorOutputToFile gotf = new GeneratorOutputToFile(context, location);
        execute(context, generator_class, gotf, o_args); 
    }

    public static void main(String[] args) {

        Context context = RootContext.getRootContext ();
        Configuration.checkCommandLineArgs(args);
        Configuration.initialise(context);
        CustomaryContext cc = CustomaryContext.create(context);

        Operation_Generate og = new Operation_Generate(context);

        int a = 0;
        for (; a<args.length; a++) {
            if (args[a].matches("^-.*") == false) { break; }
        }
        
        if (args.length - a < 1) {
            System.err.println("Usage:\n");
            System.err.println("\n");
            System.err.println("    java [java-options] com.sphenon.engines.generator.operations.Operation_Generate [options] <templatelocator> [outputlocator] [arguments]\n");
            System.err.println("\n");
            System.err.println("  java-options              e.g. classpath\n");
            System.err.println("  options                   --configuration-name, --configuration-variant, --property\n");
            System.err.println("  templatelocator           just a filename, or a textual locator (ctn)\n");
            System.err.println("  outputlocator             just a filename, or a textual locator (ctn)\n");
            System.err.println("                            if empty, the templatelocator is considered as a\n");
            System.err.println("                            dynamic string to describe the output locator\n");
            System.err.println("  arguments                 passed to template\n");
            System.exit(1);
        }
            
        String templatelocator = args[a++];
        String outputlocator   = ((args.length - a > 0) ? args[a++] : null);
        if (outputlocator != null && outputlocator.length() == 0) { outputlocator = null; }

//         if (outputlocator == null || outputlocator.length() == 0) {
//             DynamicString ds= new DynamicString(context, "genoutfile:" + templatelocator.replaceFirst("\\.template$", ""));
//             outputlocator = ds.get(context);
//         }

        System.err.println("Template   : " + templatelocator);
        System.err.println("Output     : " + (outputlocator == null ? "-using instance name-" : outputlocator));
        
        og.setTextLocator(context, templatelocator);
        og.setGeneratorClass(context, null);
        og.setInteractive(context, true);

        if (outputlocator != null) {
            if (outputlocator.matches("---")) {
                og.setGeneratorOutputHandler(context, new com.sphenon.engines.generator.classes.GeneratorOutputToString(context));
            } else {                
                og.setGeneratorOutputHandler(context, new GeneratorOutputToFile(context, outputlocator));
            }
        }

        Vector_Object_long_ arguments = Factory_Vector_Object_long_.construct(context);
        for (; a<args.length; a++) {
            arguments.append(context, args[a]);
        }
        og.setArguments (context, arguments);

        Execution execution = og.execute (context);

        Dumper.dump(context, "Result: ", execution);
    }

}
