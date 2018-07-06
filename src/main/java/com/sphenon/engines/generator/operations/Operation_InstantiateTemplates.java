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
import com.sphenon.basics.expression.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.monitoring.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.factories.*;
import com.sphenon.basics.locating.returncodes.*;
import com.sphenon.basics.locating.tplinst.*;
import com.sphenon.basics.validation.returncodes.ValidationFailure;
import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.basics.data.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.returncodes.*;

import java.util.Vector;
import java.util.HashMap;

public class Operation_InstantiateTemplates implements Operation, Dumpable {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static protected boolean initialised;

    public Operation_InstantiateTemplates (CallContext context) {
        if ( ! initialised) {
            notification_level = NotificationLocationContext.getLevel(context, "com.sphenon.engines.generator.operations.Operation_InstantiateTemplates");
            initialised = true;
        }
    }

    protected Vector_String_long_ template_instance_expressions;

    public Vector_String_long_ getTemplateInstanceExpressions (CallContext context) {
        return this.template_instance_expressions;
    }

    public void setTemplateInstanceExpressions (CallContext context, Vector_String_long_ template_instance_expressions) {
        this.template_instance_expressions = template_instance_expressions;
    }

    protected Location template_instance_location;

    public Location getTemplateInstanceLocation (CallContext context) {
        return this.template_instance_location;
    }

    public void setTemplateInstanceLocation (CallContext context, Location template_instance_location) {
        this.template_instance_location = template_instance_location;
    }

    protected Vector_Location_long_ template_locations;

    public Vector_Location_long_ getTemplateLocations (CallContext context) {
        return this.template_locations;
    }

    public void setTemplateLocations (CallContext context, Vector_Location_long_ template_locations) {
        this.template_locations = template_locations;
    }

    protected String template_package_path;

    public String getTemplatePackagePath (CallContext context) {
        return this.template_package_path;
    }

    public void setTemplatePackagePath (CallContext context, String template_package_path) {
        this.template_package_path = template_package_path;
    }

    protected String package_name;

    public String getPackageName (CallContext context) {
        return this.package_name;
    }

    public void setPackageName (CallContext context, String package_name) {
        this.package_name = package_name;
    }

    protected Vector_String_long_ imports;

    public Vector_String_long_ getImports (CallContext context) {
        return this.imports;
    }

    public void setImports (CallContext context, Vector_String_long_ imports) {
        this.imports = imports;
    }

    protected boolean recursive;

    public boolean getRecursive (CallContext context) {
        return this.recursive;
    }

    public void setRecursive (CallContext context, boolean recursive) {
        this.recursive = recursive;
    }

    protected JavaTemplateTraits traits;

    public JavaTemplateTraits getTraits (CallContext context) {
        return this.traits;
    }

    public JavaTemplateTraits defaultTraits (CallContext context) {
        return new JavaTemplateTraits(context);
    }

    public void setTraits (CallContext context, JavaTemplateTraits traits) {
        this.traits = traits;
    }

    protected boolean include_java_library_types;

    public boolean getIncludeJavaLibraryTypes (CallContext context) {
        return this.include_java_library_types;
    }

    public boolean defaultIncludeJavaLibraryTypes (CallContext context) {
        return false;
    }

    public void setIncludeJavaLibraryTypes (CallContext context, boolean include_java_library_types) {
        this.include_java_library_types = include_java_library_types;
    }
    
    protected boolean keep_unmodified_files;

    public boolean getKeepUnmodifiedFiles (CallContext context) {
        return this.keep_unmodified_files;
    }

    public boolean defaultKeepUnmodifiedFiles (CallContext context) {
        return false;
    }

    public void setKeepUnmodifiedFiles (CallContext context, boolean keep_unmodified_files) {
        this.keep_unmodified_files = keep_unmodified_files;
    }

    public Execution execute (CallContext context) {
        return execute(context, null);
    }

