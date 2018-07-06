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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.expression.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.returncodes.*;

import java.util.Vector;

public class TemplateInstancesSpecification {

    // Construction -------------------------------------------------------------------

    public TemplateInstancesSpecification (CallContext context) {
    }

    // --------------------------------------------------------------------------------

    protected String instance_package;

    public String getInstancePackage (CallContext context) {
        return this.instance_package;
    }

    public void setInstancePackage (CallContext context, String instance_package) {
        this.instance_package = instance_package;
    }

    protected String template_package_path;

    public String getTemplatePackagePath (CallContext context) {
        return this.template_package_path;
    }

    public void setTemplatePackagePath (CallContext context, String template_package_path) {
        this.template_package_path = template_package_path;
    }

    protected JavaTemplateTraits traits;

    public JavaTemplateTraits getTraits (CallContext context) {
        return this.traits;
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

    protected Vector_String_long_ instance_specifications;

    public Vector_String_long_ getInstanceSpecifications (CallContext context) {
        if (this.template_instance_registry != null && this.template_instance_registry.getInstanceSpecifications(context) != null) {
            return new VectorUnion_String_long_(context, this.instance_specifications, this.template_instance_registry.getInstanceSpecifications(context));
        } else {
            return this.instance_specifications;
        }
    }

    public void setInstanceSpecifications (CallContext context, Vector_String_long_ instance_specifications) {
        this.instance_specifications = instance_specifications;
    }

    protected TemplateInstanceRegistry template_instance_registry;

    public TemplateInstanceRegistry getTemplateInstanceRegistry (CallContext context) {
        if (this.template_instance_registry == null) {
            this.template_instance_registry = new TemplateInstanceRegistry(context);
        }
        return this.template_instance_registry;
    }
}
