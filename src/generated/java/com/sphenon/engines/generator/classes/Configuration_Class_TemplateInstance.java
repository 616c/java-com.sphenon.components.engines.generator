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
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.configuration.*;

public class Configuration_Class_TemplateInstance implements com.sphenon.engines.generator.classes.Class_TemplateInstance.Config {

    protected Configuration configuration;

    protected Configuration_Class_TemplateInstance (CallContext context) {
        configuration = Configuration.create(context, "com.sphenon.engines.generator.classes.Class_TemplateInstance");
    }

    static public Configuration_Class_TemplateInstance get (CallContext context) {
        return new Configuration_Class_TemplateInstance(context);
    }

    public java.lang.String getTemplateInstancePath(CallContext context) {
        String entry = "TemplateInstancePath";
        return configuration.get(context, entry, (java.lang.String) null);
    }

    public void setTemplateInstancePath(CallContext context, java.lang.String template_instance_path) {
        configuration.set(context, "TemplateInstancePath", template_instance_path);
    }
}