    public Execution execute (CallContext call_context, com.sphenon.basics.data.DataSink<Execution> execution_sink) {
        Execution execution = null;
        Context context = Context.create(call_context);
        try {

            ConfigurationContext cc = ConfigurationContext.create(context);
            cc.instantiateLocalProperties(context);

            String tip = this.template_instance_location.tryGetTextLocatorValue(context,"ctn://Space/local_host/file_system","File");

            String[] tp = new String[(int) this.template_locations.getSize(context) + 1];
            int i=0;
            for (Location location : this.template_locations.getIterable_Location_(context)) {
                tp[i++] = location.tryGetTextLocatorValue(context,"ctn://Space/local_host/file_system","File");
            }
            tp[i++] = Configuration_Class_Template.get(context).getTemplatePath(context);

            Configuration_Class_TemplateInstance.get(context).setTemplateInstancePath(context, tip);
            Configuration_Class_Template.get(context).setTemplatePath(context, StringUtilities.join(context, tp, ":", false));
            Configuration_Class_Template.get(context).setTemplatePackagePath(context, this.template_package_path);

            HashMap<String,TemplateInstance> already_processed = new HashMap<String,TemplateInstance>();
            TemplateInstance.TIFilter filter = new TemplateInstance.TIFilter(context, this.include_java_library_types);

            Vector<String> impvs;
            if (this.imports instanceof VectorImpl_String_long_) {
                impvs = ((VectorImpl_String_long_) this.imports).getImplementationVector(context);
            } else {
                impvs = new Vector();
                for (String string : this.imports.getIterable_String_(context)) {
                    impvs.add(string);
                }
            }

            for (String template_instance_expression : this.template_instance_expressions.getIterable_String_(context)) {
                Class_TemplateInstance cti = new Class_TemplateInstance(context, template_instance_expression);

                cti.create(context, this.package_name, impvs, this.traits, this.recursive, already_processed, filter, keep_unmodified_files);
            }

            execution = Class_Execution.createExecutionSuccess(context);
        } catch (Throwable t) {
            execution = Class_Execution.createExecutionFailure(context, t);
        }

        if (execution_sink != null) { execution_sink.set(context, execution); }
        return execution;
    }

    public static void main(String[] args) {

        Context context = RootContext.getRootContext ();
        Configuration.checkCommandLineArgs(args);
        Configuration.initialise(context);
        CustomaryContext cc = CustomaryContext.create(context);

        Operation_InstantiateTemplates oit = new Operation_InstantiateTemplates(context);

        int a = 0;
        for (; a<args.length; a++) {
            if (args[a].matches("^-.*") == false) { break; }
        }
        
        if (args.length - a < 1) {
            System.err.println("Usage:\n");
            System.err.println("\n");
            System.err.println("    java [java-options] com.sphenon.engines.generator.operations.Operation_InstantiateTemplate [options] <templatelocator> [outputlocator] [arguments]\n");
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
        System.err.println("Template   : " + templatelocator);

        Execution execution = oit.execute (context);

        Dumper.dump(context, "Result: ", execution);
    }

    public void dump(CallContext context, DumpNode dump_node) {
        DumpNode dn = dump_node.openDump(context, "Operation_InstantiateTtemplates");
        dn.dump(context, "TemplateInstanceLocation    ", getTemplateInstanceLocation(context));
        dn.dump(context, "TemplatePackagePath         ", getTemplatePackagePath(context));
        dn.dump(context, "PackageName                 ", getPackageName(context));
        dn.dump(context, "Recursive                   ", getRecursive(context));

        DumpNode dn2;

        dn2 = dn.openDump(context, "TemplateLocations           ");
        Integer i=0;
        for (Location location : getTemplateLocations(context).getIterable_Location_(context)) {
            dn2.dump(context, i.toString(), location);
            i++;
        }
        dn2.close(context);

        dn2 = dn.openDump(context, "Imports                     ");
        for (String an_import : getImports(context).getIterable_String_(context)) {
            dn2.dump(context, an_import);
        }
        dn2.close(context);

        dn2 = dn.openDump(context, "TemplateInstanceExpressions ");
        for (String tiexp : getTemplateInstanceExpressions(context).getIterable_String_(context)) {
            dn2.dump(context, tiexp);
        }
        dn2.close(context);
    }
}
